package service;

import model.SalableProduct;
import model.Weapon;
import model.Armor;
import model.Health;

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
                "Oak Bow",
                "Simple bow for ranged attacks",
                new BigDecimal("45.00"),
                4
        ));

        // Armor (2 different kinds)
        addProduct(new Armor(
                "Leather Armor",
                "Light armor, decent protection without slowing you down",
                new BigDecimal("30.00"),
                5
        ));
        addProduct(new Armor(
                "Iron Shield",
                "Sturdy shield that helps block incoming hits",
                new BigDecimal("25.00"),
                7
        ));

        // Health (at least 1)
        addProduct(new Health(
                "Health Potion",
                "Restores health when used",
                new BigDecimal("10.00"),
                12
        ));
    }

    /**
     * Adds a product to inventory.
     *
     * Product names must be unique (case-insensitive). If a product already exists,
     * this will overwrite the old entry.
     */
    public void addProduct(SalableProduct product) {
        inventory.put(normalizeKey(product.getName()), product);
    }

    /**
     * Returns all products in inventory as a list.
     */
    public List<SalableProduct> listProducts() {
        return new ArrayList<>(inventory.values());
    }

    /**
     * Finds a product by name (case-insensitive).
     */
    public Optional<SalableProduct> findByName(String name) {
        return Optional.ofNullable(inventory.get(normalizeKey(name)));
    }

    /**
     * Checks if the inventory has enough stock to fulfill a purchase.
     */
    public boolean hasStock(String name, int qty) {
        Optional<SalableProduct> productOpt = findByName(name);
        if (!productOpt.isPresent()) {
            return false;
        }
        return productOpt.get().getQuantity() >= qty;
    }

    /**
     * Removes stock from inventory for a given product.
     * Throws an exception if the product does not exist or stock is not enough.
     */
    public void removeStock(String name, int qty) {
        SalableProduct product = findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Unknown product: " + name));

        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        if (product.getQuantity() < qty) {
            throw new IllegalArgumentException("Not enough stock to remove.");
        }

        product.decreaseStock(qty);
    }

    /**
     * Adds stock back to inventory (used when canceling purchases).
     * Throws an exception if the product does not exist.
     */
    public void addStock(String name, int qty) {
        SalableProduct product = findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Unknown product: " + name));

        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        product.increaseStock(qty);
    }

    /**
     * Normalizes map keys so lookups are consistent.
     */
    private String normalizeKey(String name) {
        return name == null ? "" : name.trim().toLowerCase();
    }
}
