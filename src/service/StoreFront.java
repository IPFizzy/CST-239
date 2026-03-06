package service;

import java.math.BigDecimal;

/**
 * Coordinates user actions across InventoryManager and ShoppingCart.
 *
 * StoreFront is the main "middle layer" for the application.
 * The UI should call StoreFront methods instead of reaching directly into inventory or cart logic.
 */
public class StoreFront {

    // InventoryManager controls products and stock amounts.
    private final InventoryManager inventoryManager;

    // ShoppingCart tracks what the user intends to buy.
    private final ShoppingCart shoppingCart;

    /**
     * Creates a StoreFront with a fresh InventoryManager and ShoppingCart.
     *
     * Keeping these as private fields helps enforce that other code goes through StoreFront.
     */
    public StoreFront() {
        this.inventoryManager = new InventoryManager();
        this.shoppingCart = new ShoppingCart();
    }

    /**
     * Initializes the store to a known starting state using inventory loaded from JSON.
     *
     * This loads inventory from an external file and clears the cart so each run starts clean.
     *
     * @param inventoryFilePath path to the inventory JSON file
     * @throws FileServiceException when the inventory file cannot be read or parsed
     */
    public void initializeStore(String inventoryFilePath) throws FileServiceException {
        inventoryManager.initializeInventoryFromJson(inventoryFilePath);
        shoppingCart.clear();
    }

    /**
     * Initializes the store to a known starting state using default hardcoded inventory.
     *
     * This is kept as a fallback option.
     */
    public void initializeStore() {
        inventoryManager.initializeDefaultInventory();
        shoppingCart.clear();
    }

    /**
     * Provides access to the inventory manager for display purposes.
     *
     * @return inventory manager
     */
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    /**
     * Provides access to the shopping cart for display purposes.
     *
     * @return shopping cart
     */
    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    /**
     * Purchases a product by moving stock from inventory into the cart.
     *
     * The stock check happens first so inventory never becomes negative.
     * If the request is invalid or stock is unavailable, an exception is thrown.
     *
     * @param productName product name
     * @param qty quantity to purchase
     */
    public void purchaseProduct(String productName, int qty) {

        // Verify stock availability before making any changes.
        if (!inventoryManager.hasStock(productName, qty)) {
            throw new IllegalStateException("Not enough stock to purchase.");
        }

        // Remove from inventory first. This ensures we do not add items to the cart
        // unless we have actually reserved them from inventory.
        inventoryManager.removeStock(productName, qty);

        // Add to cart second. This is the final step that represents the user's "purchase intent."
        shoppingCart.addItem(productName, qty);
    }

    /**
     * Cancels a purchase by moving quantity from cart back into inventory.
     *
     * Removing from the cart first guarantees the cart has enough quantity to cancel.
     * Once the cart update succeeds, inventory is restored.
     *
     * @param productName product name
     * @param qty quantity to cancel
     */
    public void cancelPurchase(String productName, int qty) {

        // If the cart does not have enough to remove, this will throw an exception.
        shoppingCart.removeItem(productName, qty);

        // Restore inventory once the cart has been updated successfully.
        inventoryManager.addStock(productName, qty);
    }

    /**
     * Calculates the total cost of the current cart.
     *
     * @return total price for items currently in the cart
     */
    public BigDecimal getCartTotal() {
        return shoppingCart.calculateTotal(inventoryManager);
    }
}
