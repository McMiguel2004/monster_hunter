package nuevointento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Controlador encargado de las operaciones de modificación en la base de datos.
 */
public class ModificationController {
    /**
     * Modifica la descripción de un monstruo por su ID.
     *
     * @param entityId      ID del monstruo a modificar.
     * @param newDescription Nueva descripción del monstruo.
     */
    public static void modifyMonstruoDescriptionById(int entityId, String newDescription) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = ConnectionFactory.getInstance().connect();

            // Actualiza la descripción del monstruo
            String updateMonstruoQuery = "UPDATE Monstruo SET Descripcion = ? WHERE ID = ?";
            preparedStatement = connection.prepareStatement(updateMonstruoQuery);
            preparedStatement.setString(1, newDescription);
            preparedStatement.setInt(2, entityId);
            preparedStatement.executeUpdate();

            System.out.println("Descripción del monstruo actualizada correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al ejecutar la actualización: " + e.getMessage());
        } finally {
            // Cierra los recursos
            closeResources(preparedStatement, connection);
        }
    }
    /**
     * Modifica el nombre de un monstruo por su ID.
     *
     * @param entityId ID del monstruo a modificar.
     * @param newName   Nuevo nombre del monstruo.
     */

    public static void modifyMonstruoNameById(int entityId, String newName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = ConnectionFactory.getInstance().connect();

            // Actualiza el nombre del monstruo
            String updateMonstruoNameQuery = "UPDATE Monstruo SET Nombre = ? WHERE ID = ?";
            preparedStatement = connection.prepareStatement(updateMonstruoNameQuery);
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, entityId);
            preparedStatement.executeUpdate();

            System.out.println("Nombre del monstruo actualizado correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al ejecutar la actualización: " + e.getMessage());
        } finally {
            // Cierra los recursos
            closeResources(preparedStatement, connection);
        }
    }
    /**
     * Modifica el nombre de la ubicación de un monstruo por su ID.
     *
     * @param entityId ID del monstruo cuya ubicación se va a modificar.
     * @param newName   Nuevo nombre de la ubicación del monstruo.
     */
    public static void modifyLocationNameById(int entityId, String newName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = ConnectionFactory.getInstance().connect();

            // Actualiza el nombre en la tabla Location
            String updateLocationQuery = "UPDATE Location SET Name = ? WHERE ID = ?";
            preparedStatement = connection.prepareStatement(updateLocationQuery);
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, entityId);
            preparedStatement.executeUpdate();

            System.out.println("Nombre en la tabla Location actualizado correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al ejecutar la actualización: " + e.getMessage());
        } finally {
            // Cierra los recursos
            closeResources(preparedStatement, connection);
        }
    }

    /**
     * Modifica el nombre, la descripción y la ubicación de un monstruo por su ID.
     *
     * @param entityId       ID del monstruo a modificar.
     * @param newNombre      Nuevo nombre del monstruo.
     * @param newDescripcion Nueva descripción del monstruo.
     * @param newLocationName Nuevo nombre de la ubicación del monstruo.
     */

    public static void modifyMonstruoDetails(int entityId, String newNombre, String newDescripcion, String newLocationName) {
        Connection connection = null;
        PreparedStatement updateMonstruo = null;
        PreparedStatement updateLocation = null;

        try {
            connection = ConnectionFactory.getInstance().connect();

            // Actualizar el nombre y la descripción del monstruo
            String updateMonstruoQuery = "UPDATE Monstruo SET Nombre = ?, Descripcion = ? WHERE ID = ?";
            updateMonstruo = connection.prepareStatement(updateMonstruoQuery);
            updateMonstruo.setString(1, newNombre);
            updateMonstruo.setString(2, newDescripcion);
            updateMonstruo.setInt(3, entityId);
            updateMonstruo.executeUpdate();

            // Actualizar el nombre de la ubicación del monstruo
            String updateLocationQuery = "UPDATE Location SET Name = ? WHERE Monstruo_ID = ?";
            updateLocation = connection.prepareStatement(updateLocationQuery);
            updateLocation.setString(1, newLocationName);
            updateLocation.setInt(2, entityId);
            updateLocation.executeUpdate();

            System.out.println("Detalles del monstruo actualizados correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al ejecutar la actualización: " + e.getMessage());
        } finally {
            // Cierra los recursos
            closeResources(updateMonstruo, connection);
        }
    }

    /**
     * Cierra los recursos utilizados en las operaciones de modificación.
     *
     * @param preparedStatement PreparedStatement a cerrar.
     * @param connection        Conexión a la base de datos a cerrar.
     */
    private static void closeResources(PreparedStatement preparedStatement, Connection connection) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
