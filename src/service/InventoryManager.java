package service;

import model.InventoryItemData;
import model.SalableProduct;
import model.Weapon;
import model.Armor;
import model.Health;

import java.math.BigDecimal;
import java.util.*;

/**
 * Manages the store's inventory.
 *
 * For this milestone, inventory is initialized from an external JSON file using FileService.
 * Inventory is still stored in-memory using a map after it is loaded.
 */
public class InventoryManager {

    /*
     * Inventory is keyed by a normalized product name so lookups are fast and simple.
     * LinkedHashMap is used so products stay in insertion order when listed in the console.
     */
    private final Map<String, SalableProduct> inventory = new LinkedHashMap<>();

    // FileService is responsible for all file I/O and JSON serialization logic.
    private final FileService fileService = new FileService();

    /**
     * Loads inventory items from an external JSON file using the FileService.
     *
     * This supports milestone requirements for JSON initialization and proper exception handling.
     *
     * @param filePath path to the JSON file
     * @throws FileServiceException when the file cannot be read or JSON cannot be parsed
     */
    public void initializeInventoryFromJson(String filePath) throws FileServiceException {
        inventory.clear();

        List<InventoryItemData> items = fileService.readInventoryFromJson(filePath);

        for (InventoryItemData item : items) {
            SalableProduct product = createProductFromItemData(item);
            addProduct(product);
        }
    }

    /**
     * Loads a default set of inventory items.
     *
     * This method is kept as a safe fallback option in case the JSON file is missing or invalid.
     * It also makes troubleshooting easier during setup.
     */
    public void initializeDefaultInventory() {
        // Clear first so re-initializing the store resets it completely.
        inventory.clear();

        // Milestone 2: Start the shop with a few "game" items.
        // We hardcode these so the demo is consistent every time the program runs.

        // Weapons (2 different kinds)
        addProduct(new Weapon(
                "Iron Sword",
                "Basic one-handed sword, reliable damage",
                new BigDecimal("35.00"),
                6
        ));
        addProduct(new Weapon(
                "Hunter Bow",
                "Long range bow for steady damage",
                new BigDecimal("55.00"),
                4
        ));

        // Armor
        addProduct(new Armor(
                "Leather Armor",
                "Light armor with decent protection",
                new BigDecimal("40.00"),
                5
        ));

        // Health items
        addProduct(new Health(
                "Small Health Potion",
                "Restores a small amount of health",
                new BigDecimal("10.00"),
                12
        ));
        addProduct(new Health(
                "Large Health Potion",
                "Restores a large amount of health",
                new BigDecimal("25.00"),
                6
        ));
    }

    /**
     * Adds a product to inventory.
     *
     * If a product with the same normalized name already exists, its stock is increased.
     *
     * @param product product to add
     */
    public void addProduct(SalableProduct product) {
        String key = normalizeKey(product.getName());

        if (inventory.containsKey(key)) {
            inventory.get(key).increaseStock(product.getQuantity());
        } else {
            inventory.put(key, product);
        }
    }

    /**
     * Returns a list of products in inventory.
     *
     * @return list of products
     */
    public List<SalableProduct> listProducts() {
        return new ArrayList<>(inventory.values());
    }

    /**
     * Finds a product by name.
     *
     * Optional is used here to avoid returning null and to make calling code safer.
     *
     * @param name product name
     * @return optional product
     */
    public Optional<SalableProduct> findByName(String name) {
        String key = normalizeKey(name);
        return Optional.ofNullable(inventory.get(key));
    }

    /**
     * Checks if there is enough stock to fulfill a request.
     *
     * @param name product name
     * @param qty quantity requested
     * @return true if enough stock exists, otherwise false
     */
    public boolean hasStock(String name, int qty) {
        if (qty <= 0) {
            return false;
        }

        Optional<SalableProduct> found = findByName(name);
        return found.isPresent() && found.get().getQuantity() >= qty;
    }

    /**
     * Removes stock for a product.
     *
     * @param name product name
     * @param qty quantity to remove
     * @throws IllegalArgumentException if product is not found or qty is invalid
     * @throws IllegalStateException if not enough stock is available
     */
    public void removeStock(String name, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        SalableProduct product = findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + name));

        if (product.getQuantity() < qty) {
            throw new IllegalStateException("Not enough stock to remove.");
        }

        product.decreaseStock(qty);
    }

    /**
     * Adds stock back for a product.
     *
     * @param name product name
     * @param qty quantity to add
     * @throws IllegalArgumentException if product is not found or qty is invalid
     */
    public void addStock(String name, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        SalableProduct product = findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + name));

        product.increaseStock(qty);
    }

    /**
     * Normalizes product names for consistent map key lookups.
     *
     * This helps avoid issues where the user types extra spaces or uses different casing.
     *
     * @param name product name
     * @return normalized key
     */
    private String normalizeKey(String name) {
        return name == null ? "" : name.trim().toLowerCase();
    }

    /**
     * Converts InventoryItemData into the correct SalableProduct subclass.
     *
     * @param item inventory item data from JSON
     * @return SalableProduct instance
     * @throws IllegalArgumentException when JSON data is missing required values or has an invalid type
     */
    private SalableProduct createProductFromItemData(InventoryItemData item) {
        if (item == null) {
            throw new IllegalArgumentException("Inventory item data cannot be null.");
        }
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Inventory item name is required.");
        }
        if (item.getPrice() == null) {
            throw new IllegalArgumentException("Inventory item price is required for: " + item.getName());
        }
        if (item.getQuantity() < 0) {
            throw new IllegalArgumentException("Inventory quantity cannot be negative for: " + item.getName());
        }

        String type = item.getType() == null ? "" : item.getType().trim().toUpperCase();

        switch (type) {
            case "WEAPON":
                return new Weapon(item.getName(), item.getDescription(), item.getPrice(), item.getQuantity());
            case "ARMOR":
                return new Armor(item.getName(), item.getDescription(), item.getPrice(), item.getQuantity());
            case "HEALTH":
                return new Health(item.getName(), item.getDescription(), item.getPrice(), item.getQuantity());
            default:
                throw new IllegalArgumentException("Invalid inventory item type: " + item.getType()
                        + " (valid: WEAPON, ARMOR, HEALTH)");
        }
    }
}
