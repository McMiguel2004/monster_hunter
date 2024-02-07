package nuevointento;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Clase principal que contiene el método main para la ejecución del programa.
 */

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		boolean exit = false;
		Connection connection = null;

		while (!exit) {
			System.out.println("1. Borrar tablas e información");
			System.out.println("2. Crear tablas");
			System.out.println("3. Poblar tablas desde archivos");
			System.out.println("");

			System.out.println("4. Consultar cualquier monstruo de la especie (CONDICIóN)");
			System.out.println("5. Consultar cualquier monstruo que contenga la  (CONDICIóN)");
			System.out.println("");

			System.out.println("6. Modificar descripción de un monstruo por ID");
			System.out.println("7. Modificar nombre de un monstruo por ID");
			System.out.println("8. Modificar el nombre de la localización de un monstruo por ID");
			System.out.println("9. Modificar por id el nombre, descripción y localización de un monstruo");
			System.out.println("");

			System.out.println("10. Eliminar un monstruo por id");
			System.out.println("11. Eliminar vairos monstruo por id");

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
					consultMonstruosByEspecie(connection);
					break;
				case 5:
					connection = ConnectionFactory.getInstance().connect();
					consultElementsContainingText(connection);
					break;
				case 6:
					connection = ConnectionFactory.getInstance().connect();
					modifyMonstruoDescriptionById(connection);
					break;
				case 7:
					connection = ConnectionFactory.getInstance().connect();
					modifyMonstruoName(connection);
					break;
				case 8:
					connection = ConnectionFactory.getInstance().connect();
					modifyLocationNameById(connection);
					break;
				case 9:
					connection = ConnectionFactory.getInstance().connect();
					modifyMonstruoDetails(connection);
					break;

				case 10:
					connection = ConnectionFactory.getInstance().connect();
					deleteMonstruoById(connection);
					break;

				case 11:
					connection = ConnectionFactory.getInstance().connect();
					deleteMultipleMonstruos(connection);
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

// En Main.java

	private static void consultMonstruosByEspecie(Connection connection) {
		System.out.println("");

		System.out.println("Posibles especies: Brute Wyverns - Elder Dragons - Flying Wyverns - Piscine Wyverns - Bird Wyverns - Fanged Wyverns");
		System.out.print("Introduce la especie para consultar monstruos: ");
		Scanner scanner = new Scanner(System.in);
		String especie = scanner.nextLine();
		SelectController.selectMonstruosByEspecie(especie);
	}

	private static void consultElementsContainingText(Connection connection) {
		System.out.println("");
		System.out.println("Posibles elementos: Dragonblight - Iceblight - Thunderblight - Fireblight - Waterblight");
		System.out.println("Posibles estados: Health decreases over time -  Inflicts Poison Status on the target, slowly draining their health -  Increased Stamina usage - Reduced Stamina recovery -  Weapon Elemental and Status Damage nullified -  Health decreases over time");
		System.out.println("");
		System.out.print("Introduce el texto para consultar en la tabla elements: ");
		Scanner scanner = new Scanner(System.in);
		String searchText = scanner.nextLine();
		SelectController.selectElementsContainingText(searchText);
	}

	private static void modifyMonstruoDescriptionById(Connection connection) {
		System.out.println("");
		System.out.print("Introduce el ID del monstruo cuya descripción deseas modificar: ");
		Scanner scanner = new Scanner(System.in);
		int monstruoId = scanner.nextInt();
		scanner.nextLine(); // Consumir el salto de línea

		System.out.println("");
		System.out.print("Introduce la nueva descripción: ");
		String nuevaDescripcion = scanner.nextLine();

		ModificationController.modifyMonstruoDescriptionById(monstruoId, nuevaDescripcion);
	}

	private static void modifyMonstruoName(Connection connection) {
		System.out.println("");
		System.out.print("Introduce la ID del monstruo para modificar su nombre: ");
		Scanner scanner = new Scanner(System.in);
		int monstruoId = scanner.nextInt();
		scanner.nextLine(); // Consumir el salto de línea

		System.out.println("");
		System.out.print("Introduce el nuevo nombre del monstruo: ");
		String newNombre = scanner.nextLine();

		ModificationController.modifyMonstruoNameById(monstruoId, newNombre);
	}

	// Método para modificar el nombre en la tabla Location por ID
	private static void modifyLocationNameById(Connection connection) {
		System.out.println("");
		System.out.print("Introduce el ID en la tabla Location para modificar el nombre: ");
		Scanner scanner = new Scanner(System.in);
		int entityId = scanner.nextInt();
		scanner.nextLine(); // Consumir el salto de línea

		System.out.println("");
		System.out.print("Introduce el nuevo nombre en la tabla Location: ");
		String newName = scanner.nextLine();

		ModificationController.modifyLocationNameById(entityId, newName);
	}

	private static void modifyMonstruoDetails(Connection connection) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Introduce la ID del monstruo a modificar: ");
		int entityId = scanner.nextInt();
		scanner.nextLine();

		System.out.print("Introduce el nuevo nombre del monstruo: ");
		String newNombre = scanner.nextLine();

		System.out.print("Introduce la nueva descripción del monstruo: ");
		String newDescripcion = scanner.nextLine();

		System.out.print("Introduce el nuevo nombre de la ubicación: ");
		String newLocationName = scanner.nextLine();

		ModificationController.modifyMonstruoDetails(entityId, newNombre, newDescripcion, newLocationName);
	}
	private static void deleteMonstruoById(Connection connection) {
		System.out.print("Introduce el ID del monstruo a eliminar: ");
		Scanner scanner = new Scanner(System.in);
		int entityId = scanner.nextInt();
		DeleteController.deleteMonstruoById(entityId);
	}

	private static void deleteMultipleMonstruos(Connection connection) {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Introduce la cantidad de monstruos que deseas eliminar:");
		int numMonstruos = scanner.nextInt();
		scanner.nextLine(); // Consumir el salto de línea

		List<Integer> idsToDelete = new ArrayList<>();

		for (int i = 0; i < numMonstruos; i++) {
			System.out.print("Introduce el ID del monstruo #" + (i + 1) + ": ");
			int id = scanner.nextInt();
			scanner.nextLine(); // Consumir el salto de línea
			idsToDelete.add(id);
		}

		DeleteController.deleteMonstruosByIds(idsToDelete);

		// Cerrar la conexión después de realizar la operación
		if (connection != null) {
			ConnectionFactory.getInstance().disconnect();
		}
	}



}
