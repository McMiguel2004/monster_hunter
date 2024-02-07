package nuevointento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DeleteController {
    public static void deleteMonstruoById(int entityId) {
        Connection connection = null;
        PreparedStatement deleteElements = null;
        PreparedStatement deleteMonstruo = null;

        try {
            connection = ConnectionFactory.getInstance().connect();
            connection.setAutoCommit(false);  // Desactiva la confirmación automática

            // Elimina las filas relacionadas en la tabla "elements"
            String deleteElementsQuery = "DELETE FROM Elements WHERE monstruo_id = ?";
            deleteElements = connection.prepareStatement(deleteElementsQuery);
            deleteElements.setInt(1, entityId);
            deleteElements.executeUpdate();

            // Elimina el monstruo después de eliminar las referencias en "elements"
            String deleteMonstruoQuery = "DELETE FROM Monstruo WHERE id = ?";
            deleteMonstruo = connection.prepareStatement(deleteMonstruoQuery);
            deleteMonstruo.setInt(1, entityId);
            deleteMonstruo.executeUpdate();

            // Confirma los cambios en la base de datos
            connection.commit();
            System.out.println("Monstruo y referencias eliminadas correctamente.");

        } catch (SQLException e) {
            // Si hay un error, realiza un rollback para deshacer los cambios
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            System.err.println("Error al ejecutar la eliminación: " + e.getMessage());

        } finally {
            // Restaura la configuración de confirmación automática
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException autoCommitException) {
                autoCommitException.printStackTrace();
            }

            // Cierra los recursos
            closeResourcesForDeleteMonstruoById(connection, deleteElements, deleteMonstruo);
        }
    }

    public static void deleteMonstruosByIds(List<Integer> entityIds) {
        Connection connection = null;
        PreparedStatement deleteMonstruo = null;
        PreparedStatement deleteElements = null;
        PreparedStatement deleteLocation = null;

        try {
            connection = ConnectionFactory.getInstance().connect();
            connection.setAutoCommit(false);  // Desactiva la confirmación automática

            // Elimina las filas en "elements" relacionadas con los monstruos que se van a eliminar
            String deleteElementsQuery = "DELETE FROM Elements WHERE monstruo_id = ?";
            deleteElements = connection.prepareStatement(deleteElementsQuery);

            // Elimina los registros de Location relacionados con los monstruos que se van a eliminar
            String deleteLocationQuery = "DELETE FROM Location WHERE Monstruo_ID = ?";
            deleteLocation = connection.prepareStatement(deleteLocationQuery);

            // Elimina el monstruo para cada ID
            String deleteMonstruoQuery = "DELETE FROM Monstruo WHERE id = ?";
            deleteMonstruo = connection.prepareStatement(deleteMonstruoQuery);

            for (int entityId : entityIds) {
                // Elimina las filas en "elements" relacionadas con el monstruo
                deleteElements.setInt(1, entityId);
                deleteElements.executeUpdate();

                // Elimina los registros de Location relacionados con el monstruo
                deleteLocation.setInt(1, entityId);
                deleteLocation.executeUpdate();

                // Elimina el monstruo
                deleteMonstruo.setInt(1, entityId);
                deleteMonstruo.executeUpdate();
            }

            // Confirma los cambios en la base de datos
            connection.commit();
            System.out.println("Monstruos eliminados correctamente.");

        } catch (SQLException e) {
            // Si hay un error, realiza un rollback y muestra un mensaje de error
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            System.err.println("Error al ejecutar la eliminación: " + e.getMessage());

        } finally {
            // Restaura la configuración de confirmación automática
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException autoCommitException) {
                autoCommitException.printStackTrace();
            }

            // Cierra los recursos
            closeResourcesForDeleteMonstruosByIds(connection, deleteElements, deleteLocation, deleteMonstruo);
        }
    }



    private static void closeResourcesForDeleteMonstruoById(Connection connection, PreparedStatement preparedStatement1, PreparedStatement preparedStatement2) {
        try {
            if (preparedStatement1 != null) {
                preparedStatement1.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection(connection);
    }

    private static void closeResourcesForDeleteMonstruosByIds(Connection connection, PreparedStatement preparedStatement1, PreparedStatement preparedStatement2, PreparedStatement preparedStatement3) {
        try {
            if (preparedStatement1 != null) {
                preparedStatement1.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (preparedStatement3 != null) {
                preparedStatement3.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection(connection);
    }



    // Método para cerrar la conexión
    private static void closeConnection(Connection connection) {
        if (connection != null) {
            ConnectionFactory.getInstance().disconnect();
        }
    }
}
