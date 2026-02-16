package service;

import model.SalableProduct;

import java.math.BigDecimal;
import java.util.*;

/**
 * Manages the store's inventory.
 *
 * For this milestone, inventory is stored in-memory using a map.
 * Later milestones could swap this out for file storage or a database,
 * but keeping it in memory makes testing and demonstration simple.
 */
public class InventoryManager {

    /*
     * Inventory is keyed by a normalized product name so lookups are fast and simple.
     * LinkedHashMap is used so products stay in insertion order when listed in the console.
     */
    private final Map<String, SalableProduct> inventory = new LinkedHashMap<>();

    /**
     * Loads a default set of inventory items.
     *
     * This provides a consistent starting point every time the program runs.
     * It also makes the video demo easier because the inventory is predictable.
     */
    public void initializeDefaultInventory() {
        // Clear first so re-initializing the store resets it completely.
        inventory.clear();

        // Add a few sample products that cover a range of prices and quantities.
        addProduct(new SalableProduct(
                "Keyboard",
                "Mechanical keyboard, compact layout",
                new BigDecimal("79.99"),
                5
        ));

        addProduct(new SalableProduct(
                "Mouse",
                "Wireless gaming mouse",
                new BigDecimal("49.99"),
                8
        ));

        addProduct(new SalableProduct(
                "Headset",
                "Over-ear headset with mic",
                new BigDecimal("59.99"),
                4
        ));
    }

    /**
     * Adds a product to inventory.
     *
     * Product names must be unique (case-insensitive) in this milestone version.
     * If a duplicate is detected, an exception is thrown to protect data integrity.
     *
     * @param product the product to add
     */
    public void addProduct(SalableProduct product) {

        // Avoid storing null products since it would cause errors later on.
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }

        // Normalize the name for consistent case-insensitive storage and lookup.
        String key = normalizeKey(product.getName());

        // Prevent duplicates so we do not overwrite an existing product by accident.
        if (inventory.containsKey(key)) {
            throw new IllegalStateException("Product already exists: " + product.getName());
        }

        inventory.put(key, product);
    }

    /**
     * Returns a read-only list of inventory products.
     *
     * This is used mainly for display in the console app.
     * Returning an unmodifiable list prevents external code from altering inventory directly.
     *
     * @return list of products in inventory
     */
    public List<SalableProduct> listProducts() {
        return Collections.unmodifiableList(new ArrayList<>(inventory.values()));
    }

    /**
     * Finds a product by name using a case-insensitive lookup.
     *
     * Optional is used here to avoid returning null and to make "not found" handling clearer.
     *
     * @param name product name to search for
     * @return Optional containing the product if found, otherwise empty
     */
    public Optional<SalableProduct> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(inventory.get(normalizeKey(name)));
    }

    /**
     * Checks whether the requested quantity is available for a product.
     *
     * This is used by StoreFront before committing a purchase.
     *
     * @param name product name
     * @param qty quantity requested
     * @return true if the product exists and has at least that amount in stock
     */
    public boolean hasStock(String name, int qty) {

        // A request for 0 or negative quantity is not a valid "stock check."
        if (qty <= 0) {
            return false;
        }

        Optional<SalableProduct> productOpt = findByName(name);
        return productOpt.isPresent() && productOpt.get().getQuantity() >= qty;
    }

    /**
     * Removes stock from inventory.
     *
     * This is called when a purchase is made. The product must exist,
     * and the product class will enforce that stock cannot go below zero.
     *
     * @param name product name
     * @param qty quantity to remove
     */
    public void removeStock(String name, int qty) {

        // If the product name is invalid, fail fast with a clear message.
        SalableProduct product = findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Unknown product: " + name));

        product.decreaseStock(qty);
    }

    /**
     * Adds stock back into inventory.
     *
     * This is used when a purchase is canceled so the store quantity stays accurate.
     *
     * @param name product name
     * @param qty quantity to add back
     */
    public void addStock(String name, int qty) {

        SalableProduct product = findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Unknown product: " + name));

        product.increaseStock(qty);
    }

    /**
     * Normalizes a product name so all inventory keys are consistent.
     *
     * @param name raw product name
     * @return normalized key version of the name
     */
    private String normalizeKey(String name) {
        return name.trim().toLowerCase();
    }
}
