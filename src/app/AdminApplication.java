package app;

import model.AdminCommandRequest;
import model.AdminCommandResponse;
import service.FileService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Console-based Administration Application.
 *
 * This application connects to the AdministrationService running inside
 * the Store Front application and sends administration commands over the
 * local network.
 *
 * Supported commands:
 * U = update inventory with a JSON payload
 * R = return all inventory as JSON
 * Q = quit the admin application
 */
public class AdminApplication {

    /**
     * Host used for the local administration connection.
     */
    private static final String HOST = "127.0.0.1";

    /**
     * Port used for the local administration connection.
     */
    private static final int PORT = 5050;

    /**
     * Program entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FileService fileService = new FileService();
        boolean running = true;

        System.out.println("Administration Application");

        while (running) {
            printMenu();
            System.out.print("Enter command: ");
            String command = scanner.nextLine().trim().toUpperCase();

            try {
                switch (command) {
                    case "U":
                        System.out.print("Enter JSON file path to send: ");
                        String filePath = scanner.nextLine().trim();

                        String jsonPayload = fileService.readInventoryJsonAsString(filePath);
                        AdminCommandRequest updateRequest = new AdminCommandRequest("U", jsonPayload);
                        AdminCommandResponse updateResponse = sendRequest(updateRequest);

                        System.out.println("Success: " + updateResponse.isSuccess());
                        System.out.println("Message: " + updateResponse.getMessage());
                        break;

                    case "R":
                        AdminCommandRequest readRequest = new AdminCommandRequest("R", "");
                        AdminCommandResponse readResponse = sendRequest(readRequest);

                        System.out.println("Success: " + readResponse.isSuccess());
                        System.out.println("Message: " + readResponse.getMessage());
                        System.out.println("Payload:");
                        System.out.println(readResponse.getPayload());
                        break;

                    case "Q":
                        running = false;
                        System.out.println("Closing Administration Application.");
                        break;

                    default:
                        System.out.println("Invalid command. Enter U, R, or Q.");
                        break;
                }
            } catch (Exception ex) {
                System.out.println("Admin error: " + ex.getMessage());
            }

            System.out.println();
        }

        scanner.close();
    }

    /**
     * Prints the administration command menu.
     */
    private static void printMenu() {
        System.out.println("Commands:");
        System.out.println("U = Update inventory from JSON file");
        System.out.println("R = Return all inventory as JSON");
        System.out.println("Q = Quit");
    }

    /**
     * Sends a request to the administration service and waits for a response.
     *
     * @param request request to send
     * @return response from the server
     * @throws Exception when communication or parsing fails
     */
    private static AdminCommandResponse sendRequest(AdminCommandRequest request) throws Exception {
        FileService fileService = new FileService();

        try (Socket socket = new Socket(HOST, PORT);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.write(fileService.toJson(request));
            writer.newLine();
            writer.flush();

            String responseJson = reader.readLine();
            return fileService.fromJson(responseJson, AdminCommandResponse.class);
        }
    }
}
