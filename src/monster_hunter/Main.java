package monster_hunter;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		boolean exit = false;
		Connection connection = null;

		while (!exit) {
			System.out.println("1. Borrar tablas e información");
			System.out.println("2. Crear tablas");
			System.out.println("3. Poblar tablas desde archivos");
			System.out.println("4. Consultar elementos por texto");
			System.out.println("5. Modificar descripción de un monstruo por ID");
			System.out.println("0. Salir");
			System.out.print("Selecciona una opción: ");

			int choice = scanner.nextInt();
			scanner.nextLine(); // Consumir el salto de línea

			switch (choice) {
				case 1:
					connection = ConnectionFactory.getInstance().connect();
					TablaController.dropTables();
					break;
				case 2:
					connection = ConnectionFactory.getInstance().connect();
					TablaController.createTables();
					break;

				case 3:
					connection = ConnectionFactory.getInstance().connect();
					TablaController.populateFromXML("monstruos.xml");
					break;

				case 4:
					connection = ConnectionFactory.getInstance().connect();
					System.out.print("Ingrese el texto de búsqueda: ");
					String searchText = scanner.nextLine();
					TablaController.selectElementsByText(searchText);
					break;

				case 5:
					connection = ConnectionFactory.getInstance().connect();
					System.out.println("Introduce el ID del monstruo a modificar:");
					int monstruoID;
					try {
						monstruoID = Integer.parseInt(scanner.nextLine());
					} catch (NumberFormatException e) {
						System.out.println("Entrada no válida. El ID debe ser un número entero.");
						break;
					}
					System.out.println("Introduce la nueva descripción:");
					String nuevaDescripcion = scanner.nextLine();
					TablaController.updateMonstruoDescription(monstruoID, nuevaDescripcion);
					break;


				case 0:
					exit = true;
					break;
				default:
					System.out.println("Opción no válida. Inténtalo de nuevo.");
			}


			// Cerrar la conexión después de realizar la operación
			if (connection != null) {
				ConnectionFactory.getInstance().disconnect();
			}
		}

		scanner.close();
	}
}