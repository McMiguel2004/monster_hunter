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
            String insertSubDescripcion = "INSERT INTO SubDescripcion (MonstruoID, SubDescripcionID, Descripcion) VALUES (?, ?, ?)";
            PreparedStatement preparedStatementMonstruo = connection.prepareStatement(insertMonstruo, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement preparedStatementSubDescripcion = connection.prepareStatement(insertSubDescripcion);

            int subDescripcionID = 1; // Inicializar el contador de SubDescripcionID fuera del bucle principal

            for (int i = 0; i < monstruoList.getLength(); i++) {
                Node monstruoNode = monstruoList.item(i);

                if (monstruoNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element monstruoElement = (Element) monstruoNode;

                    String nombre = monstruoElement.getElementsByTagName("Nombre").item(0).getTextContent();
                    String imagen = monstruoElement.getElementsByTagName("Imagen").item(0).getTextContent();
                    String descripcion = monstruoElement.getElementsByTagName("Descripcion").item(0).getTextContent();

                    // Después de insertar la información del monstruo, obtener el ID del monstruo recién insertado
                    preparedStatementMonstruo.setString(1, nombre);
                    preparedStatementMonstruo.setString(2, imagen);
                    preparedStatementMonstruo.setString(3, descripcion);
                    preparedStatementMonstruo.executeUpdate();

                    // Obtener el ID del monstruo insertado
                    ResultSet generatedKeys = preparedStatementMonstruo.getGeneratedKeys();
                    int monstruoID;
                    if (generatedKeys.next()) {
                        monstruoID = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Error al obtener el ID del monstruo insertado.");
                    }

                    // Insertar datos en la tabla SubDescripcion utilizando el ID del monstruo
                    NodeList subDescripcionList = monstruoElement.getElementsByTagName("SubDescripcion");
                    for (int j = 0; j < subDescripcionList.getLength(); j++) {
                        String subDescripcion = subDescripcionList.item(j).getTextContent();
                        preparedStatementSubDescripcion.setInt(1, monstruoID);
                        preparedStatementSubDescripcion.setInt(2, subDescripcionID++);
                        preparedStatementSubDescripcion.setString(3, subDescripcion);
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

    public static void selectElementsByText(String searchText) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionFactory.getInstance().connect();

            // Consulta SQL para seleccionar elementos que contengan un texto concreto
            String query = "SELECT * FROM Monstruos WHERE Descripcion LIKE ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            // Procesar y mostrar resultados
            while (resultSet.next()) {
                // Recuperar datos
                int monstruoID = resultSet.getInt("MonstruoID");
                String nombre = resultSet.getString("Nombre");
                String imagen = resultSet.getString("Imagen");
                String descripcion = resultSet.getString("Descripcion");

                // Mostrar resultados
                System.out.println("MonstruoID: " + monstruoID);
                System.out.println("Nombre: " + nombre);
                System.out.println("Imagen: " + imagen);
                System.out.println("Descripcion: " + descripcion);
                System.out.println("-------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                ConnectionFactory.getInstance().disconnect();
            }
        }
    }

    // Modificar la descripción de un monstruo por su ID
    public static void updateMonstruoDescription(int monstruoID, String nuevaDescripcion) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = ConnectionFactory.getInstance().connect();

            // Logs de depuración
            System.out.println("Actualizando descripción del monstruo con ID: " + monstruoID);
            System.out.println("Nueva descripción: " + nuevaDescripcion);

            // Consulta SQL para actualizar la descripción del monstruo
            String updateQuery = "UPDATE Monstruos SET Descripcion = ? WHERE MonstruoID = ?";
            preparedStatement = connection.prepareStatement(updateQuery);

            // Establecer los parámetros en la consulta
            preparedStatement.setString(1, nuevaDescripcion);
            preparedStatement.setInt(2, monstruoID);

            // Logs de depuración
            System.out.println("Consulta SQL: " + preparedStatement.toString());

            // Ejecutar la actualización
            int filasAfectadas = preparedStatement.executeUpdate();

            // Verificar si la actualización fue exitosa
            if (filasAfectadas > 0) {
                System.out.println("Descripción del monstruo actualizada exitosamente.");
            } else {
                System.out.println("No se encontró el monstruo con ID " + monstruoID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                ConnectionFactory.getInstance().disconnect();
            }
        }
    }


}





