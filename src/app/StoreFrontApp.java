package app;

import model.SalableProduct;
import service.FileServiceException;
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

    // Recommended: place inventory.json in the project root (same level as src).
    private static final String INVENTORY_FILE_PATH = "inventory.json";

    /**
     * Program entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        // StoreFront is the main "controller" for the program.
        // It coordinates inventory changes and cart changes.
        StoreFront storeFront = new StoreFront();

        // Start with a known state so every run is predictable.
        // Milestone 4: Attempt to load from JSON using FileService.
        try {
            storeFront.initializeStore(INVENTORY_FILE_PATH);
            System.out.println("Inventory loaded from JSON: " + INVENTORY_FILE_PATH);
        } catch (FileServiceException ex) {
            System.out.println("Failed to load inventory from JSON.");
            System.out.println("Reason: " + ex.getMessage());
            System.out.println("Falling back to default hardcoded inventory for this run.\n");

            storeFront.initializeStore();
        }

        Scanner scanner = new Scanner(System.in);

        boolean running = true;

        System.out.println("Welcome to the Store Front!");

        while (running) {
            printMenu();
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        printInventory(storeFront.getInventoryManager().listProducts());
                        break;
                    case "2":
                        handlePurchase(storeFront, scanner);
                        break;
                    case "3":
                        handleCancel(storeFront, scanner);
                        break;
                    case "4":
                        printCart(storeFront);
                        break;
                    case "5":
                        System.out.println("Cart Total: $" + storeFront.getCartTotal());
                        break;
                    case "6":
                        // Re-initialize store inventory and clear cart.
                        // Try JSON again first, fall back if needed.
                        try {
                            storeFront.initializeStore(INVENTORY_FILE_PATH);
                            System.out.println("Store re-initialized from JSON.");
                        } catch (FileServiceException ex) {
                            System.out.println("Failed to re-load inventory from JSON.");
                            System.out.println("Reason: " + ex.getMessage());
                            System.out.println("Falling back to default hardcoded inventory.");
                            storeFront.initializeStore();
                        }
                        break;
                    case "7":
                        handleSortInventory(storeFront, scanner);
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                        break;
                }
            } catch (RuntimeException ex) {
                // This prevents stack traces during the demo and keeps output clean.
                System.out.println("Error: " + ex.getMessage());
            }

            System.out.println();
        }

        System.out.println("Goodbye");
        scanner.close();
    }

    /**
     * Prints the console menu options.
     */
    private static void printMenu() {
        System.out.println("Menu:");
        System.out.println("1) List Inventory");
        System.out.println("2) Purchase Product");
        System.out.println("3) Cancel Purchase");
        System.out.println("4) View Cart");
        System.out.println("5) View Cart Total");
        System.out.println("6) Re-Initialize Store");
        System.out.println("7) Sort Inventory");
        System.out.println("0) Exit");
    }

    /**
     * Prints the current inventory list.
     *
     * @param products list of products in inventory
     */
    private static void printInventory(List<SalableProduct> products) {
        System.out.println("Inventory:");

        for (SalableProduct p : products) {
            System.out.println(p);
        }
    }

    /**
     * Handles inventory sorting flow from the console.
     *
     * The user can sort by:
     * - name
     * - price
     *
     * And in either:
     * - ascending order
     * - descending order
     *
     * @param storeFront store front instance
     * @param scanner input scanner
     */
    private static void handleSortInventory(StoreFront storeFront, Scanner scanner) {
        System.out.print("Sort by name or price: ");
        String sortBy = scanner.nextLine().trim().toLowerCase();

        if (!sortBy.equals("name") && !sortBy.equals("price")) {
            throw new IllegalArgumentException("Sort field must be 'name' or 'price'.");
        }

        System.out.print("Sort order (asc or desc): ");
        String order = scanner.nextLine().trim().toLowerCase();

        if (!order.equals("asc") && !order.equals("desc")) {
            throw new IllegalArgumentException("Sort order must be 'asc' or 'desc'.");
        }

        boolean ascending = order.equals("asc");

        List<SalableProduct> sortedProducts = storeFront.getSortedProducts(sortBy, ascending);

        System.out.println("Sorted Inventory:");
        printInventory(sortedProducts);
    }

    /**
     * Handles purchasing flow from the console.
     *
     * @param storeFront store front instance
     * @param scanner input scanner
     */
    private static void handlePurchase(StoreFront storeFront, Scanner scanner) {
        System.out.print("Enter product name to purchase: ");
        String productName = scanner.nextLine();

        System.out.print("Enter quantity to purchase: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());

        storeFront.purchaseProduct(productName, qty);
        System.out.println("Added to cart.");
    }

    /**
     * Handles cancel purchase flow from the console.
     *
     * @param storeFront store front instance
     * @param scanner input scanner
     */
    private static void handleCancel(StoreFront storeFront, Scanner scanner) {
        System.out.print("Enter product name to cancel: ");
        String productName = scanner.nextLine();

        System.out.print("Enter quantity to cancel: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());

        storeFront.cancelPurchase(productName, qty);
        System.out.println("Canceled from cart.");
    }

    /**
     * Prints cart contents to the console.
     *
     * @param storeFront store front instance
     */
    private static void printCart(StoreFront storeFront) {
        Map<String, Integer> items = storeFront.getShoppingCart().getItems();

        if (items.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        System.out.println("Cart:");
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            System.out.println(entry.getKey() + " x " + entry.getValue());
        }
    }
}
