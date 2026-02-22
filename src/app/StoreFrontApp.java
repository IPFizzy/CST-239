package app;

import model.SalableProduct;
import service.StoreFront;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Console driver for the Store Front milestone.
 *
 * This class is intentionally lightweight. Its job is to:
 * 1) Show a menu
 * 2) Collect user input
 * 3) Call StoreFront methods to perform actions
 *
 * The real business logic lives in StoreFront, InventoryManager, and ShoppingCart.
 */
public class StoreFrontApp {

    /**
     * Program entry point.
     */
    public static void main(String[] args) {

        // StoreFront is the main "controller" for the program.
        // It coordinates inventory changes and cart changes.
        StoreFront storeFront = new StoreFront();

        // Start with a known state so every run is predictable.
        storeFront.initializeStore();

        // Scanner reads the user's menu choices and input.
        // We keep it open for the entire program and close it once at the end.
        Scanner scanner = new Scanner(System.in);

        // Simple welcome banner so the user knows the shop is ready.
        System.out.println("================================");
        System.out.println("Welcome to Keon's Adventurer Shop!");
        System.out.println("Type a menu number to get started.");
        System.out.println("================================");

        // Main loop flag. When the user chooses "0", we flip this to false.
        boolean running = true;

        // Keep showing the menu until the user chooses to exit.
        while (running) {
            printMenu();

            // Read the user's choice as a string so we can handle unexpected inputs safely.
            // For example, the user might type "two" instead of "2".
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        // Show current inventory state (products and remaining stock).
                        printInventory(storeFront.getInventoryManager().listProducts());
                        break;

                    case "2":
                        // Purchase a product (remove stock and add to cart).
                        handlePurchase(storeFront, scanner);
                        break;

                    case "3":
                        // Cancel a purchase (remove from cart and add stock back).
                        handleCancel(storeFront, scanner);
                        break;

                    case "4":
                        // View what's in the shopping cart.
                        printCart(storeFront);
                        break;

                    case "5":
                        // Show the total cost of the cart.
                        System.out.println("Cart Total: $" + storeFront.getCartTotal());
                        break;

                    case "6":
                        // Re-initialize store inventory and clear cart.
                        storeFront.initializeStore();
                        System.out.println("Store re-initialized and cart cleared.");
                        break;

                    case "0":
                        // Exit the program.
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid option. Please choose a menu number.");
                        break;
                }
            } catch (Exception ex) {
                // Catch errors and display the message so the user knows what went wrong.
                System.out.println("Error: " + ex.getMessage());
            }

            System.out.println(); // spacing between actions
        }

        // Clean shutdown.
        scanner.close();
        System.out.println("Goodbye!");
    }

    /**
     * Prints the available menu options.
     */
    private static void printMenu() {
        System.out.println("==== Adventurer Shop ====");
        System.out.println("1) List Inventory");
        System.out.println("2) Purchase Item");
        System.out.println("3) Cancel Purchase");
        System.out.println("4) View Cart");
        System.out.println("5) View Cart Total");
        System.out.println("6) Re-Initialize Store");
        System.out.println("0) Exit");
        System.out.print("Enter choice: ");
    }

    /**
     * Prints the inventory list to the console.
     */
    private static void printInventory(List<SalableProduct> products) {
        System.out.println("Inventory:");
        if (products.isEmpty()) {
            System.out.println("(No products in inventory)");
            return;
        }
        for (SalableProduct p : products) {
            System.out.println(p);
        }
    }

    /**
     * Handles the purchase flow by collecting input and calling StoreFront.
     */
    private static void handlePurchase(StoreFront storeFront, Scanner scanner) {
        System.out.print("Enter product name to purchase: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter quantity to purchase: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());

        storeFront.purchaseProduct(name, qty);
        System.out.println("Added to cart.");
    }

    /**
     * Handles the cancel flow by collecting input and calling StoreFront.
     */
    private static void handleCancel(StoreFront storeFront, Scanner scanner) {
        System.out.print("Enter product name to cancel: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter quantity to cancel: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());

        storeFront.cancelPurchase(name, qty);
        System.out.println("Cancelled from cart.");
    }

    /**
     * Prints cart contents in a readable way.
     */
    private static void printCart(StoreFront storeFront) {
        Map<String, Integer> items = storeFront.getShoppingCart().getItems();
        System.out.println("Shopping Cart:");

        if (items.isEmpty()) {
            System.out.println("(Cart is empty)");
            return;
        }

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            System.out.println(entry.getKey() + " x " + entry.getValue());
        }
    }
}
