package service;

import model.Armor;
import model.Health;
import model.SalableProduct;
import model.Weapon;

import java.math.BigDecimal;
import java.util.*;

/**
 * Manages the store inventory.
 *
 * Requirements supported:
 * - Initialize store inventory (invoked on StoreFront startup)
 * - Remove stock when a product is purchased
 * - Add stock when a purchase is canceled
 * - Return the entire inventory
 */
public class InventoryManager {

    // Inventory is stored by a normalized string key
    private final Map<String, SalableProduct> inventory = new HashMap<>();

    /**
     * Loads default inventory into the store.
     *
     * This resets the inventory map to a known state.
     */
    public void initializeDefaultInventory() {
        inventory.clear();

        // Weapons
        addProduct(new Weapon("Iron Sword", "Basic sword with reliable damage.", new BigDecimal("25.00"), 10));
        addProduct(new Weapon("Oak Bow", "Simple bow with decent range.", new BigDecimal("20.00"), 8));

        // Armor
        addProduct(new Armor("Leather Armor", "Light armor, easy to wear.", new BigDecimal("30.00"), 6));
        addProduct(new Armor("Steel Helmet", "Sturdy helmet for extra protection.", new BigDecimal("18.50"), 12));

        // Health items
        addProduct(new Health("Small Potion", "Restores a small amount of health.", new BigDecimal("5.00"), 25));
        addProduct(new Health("Large Potion", "Restores a large amount of health.", new BigDecimal("12.00"), 15));
    }

    /**
     * Adds a product into the inventory system.
     *
     * If a product already exists with the same name, this overwrites the existing entry.
     *
     * @param product product to add
     */
    public void addProduct(SalableProduct product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }

        inventory.put(normalizeKey(product.getName()), product);
    }

    /**
     * Returns a list of all products in inventory.
     *
     * @return list of SalableProduct objects
     */
    public List<SalableProduct> listProducts() {
        return new ArrayList<>(inventory.values());
    }

    /**
     * Finds a product by name.
     *
     * @param name product name
     * @return Optional containing product if found
     */
    public Optional<SalableProduct> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(inventory.get(normalizeKey(name)));
    }

    /**
     * Checks if enough stock exists to cover a requested quantity.
     *
     * @param name product name
     * @param qty  requested quantity
     * @return true if product exists and has enough stock
     */
    public boolean hasStock(String name, int qty) {
        if (qty <= 0) {
            return false;
        }

        Optional<SalableProduct> productOpt = findByName(name);
        if (!productOpt.isPresent()) {
            return false;
        }

        return productOpt.get().getQuantity() >= qty;
    }

    /**
     * Removes stock for a product.
     *
     * Invoked when a product is purchased.
     *
     * @param name product name
     * @param qty  quantity to remove
     */
    public void removeStock(String name, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        SalableProduct product = findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + name));

        product.decreaseStock(qty);
    }

    /**
     * Adds stock back to a product.
     *
     * Invoked when a purchase is canceled.
     *
     * @param name product name
     * @param qty  quantity to add back
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
     * Normalizes a product name into a key for map storage and lookup.
     *
     * @param name raw product name
     * @return normalized key
     */
    private String normalizeKey(String name) {
        return name.trim().toLowerCase();
    }
}
