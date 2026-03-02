package service;

import model.SalableProduct;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores items that the user intends to purchase.
 *
 * Requirements supported:
 * - Initialize cart on StoreFront startup
 * - Add to cart on purchase
 * - Remove from cart on cancel
 * - Return cart contents
 * - Clear cart contents
 */
public class ShoppingCart {

    // Store cart items by normalized product name
    private final Map<String, Integer> items = new HashMap<>();

    /**
     * Adds a quantity of an item to the cart.
     *
     * @param productName name of item
     * @param qty         quantity to add
     */
    public void addItem(String productName, int qty) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be blank.");
        }
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        String key = normalizeKey(productName);
        int currentQty = items.getOrDefault(key, 0);
        items.put(key, currentQty + qty);
    }

    /**
     * Removes a quantity of an item from the cart.
     *
     * @param productName name of item
     * @param qty         quantity to remove
     */
    public void removeItem(String productName, int qty) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be blank.");
        }
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        String key = normalizeKey(productName);

        if (!items.containsKey(key)) {
            throw new IllegalArgumentException("Item is not in the cart.");
        }

        int currentQty = items.get(key);

        if (qty > currentQty) {
            throw new IllegalArgumentException("Not enough quantity in the cart to remove.");
        }

        int newQty = currentQty - qty;

        // If item reaches 0, remove it completely
        if (newQty == 0) {
            items.remove(key);
        } else {
            items.put(key, newQty);
        }
    }

    /**
     * Clears the entire cart.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Returns an unmodifiable view of the cart contents.
     *
     * @return map of item name -> quantity
     */
    public Map<String, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * Calculates the cart total using inventory prices.
     *
     * @param inventoryManager inventory manager for price lookup
     * @return total cost
     */
    public BigDecimal calculateTotal(InventoryManager inventoryManager) {

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> entry : items.entrySet()) {

            String nameKey = entry.getKey();
            int qty = entry.getValue();

            // Find product info from inventory so the cart does not store price data itself
            SalableProduct product = inventoryManager.findByName(nameKey)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found in inventory: " + nameKey));

            BigDecimal lineCost = product.getPrice().multiply(new BigDecimal(qty));
            total = total.add(lineCost);
        }

        return total;
    }

    /**
     * Normalizes a product name into a key.
     *
     * @param name raw name
     * @return normalized key
     */
    private String normalizeKey(String name) {
        return name.trim().toLowerCase();
    }
}
