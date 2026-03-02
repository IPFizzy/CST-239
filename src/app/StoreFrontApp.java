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

        // Scanner is shared across the program so we can read user input.
        Scanner scanner = new Scanner(System.in);

        boolean programRunning = true;

        System.out.println("========================================");
        System.out.println(" Welcome to the Fantasy Store Front!");
        System.out.println("========================================");

        // Main program loop
        while (programRunning) {

            // Show the menu and read the user's choice
            printMenu();
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            // Handle menu options
            switch (choice) {
                case "0":
                    programRunning = false;
                    break;

                case "1":
                    // Inventory list
                    List<SalableProduct> products = storeFront.getInventoryManager().listProducts();
                    printInventory(products);
                    break;

                case "2":
                    // Purchase flow
                    handlePurchase(storeFront, scanner);
                    break;

                case "3":
                    // Cancel purchase flow
                    handleCancel(storeFront, scanner);
                    break;

                case "4":
                    // Show cart items
                    printCart(storeFront);
                    break;

                case "5":
                    // Show cart total
                    System.out.println("Cart total: $" + storeFront.getCartTotal());
                    break;

                case "6":
                    // Reinitialize store inventory + clear cart
                    storeFront.initializeStore();
                    System.out.println("Store was reinitialized and the cart was cleared.");
                    break;

                default:
                    System.out.println("Invalid option.");
                    break;
            }

            System.out.println();
        }

        // Clean shutdown
        scanner.close();
        System.out.println("Goodbye");
    }

    /**
     * Prints the menu options.
     */
    private static void printMenu() {
        System.out.println("Menu Options:");
        System.out.println("0) Exit");
        System.out.println("1) Show inventory");
        System.out.println("2) Purchase a product");
        System.out.println("3) Cancel a purchase");
        System.out.println("4) Show cart items");
        System.out.println("5) Show cart total");
        System.out.println("6) Reinitialize store");
    }

    /**
     * Prints the inventory list to the console.
     *
     * @param products List of products returned from the InventoryManager
     */
    private static void printInventory(List<SalableProduct> products) {
        System.out.println("Current Inventory:");
        System.out.println("----------------------------------------");
        for (SalableProduct p : products) {
            System.out.println(p);
        }
        System.out.println("----------------------------------------");
    }

    /**
     * Handles the purchase sub-flow.
     *
     * @param storeFront StoreFront controller
     * @param scanner    Scanner for user input
     */
    private static void handlePurchase(StoreFront storeFront, Scanner scanner) {

        // Show inventory first so the user can see what's available.
        List<SalableProduct> products = storeFront.getInventoryManager().listProducts();
        printInventory(products);

        // Ask for product name
        System.out.print("Enter product name to purchase: ");
        String name = scanner.nextLine();

        // Ask for quantity
        System.out.print("Enter quantity to purchase: ");
        int qty;

        try {
            qty = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException ex) {
            System.out.println("Error: quantity must be a whole number.");
            return;
        }

        try {
            storeFront.purchaseProduct(name, qty);
            System.out.println("Added to cart");
        } catch (IllegalArgumentException ex) {
            // StoreFront throws IllegalArgumentException for validation and stock errors
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /**
     * Handles the cancel purchase sub-flow.
     *
     * @param storeFront StoreFront controller
     * @param scanner    Scanner for user input
     */
    private static void handleCancel(StoreFront storeFront, Scanner scanner) {

        // Show cart items so the user knows what can be removed.
        printCart(storeFront);

        // Ask for product name to cancel
        System.out.print("Enter product name to cancel: ");
        String name = scanner.nextLine();

        // Ask for quantity
        System.out.print("Enter quantity to cancel: ");
        int qty;

        try {
            qty = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException ex) {
            System.out.println("Error: quantity must be a whole number.");
            return;
        }

        try {
            storeFront.cancelPurchase(name, qty);
            System.out.println("Canceled from cart");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /**
     * Prints the cart contents.
     *
     * @param storeFront StoreFront controller
     */
    private static void printCart(StoreFront storeFront) {

        Map<String, Integer> items = storeFront.getShoppingCart().getItems();

        System.out.println("Cart Items:");
        System.out.println("----------------------------------------");

        if (items.isEmpty()) {
            System.out.println("(Cart is empty)");
        } else {
            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                System.out.println(entry.getKey() + " x" + entry.getValue());
            }
        }

        System.out.println("----------------------------------------");
    }
}
