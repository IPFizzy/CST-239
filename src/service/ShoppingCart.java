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
 * Stock is handled by InventoryManager, and StoreFront coordinates changes
 * between inventory and cart.
 */
public class ShoppingCart {

    /*
     * Map of product name to quantity in the cart.
     * LinkedHashMap keeps the cart display in the order items were added.
     */
    private final Map<String, Integer> items = new LinkedHashMap<>();

    /**
     * Adds an item to the cart.
     *
     * This only updates cart contents. Inventory checks are handled elsewhere.
     *
     * @param productName product name
     * @param qty quantity to add
     */
    public void addItem(String productName, int qty) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank.");
        }

        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0.");
        }

        String key = normalizeKey(productName);

        /*
         * Add onto the existing quantity if the item is already in the cart.
         * Otherwise, create a new cart entry.
         */
        items.put(key, items.getOrDefault(key, 0) + qty);
    }

    /**
     * Removes an item quantity from the cart.
     *
     * If the quantity becomes zero, the item is removed from the cart.
     *
     * @param productName product name
     * @param qty quantity to remove
     */
    public void removeItem(String productName, int qty) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank.");
        }

        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0.");
        }

        String key = normalizeKey(productName);
        int current = items.getOrDefault(key, 0);

        /*
         * This matches the expectation used in the unit tests.
         */
        if (current < qty) {
            throw new IllegalStateException("Cart does not have enough quantity to remove.");
        }

        int updated = current - qty;

        if (updated == 0) {
            items.remove(key);
        } else {
            items.put(key, updated);
        }
    }

    /**
     * Clears the entire cart.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Returns a read-only view of the cart contents.
     *
     * @return unmodifiable cart contents
     */
    public Map<String, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * Calculates the current total value of the cart.
     *
     * The cart stores only product names and quantities, so prices are looked up
     * from the inventory manager.
     *
     * @param inventoryManager inventory manager used for product lookup
     * @return total cart cost
     */
    public BigDecimal calculateTotal(InventoryManager inventoryManager) {
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String productName = entry.getKey();
            int qty = entry.getValue();

            SalableProduct product = inventoryManager.findByName(productName)
                    .orElseThrow(() -> new IllegalStateException(
                            "Cart item not found in inventory: " + productName));

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(qty));
            total = total.add(lineTotal);
        }

        return total;
    }

    /**
     * Normalizes the cart key.
     *
     * For the cart, we only trim spaces so the displayed product name stays the
     * same as the original item name used by the application and tests.
     *
     * @param name raw product name
     * @return normalized key
     */
    private String normalizeKey(String name) {
        return name.trim();
    }
}
