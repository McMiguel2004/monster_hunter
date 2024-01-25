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
			// ... Agrega más opciones según tus necesidades

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