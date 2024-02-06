package monster_hunter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.*;

public class TablaController {

    public static void createTables() {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = ConnectionFactory.getInstance().connect();
            statement = connection.createStatement();

            // Crear tablas en el orden correcto
            String createSpeciesTable = "CREATE TABLE IF NOT EXISTS Species ("
                    + "id SERIAL PRIMARY KEY,"
                    + "name VARCHAR(255) NOT NULL,"
                    + "description TEXT NOT NULL,"
                    + "monstruo_id INT REFERENCES Monstruo(id))";

            String createLocationTable = "CREATE TABLE IF NOT EXISTS Location ("
                    + "id SERIAL PRIMARY KEY,"
                    + "name VARCHAR(255) NOT NULL,"
                    + "description TEXT NOT NULL,"
                    + "monstruo_id INT REFERENCES Monstruo(id))";

            String createMonstruoTable = "CREATE TABLE IF NOT EXISTS Monstruo ("
                    + "id SERIAL PRIMARY KEY,"
                    + "nombre VARCHAR(255) NOT NULL,"
                    + "imagen VARCHAR(255) NOT NULL,"
                    + "descripcion TEXT NOT NULL,"
                    + "species_id INT REFERENCES Species(id),"
                    + "location_id INT REFERENCES Location(id))";

            String createElementsTable = "CREATE TABLE IF NOT EXISTS Elements ("
                    + "id SERIAL PRIMARY KEY,"
                    + "element_name VARCHAR(255) NOT NULL,"
                    + "monstruo_id INT REFERENCES Monstruo(id))";

            // Ejecutar las sentencias SQL en el orden correcto
            statement.executeUpdate(createSpeciesTable);
            statement.executeUpdate(createLocationTable);
            statement.executeUpdate(createMonstruoTable);
            statement.executeUpdate(createElementsTable);

            System.out.println("Tablas creadas exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        } finally {
            // Cerrar recursos
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar statement: " + e.getMessage());
                }
            }
            if (connection != null) {
                ConnectionFactory.getInstance().disconnect();
            }
        }
    }





    // Eliminar todas las tablas
    public static void dropTables() {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = ConnectionFactory.getInstance().connect();
            statement = connection.createStatement();

            // Definir las sentencias SQL para eliminar todas las tablas
            String dropElementsTable = "DROP TABLE IF EXISTS Elements";
            String dropSpeciesTable = "DROP TABLE IF EXISTS Species";
            String dropLocationTable = "DROP TABLE IF EXISTS Location";
            String dropMonstruoTable = "DROP TABLE IF EXISTS Monstruo CASCADE"; // Agregar CASCADE

            // Ejecutar las sentencias SQL
            statement.executeUpdate(dropElementsTable);
            statement.executeUpdate(dropSpeciesTable);
            statement.executeUpdate(dropLocationTable);
            statement.executeUpdate(dropMonstruoTable);

            System.out.println("Todas las tablas han sido eliminadas exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                ConnectionFactory.getInstance().disconnect();
            }
        }
    }


    public static void populateFromXML(String xmlFilePath) {
        Connection connection = null;
        try {
            connection = ConnectionFactory.getInstance().connect();

            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList monstruoList = doc.getElementsByTagName("Monstruo");

            String insertMonstruo = "INSERT INTO Monstruo (nombre, imagen, descripcion, species_id, location_id) VALUES (?, ?, ?, ?, ?)";
            String insertSpecies = "INSERT INTO Species (name, description) VALUES (?, ?)";
            String insertElements = "INSERT INTO Elements (monstruo_id, element_name) VALUES (?, ?)";
            String insertLocation = "INSERT INTO Location (name, description) VALUES (?, ?)";

            PreparedStatement preparedStatementMonstruo = connection.prepareStatement(insertMonstruo);
            PreparedStatement preparedStatementSpecies = connection.prepareStatement(insertSpecies, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement preparedStatementElements = connection.prepareStatement(insertElements);
            PreparedStatement preparedStatementLocation = connection.prepareStatement(insertLocation);

            int speciesID;
            int locationID;

            for (int i = 0; i < monstruoList.getLength(); i++) {
                Node monstruoNode = monstruoList.item(i);

                if (monstruoNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element monstruoElement = (Element) monstruoNode;

                    String nombre = monstruoElement.getElementsByTagName("Nombre").item(0).getTextContent();
                    String imagen = monstruoElement.getElementsByTagName("Imagen").item(0).getTextContent();
                    String descripcion = monstruoElement.getElementsByTagName("Descripcion").item(0).getTextContent();

                    // Obtener o insertar datos en la tabla Species
                    Element speciesElement = (Element) monstruoElement.getElementsByTagName("Species").item(0);
                    String speciesName = speciesElement.getElementsByTagName("name").item(0).getTextContent();
                    String speciesDescription = speciesElement.getElementsByTagName("descripcion").item(0).getTextContent();
                    speciesID = insertSpeciesAndGetID(connection, speciesName, speciesDescription);

                    // Obtener o insertar datos en la tabla Location
                    Element locationElement = (Element) monstruoElement.getElementsByTagName("location").item(0);
                    String locationName = locationElement.getElementsByTagName("name").item(0).getTextContent();
                    String locationDescription = locationElement.getElementsByTagName("description").item(0).getTextContent();
                    locationID = insertLocationAndGetID(connection, locationName, locationDescription);

                    // Insertar datos en la tabla Monstruo
                    preparedStatementMonstruo.setString(1, nombre);
                    preparedStatementMonstruo.setString(2, imagen);
                    preparedStatementMonstruo.setString(3, descripcion);
                    preparedStatementMonstruo.setInt(4, speciesID);
                    preparedStatementMonstruo.setInt(5, locationID);
                    preparedStatementMonstruo.executeUpdate();

                    // Obtener el ID del monstruo insertado
                    int monstruoID = getLastInsertedID(connection);

                    // Insertar datos en la tabla Elements
                    Element elementsElement = (Element) monstruoElement.getElementsByTagName("Elements").item(0);
                    NodeList elementList = elementsElement.getElementsByTagName("element");
                    for (int j = 0; j < elementList.getLength(); j++) {
                        String elementName = elementList.item(j).getTextContent();
                        preparedStatementElements.setInt(1, monstruoID);
                        preparedStatementElements.setString(2, elementName);
                        preparedStatementElements.executeUpdate();
                    }
                }
            }

            System.out.println("Datos insertados desde XML exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al poblar desde XML: " + e.getMessage());
        } finally {
            // Cerrar recursos
            if (connection != null) {
                ConnectionFactory.getInstance().disconnect();
            }
        }
    }


    // Método auxiliar para insertar o obtener el ID de una especie
    private static int insertSpeciesAndGetID(Connection connection, String name, String description) throws SQLException {
        String selectSpecies = "SELECT id FROM Species WHERE name = ?";
        String insertSpecies = "INSERT INTO Species (name, description) VALUES (?, ?)";

        PreparedStatement preparedStatementSelect = connection.prepareStatement(selectSpecies);
        preparedStatementSelect.setString(1, name);

        ResultSet resultSet = preparedStatementSelect.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("id");
        } else {
            PreparedStatement preparedStatementInsert = connection.prepareStatement(insertSpecies, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, name);
            preparedStatementInsert.setString(2, description);
            preparedStatementInsert.executeUpdate();

            ResultSet generatedKeys = preparedStatementInsert.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Error al obtener el ID de Species insertado.");
            }
        }
    }

    // Método auxiliar para insertar o obtener el ID de una ubicación
    private static int insertLocationAndGetID(Connection connection, String name, String description) throws SQLException {
        String selectLocation = "SELECT id FROM Location WHERE name = ?";
        String insertLocation = "INSERT INTO Location (name, description) VALUES (?, ?)";

        PreparedStatement preparedStatementSelect = connection.prepareStatement(selectLocation);
        preparedStatementSelect.setString(1, name);

        ResultSet resultSet = preparedStatementSelect.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("id");
        } else {
            PreparedStatement preparedStatementInsert = connection.prepareStatement(insertLocation, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, name);
            preparedStatementInsert.setString(2, description);
            preparedStatementInsert.executeUpdate();

            ResultSet generatedKeys = preparedStatementInsert.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Error al obtener el ID de Location insertado.");
            }
        }
    }

    // Método auxiliar para obtener el último ID insertado
    private static int getLastInsertedID(Connection connection) throws SQLException {
        int lastID = -1;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String query = "SELECT lastval()";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                lastID = resultSet.getInt(1);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }

        return lastID;
    }

}





