package service;

import model.Armor;
import model.Health;
import model.InventoryItemData;
import model.SalableProduct;
import model.Weapon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Manages the store inventory.
 *
 * Inventory is stored in memory using a map and can be loaded from or saved to JSON.
 */
public class InventoryManager {

    private final Map<String, SalableProduct> inventory = new LinkedHashMap<>();
    private final FileService fileService = new FileService();

    /**
     * Loads inventory from a JSON file.
     *
     * @param filePath inventory file path
     * @throws FileServiceException when file reading or JSON parsing fails
     */
    public synchronized void initializeInventoryFromJson(String filePath) throws FileServiceException {
        inventory.clear();

        List<InventoryItemData> items = fileService.readInventoryFromJson(filePath);
        for (InventoryItemData item : items) {
            addProduct(createProductFromItemData(item));
        }
    }

    /**
     * Loads the default inventory.
     */
    public synchronized void initializeDefaultInventory() {
        inventory.clear();

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

        addProduct(new Armor(
                "Leather Armor",
                "Light armor with decent protection",
                new BigDecimal("40.00"),
                5
        ));

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
     * @param product product to add
     */
    public synchronized void addProduct(SalableProduct product) {
        String key = normalizeKey(product.getName());

        if (inventory.containsKey(key)) {
            inventory.get(key).increaseStock(product.getQuantity());
        } else {
            inventory.put(key, product);
        }
    }

    /**
     * Returns inventory in insertion order.
     *
     * @return product list
     */
    public synchronized List<SalableProduct> listProducts() {
        return new ArrayList<>(inventory.values());
    }

    /**
     * Returns inventory sorted by name or price.
     *
     * @param sortBy field to sort by
     * @param ascending true for ascending order
     * @return sorted products
     */
    public synchronized List<SalableProduct> getSortedProducts(String sortBy, boolean ascending) {
        List<SalableProduct> sortedProducts = new ArrayList<>(inventory.values());

        String normalizedSortBy = sortBy == null ? "" : sortBy.trim().toLowerCase();

        if ("price".equals(normalizedSortBy)) {
            sortedProducts.sort(Comparator.comparing(SalableProduct::getPrice));
        } else {
            Collections.sort(sortedProducts);
        }

        if (!ascending) {
            Collections.reverse(sortedProducts);
        }

        return sortedProducts;
    }

    /**
     * Finds a product by name.
     *
     * @param name product name
     * @return matching product if found
     */
    public synchronized Optional<SalableProduct> findByName(String name) {
        return Optional.ofNullable(inventory.get(normalizeKey(name)));
    }

    /**
     * Checks whether enough stock exists.
     *
     * @param name product name
     * @param qty quantity requested
     * @return true when enough stock exists
     */
    public synchronized boolean hasStock(String name, int qty) {
        if (qty <= 0) {
            return false;
        }

        Optional<SalableProduct> found = findByName(name);
        return found.isPresent() && found.get().getQuantity() >= qty;
    }

    /**
     * Removes stock from inventory.
     *
     * @param name product name
     * @param qty quantity to remove
     */
    public synchronized void removeStock(String name, int qty) {
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
     * Adds stock back to inventory.
     *
     * @param name product name
     * @param qty quantity to add
     */
    public synchronized void addStock(String name, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        SalableProduct product = findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + name));

        product.increaseStock(qty);
    }

    /**
     * Replaces the entire inventory with item data provided by the admin service.
     *
     * @param items new item data
     */
    public synchronized void replaceInventoryFromItemData(List<InventoryItemData> items) {
        inventory.clear();

        for (InventoryItemData item : items) {
            addProduct(createProductFromItemData(item));
        }
    }

    /**
     * Returns the current inventory converted to transferable item data.
     *
     * @return item data list
     */
    public synchronized List<InventoryItemData> getInventoryItemDataList() {
        List<InventoryItemData> items = new ArrayList<>();

        for (SalableProduct product : inventory.values()) {
            items.add(toInventoryItemData(product));
        }

        return items;
    }

    /**
     * Saves the current inventory to JSON.
     *
     * @param filePath file path to save
     * @throws FileServiceException when writing fails
     */
    public synchronized void saveInventoryToJson(String filePath) throws FileServiceException {
        fileService.writeInventoryToJson(filePath, getInventoryItemDataList());
    }

    /**
     * Returns the current inventory as a JSON string.
     *
     * @return JSON inventory string
     * @throws FileServiceException when conversion fails
     */
    public synchronized String getInventoryAsJson() throws FileServiceException {
        return fileService.inventoryItemsToJson(getInventoryItemDataList());
    }

    /**
     * Normalizes product names for map lookups.
     *
     * @param name product name
     * @return normalized key
     */
    private String normalizeKey(String name) {
        return name == null ? "" : name.trim().toLowerCase();
    }

    /**
     * Converts raw item data into the correct product type.
     *
     * @param item item data
     * @return product instance
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
                throw new IllegalArgumentException("Invalid inventory item type: " + item.getType());
        }
    }

    /**
     * Converts a product to JSON-transferable item data.
     *
     * @param product product to convert
     * @return item data object
     */
    private InventoryItemData toInventoryItemData(SalableProduct product) {
        String type = "SALABLE_PRODUCT";

        if (product instanceof Weapon) {
            type = "WEAPON";
        } else if (product instanceof Armor) {
            type = "ARMOR";
        } else if (product instanceof Health) {
            type = "HEALTH";
        }

        return new InventoryItemData(
                type,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity()
        );
    }
}
