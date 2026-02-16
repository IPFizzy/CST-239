package service;

import model.SalableProduct;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the user's shopping cart.
 *
 * The cart tracks what the user intends to buy, but it does not manage stock.
 * Stock is handled by InventoryManager, and StoreFront coordinates changes between them.
 */
public class ShoppingCart {

    /*
     * Map of product name -> quantity in the cart.
     * LinkedHashMap keeps items in the order they were added, which looks nicer in console output.
     */
    private final Map<String, Integer> items = new LinkedHashMap<>();

    /**
     * Adds an item to the cart.
     *
     * This method only updates the cart. It does not check inventory stock.
     * StoreFront is responsible for verifying stock before calling this.
     *
     * @param productName product name
     * @param qty quantity to add (must be > 0)
     */
    public void addItem(String productName, int qty) {

        // Product name is required so the cart always has meaningful keys.
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank.");
        }

        // Quantity must be positive, otherwise the cart could become inconsistent.
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0.");
        }

        // Keep the key trimmed so "Mouse" and "Mouse " do not become separate entries.
        String key = productName.trim();

        // If the item already exists, add onto it. Otherwise, start at the given quantity.
        items.put(key, items.getOrDefault(key, 0) + qty);
    }

    /**
     * Removes an item quantity from the cart.
     *
     * If the removal brings the quantity down to zero, the item is removed completely.
     *
     * @param productName product name
     * @param qty quantity to remove (must be > 0)
     */
    public void removeItem(String productName, int qty) {

        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank.");
        }

        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0.");
        }

        String key = productName.trim();
        int current = items.getOrDefault(key, 0);

        // Do not allow removing more than what exists in the cart.
        if (current < qty) {
            throw new IllegalStateException("Cart does not have enough quantity to remove.");
        }

        int updated = current - qty;

        // If quantity hits zero, remove the entry entirely to keep output clean.
        if (updated == 0) {
            items.remove(key);
        } else {
            items.put(key, updated);
        }
    }

    /**
     * Clears all items from the cart.
     *
     * Used when the store is re-initialized or if a user checks out in a future milestone.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Returns a read-only view of the cart contents.
     *
     * This prevents other classes from modifying the map directly.
     *
     * @return unmodifiable cart map
     */
    public Map<String, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * Calculates the total cost of the current cart.
     *
     * The cart only stores names and quantities, so we use InventoryManager
     * to look up product prices. If an item is missing from inventory, that indicates
     * a mismatch between inventory and cart and should be treated as an error.
     *
     * @param inventoryManager inventory manager used to lookup product prices
     * @return total cost of all items in the cart
     */
    public BigDecimal calculateTotal(InventoryManager inventoryManager) {

        BigDecimal total = BigDecimal.ZERO;

        // Multiply each product's price by its cart quantity and add to the running total.
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String name = entry.getKey();
            int qty = entry.getValue();

            SalableProduct product = inventoryManager.findByName(name)
                    .orElseThrow(() -> new IllegalStateException("Cart item not found in inventory: " + name));

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        return total;
    }
}
