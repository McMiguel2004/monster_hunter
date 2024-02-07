package nuevointento;// En SelectController.java

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Controlador encargado de las operaciones de consulta en la base de datos.
 */
public class SelectController {

    // Consulta para seleccionar elementos de la tabla Elements que cumplen una condición
    private static final String SELECT_ELEMENTS_WITH_CONDITION = "SELECT * FROM Elements WHERE %s";

    // Consulta para seleccionar la ID y el nombre de los monstruos por especie
    private static final String SELECT_MONSTRUOS_BY_ESPECIE = "SELECT id, nombre FROM Monstruo WHERE speciesname = ?";

    private static final String SELECT_ELEMENTS_CONTAINING_TEXT_WITH_MONSTER_NAME =
            "SELECT E.id, E.element_name, E.monstruo_id, M.nombre AS monstruo_nombre " +
                    "FROM Elements E " +
                    "JOIN Monstruo M ON E.monstruo_id = M.id " +
                    "WHERE E.element_name LIKE ?";

    /**
     * Método para seleccionar la ID y el nombre de los monstruos por especie.
     *
     * @param especie Especie de los monstruos a consultar.
     */

    // Método para seleccionar la ID y el nombre de los monstruos por especie
    public static void selectMonstruosByEspecie(String especie) {
        String query = SELECT_MONSTRUOS_BY_ESPECIE;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionFactory.getInstance().connect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, especie);
            resultSet = preparedStatement.executeQuery();

            // Procesa y muestra los resultados según tus necesidades
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");

                System.out.println("ID: " + id + ", Nombre: " + nombre);
            }
        } catch (SQLException e) {
            System.err.println("Error al ejecutar la consulta: " + e.getMessage());
        } finally {
            // Cierra los recursos
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

    /**
     * Método para seleccionar elementos de la tabla Elements que contienen un texto específico.
     *
     * @param searchText Texto a buscar en los elementos.
     */
    // Método para seleccionar elementos de la tabla Elements que contienen un texto específico
    public static void selectElementsContainingText(String searchText) {
        String query = SELECT_ELEMENTS_CONTAINING_TEXT_WITH_MONSTER_NAME;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionFactory.getInstance().connect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchText + "%");
            resultSet = preparedStatement.executeQuery();

            // Procesa y muestra los resultados según tus necesidades
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String elementName = resultSet.getString("element_name");
                int monstruoId = resultSet.getInt("monstruo_id");
                String monstruoNombre = resultSet.getString("monstruo_nombre");

                System.out.println("ID: " + id + ", Element Name: " + elementName +
                        ", Monstruo ID: " + monstruoId + ", Monstruo Nombre: " + monstruoNombre);
            }
        } catch (SQLException e) {
            System.err.println("Error al ejecutar la consulta: " + e.getMessage());
        } finally {
            // Cierra los recursos
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

    /**
     * Método genérico para ejecutar una consulta y mostrar los resultados.
     *
     * @param query Consulta SQL a ejecutar.
     */

    // Método genérico para ejecutar una consulta y mostrar los resultados
    private static void executeQuery(String query) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionFactory.getInstance().connect();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            // Procesa y muestra los resultados según tus necesidades
            while (resultSet.next()) {
                // Aquí puedes extraer y mostrar los resultados
                // por ejemplo: resultSet.getString("columna_nombre");
            }
        } catch (SQLException e) {
            System.err.println("Error al ejecutar la consulta: " + e.getMessage());
        } finally {
            // Cierra los recursos
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

}
