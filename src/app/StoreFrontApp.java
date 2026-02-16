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
     *
     * @param args command-line arguments (not used for this assignment)
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
                        // Purchase moves items from inventory into the shopping cart.
                        handlePurchase(storeFront, scanner);
                        break;

                    case "3":
                        // Cancel purchase removes items from the cart and restores inventory.
                        handleCancel(storeFront, scanner);
                        break;

                    case "4":
                        // View cart shows what the user has added so far.
                        printCart(storeFront);
                        break;

                    case "5":
                        // Total is calculated from what's currently in the cart.
                        System.out.println("Cart total: $" + storeFront.getCartTotal());
                        break;

                    case "6":
                        // Re-initialize resets the store and clears the cart.
                        // This is useful when demonstrating the app during the screencast.
                        storeFront.initializeStore();
                        System.out.println("Store re-initialized.");
                        break;

                    case "0":
                        // Exit the program loop.
                        running = false;
                        break;

                    default:
                        // If they type something not on the menu, keep the program running.
                        System.out.println("Invalid option. Try again.");
                        break;
                }
            } catch (Exception ex) {
                /*
                 * We catch exceptions here so the program does not crash.
                 * Most errors will come from invalid input or invalid actions:
                 * - Purchasing more than stock
                 * - Canceling more than what's in the cart
                 * - Typing a product name that does not exist
                 */
                System.out.println("Error: " + ex.getMessage());
            }

            // Blank line keeps the console output easier to read between actions.
            System.out.println();
        }

        // Always close the scanner before exiting to clean up system resources.
        scanner.close();

        System.out.println("Goodbye.");
    }

    /**
     * Prints the main menu options for the user.
     */
    private static void printMenu() {
        System.out.println("==== Store Front ====");
        System.out.println("1) List Inventory");
        System.out.println("2) Purchase Product");
        System.out.println("3) Cancel Purchase");
        System.out.println("4) View Cart");
        System.out.println("5) View Cart Total");
        System.out.println("6) Re-Initialize Store");
        System.out.println("0) Exit");
        System.out.print("Select: ");
    }

    /**
     * Prints all products currently in inventory.
     *
     * @param products inventory product list
     */
    private static void printInventory(List<SalableProduct> products) {
        System.out.println("---- Inventory ----");

        // Each product has a clean toString() format so listing is simple.
        for (SalableProduct p : products) {
            System.out.println(p);
        }
    }

    /**
     * Handles the "Purchase Product" menu option.
     * Reads the product name and quantity, then calls StoreFront to perform the purchase.
     *
     * @param storeFront store front controller
     * @param scanner input reader
     */
    private static void handlePurchase(StoreFront storeFront, Scanner scanner) {
        System.out.print("Enter product name to purchase: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());

        // If there is not enough stock, StoreFront will throw an error.
        storeFront.purchaseProduct(name, qty);

        System.out.println("Added to cart: " + name + " x" + qty);
    }

    /**
     * Handles the "Cancel Purchase" menu option.
     * Reads the product name and quantity, then calls StoreFront to cancel the purchase.
     *
     * @param storeFront store front controller
     * @param scanner input reader
     */
    private static void handleCancel(StoreFront storeFront, Scanner scanner) {
        System.out.print("Enter product name to cancel: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter quantity to cancel: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());

        // If the cart does not contain enough quantity, StoreFront will throw an error.
        storeFront.cancelPurchase(name, qty);

        System.out.println("Canceled from cart: " + name + " x" + qty);
    }

    /**
     * Prints the current shopping cart contents.
     *
     * @param storeFront store front controller (provides access to the cart)
     */
    private static void printCart(StoreFront storeFront) {
        System.out.println("---- Cart ----");

        // Cart stores product names and quantities. We display what is currently in the map.
        Map<String, Integer> items = storeFront.getShoppingCart().getItems();

        if (items.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            System.out.println(entry.getKey() + " x" + entry.getValue());
        }
    }
}
