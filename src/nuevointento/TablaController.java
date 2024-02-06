package nuevointento;

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

            // Crear tabla Monstruo
            // Crear tabla Monstruo
            String createMonstruoTable = "CREATE TABLE IF NOT EXISTS Monstruo ("
                    + "id SERIAL PRIMARY KEY,"
                    + "Nombre VARCHAR(255) NOT NULL,"
                    + "Imagen VARCHAR(255) NOT NULL,"
                    + "Descripcion TEXT NOT NULL,"
                    + "SpeciesName VARCHAR(255),"
                    + "SpeciesDescripcion TEXT)";


            // Crear tabla Elements
            String createElementsTable = "CREATE TABLE IF NOT EXISTS Elements ("
                    + "id SERIAL PRIMARY KEY,"
                    + "element_name VARCHAR(255) NOT NULL,"
                    + "monstruo_id INT REFERENCES Monstruo(id))";

            // Crear tabla Location
            String createLocationTable = "CREATE TABLE IF NOT EXISTS Location ("
                    + "id SERIAL PRIMARY KEY,"
                    + "Name VARCHAR(255) NOT NULL,"
                    + "descripcion TEXT NOT NULL,"
                    + "monstruo_id INT REFERENCES Monstruo(id))";

            // Ejecutar las sentencias SQL en el orden correcto
            statement.executeUpdate(createMonstruoTable);
            statement.executeUpdate(createElementsTable);
            statement.executeUpdate(createLocationTable);

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
            String dropLocationTable = "DROP TABLE IF EXISTS Location";
            String dropMonstruoTable = "DROP TABLE IF EXISTS Monstruo CASCADE"; // Agregar CASCADE

            // Ejecutar las sentencias SQL
            statement.executeUpdate(dropElementsTable);
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

            String insertMonstruo = "INSERT INTO Monstruo (Nombre, Imagen, Descripcion, SpeciesName, SpeciesDescripcion) VALUES (?, ?, ?, ?, ?)";
            String insertElements = "INSERT INTO Elements (Monstruo_ID, element_name) VALUES (?, ?)";
            String insertLocation = "INSERT INTO Location (Monstruo_ID, Name, Descripcion) VALUES (?, ?, ?)";

            PreparedStatement preparedStatementMonstruo = connection.prepareStatement(insertMonstruo, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement preparedStatementElements = connection.prepareStatement(insertElements);
            PreparedStatement preparedStatementLocation = connection.prepareStatement(insertLocation);

            for (int i = 0; i < monstruoList.getLength(); i++) {
                Node monstruoNode = monstruoList.item(i);

                if (monstruoNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element monstruoElement = (Element) monstruoNode;

                    String nombre = getElementTextContent(monstruoElement, "Nombre");
                    String imagen = getElementTextContent(monstruoElement, "Imagen");
                    String descripcion = getElementTextContent(monstruoElement, "Descripcion");

                    // Obtener o insertar datos en la tabla Species
                    Element speciesElement = (Element) monstruoElement.getElementsByTagName("Species").item(0);
                    String speciesName = getElementTextContent(speciesElement, "name");
                    String speciesDescription = getElementTextContent(speciesElement, "descripcion");

                    // Insertar datos en la tabla Monstruos
                    preparedStatementMonstruo.setString(1, nombre);
                    preparedStatementMonstruo.setString(2, imagen);
                    preparedStatementMonstruo.setString(3, descripcion);
                    preparedStatementMonstruo.setString(4, speciesName);
                    preparedStatementMonstruo.setString(5, speciesDescription);
                    preparedStatementMonstruo.executeUpdate();

                    // Obtener el ID del monstruo insertado
                    ResultSet generatedKeysMonstruo = preparedStatementMonstruo.getGeneratedKeys();
                    int monstruoID;
                    if (generatedKeysMonstruo.next()) {
                        monstruoID = generatedKeysMonstruo.getInt(1);

                        // Insertar datos en la tabla Elements si existen elementos
                        try {
                            insertElementsData(monstruoElement, preparedStatementElements, monstruoID);
                        } catch (SQLException e) {
                            System.err.println("Error al insertar elementos para el monstruo con ID " + monstruoID + ": " + e.getMessage());
                        }

                        // Insertar datos en la tabla Location si existe el elemento "location"
                        try {
                            insertLocationData(monstruoElement, preparedStatementLocation, monstruoID);
                        } catch (SQLException e) {
                            System.err.println("Error al insertar ubicación para el monstruo con ID " + monstruoID + ": " + e.getMessage());
                        }
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

    private static String getElementTextContent(Element parentElement, String tagName) {
        if (parentElement != null) {
            NodeList nodeList = parentElement.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                return nodeList.item(0).getTextContent();
            }
        }
        return null;
    }

    private static void insertElementsData(Element monstruoElement, PreparedStatement preparedStatementElements, int monstruoID) throws SQLException {
        NodeList elementsList = monstruoElement.getElementsByTagName("Elements");
        if (elementsList.getLength() > 0) {
            Element elementsElement = (Element) elementsList.item(0);
            NodeList elementList = elementsElement.getChildNodes(); // Obtener todos los hijos de Elements
            for (int j = 0; j < elementList.getLength(); j++) {
                Node elementNode = elementList.item(j);
                if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
                    String elementName = elementNode.getTextContent();
                    preparedStatementElements.setInt(1, monstruoID);
                    preparedStatementElements.setString(2, elementName);
                    preparedStatementElements.executeUpdate();
                }
            }
        } else {
            System.out.println("Advertencia: No se encontraron elementos 'Elements' para este monstruo.");
        }
    }


    private static void insertLocationData(Element monstruoElement, PreparedStatement preparedStatementLocation, int monstruoID) throws SQLException {
        NodeList locationList = monstruoElement.getElementsByTagName("location");
        if (locationList.getLength() > 0) {
            Element locationElement = (Element) locationList.item(0);
            String locationName = getElementTextContent(locationElement, "name");
            String locationDescription = getElementTextContent(locationElement, "description");
            preparedStatementLocation.setInt(1, monstruoID);
            preparedStatementLocation.setString(2, locationName);
            preparedStatementLocation.setString(3, locationDescription);
            preparedStatementLocation.executeUpdate();
        } else {
            System.out.println("Advertencia: No se encontró elemento 'location' para este monstruo.");
        }
    }


}



