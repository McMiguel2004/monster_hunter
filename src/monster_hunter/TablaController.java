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

    // Crear tablas
    public static void createTables() {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = ConnectionFactory.getInstance().connect();
            statement = connection.createStatement();

            // Definir las sentencias SQL para la creación de tablas
            String createMonstruosTable = "CREATE TABLE IF NOT EXISTS Monstruos ("
                    + "MonstruoID SERIAL PRIMARY KEY,"
                    + "Nombre VARCHAR(255) NOT NULL,"
                    + "Imagen VARCHAR(255) NOT NULL,"
                    + "Descripcion TEXT NOT NULL)";

            String createSubDescripcionTable = "CREATE TABLE IF NOT EXISTS SubDescripcion ("
                    + "SubDescripcionID SERIAL PRIMARY KEY,"
                    + "MonstruoID INT,"
                    + "Descripcion TEXT NOT NULL,"
                    + "FOREIGN KEY (MonstruoID) REFERENCES Monstruos(MonstruoID))";

            // Ejecutar las sentencias SQL
            statement.executeUpdate(createMonstruosTable);
            statement.executeUpdate(createSubDescripcionTable);

            System.out.println("Tablas creadas exitosamente.");
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

    // Eliminar tablas
    public static void dropTables() {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = ConnectionFactory.getInstance().connect();
            statement = connection.createStatement();

            // Definir las sentencias SQL para eliminar las tablas
            String dropMonstruosTable = "DROP TABLE IF EXISTS Monstruos";
            String dropSubDescripcionTable = "DROP TABLE IF EXISTS SubDescripcion";

            // Ejecutar las sentencias SQL
            statement.executeUpdate(dropSubDescripcionTable);
            statement.executeUpdate(dropMonstruosTable);

            System.out.println("Tablas eliminadas exitosamente.");
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

            String insertMonstruo = "INSERT INTO Monstruos (Nombre, Imagen, Descripcion) VALUES (?, ?, ?)";
            String insertSubDescripcion = "INSERT INTO SubDescripcion (MonstruoID, Descripcion) VALUES (?, ?)";
            PreparedStatement preparedStatementMonstruo = connection.prepareStatement(insertMonstruo);
            PreparedStatement preparedStatementSubDescripcion = connection.prepareStatement(insertSubDescripcion);

            for (int i = 0; i < monstruoList.getLength(); i++) {
                Node monstruoNode = monstruoList.item(i);

                if (monstruoNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element monstruoElement = (Element) monstruoNode;

                    String nombre = monstruoElement.getElementsByTagName("Nombre").item(0).getTextContent();
                    String imagen = monstruoElement.getElementsByTagName("Imagen").item(0).getTextContent();
                    String descripcion = monstruoElement.getElementsByTagName("Descripcion").item(0).getTextContent();

                    // Insertar datos en la tabla Monstruos
                    preparedStatementMonstruo.setString(1, nombre);
                    preparedStatementMonstruo.setString(2, imagen);
                    preparedStatementMonstruo.setString(3, descripcion);
                    preparedStatementMonstruo.executeUpdate();

                    // Obtener el ID del monstruo insertado
                    int monstruoID = getLastInsertedID(connection);

                    // Insertar datos en la tabla SubDescripcion
                    NodeList subDescripcionList = monstruoElement.getElementsByTagName("SubDescripcion");
                    for (int j = 0; j < subDescripcionList.getLength(); j++) {
                        String subDescripcion = subDescripcionList.item(j).getTextContent();
                        preparedStatementSubDescripcion.setInt(1, monstruoID);
                        preparedStatementSubDescripcion.setString(2, subDescripcion);
                        preparedStatementSubDescripcion.executeUpdate();
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

    // Método auxiliar para obtener el último ID insertado
    private static int getLastInsertedID(Connection connection) throws SQLException {
        int lastID = -1;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String query = "SELECT LASTVAL()";
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





