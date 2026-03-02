package service;

import java.math.BigDecimal;

/**
 * Coordinates the InventoryManager and ShoppingCart.
 *
 * This is the main "business logic" class called by the console app.
 */
public class StoreFront {

    private final InventoryManager inventoryManager;
    private final ShoppingCart shoppingCart;

    /**
     * Creates a StoreFront with a new inventory manager and shopping cart.
     */
    public StoreFront() {
        this.inventoryManager = new InventoryManager();
        this.shoppingCart = new ShoppingCart();
    }

    /**
     * Initializes the store inventory and clears the cart.
     *
     * This should be called when the Store Front starts up.
     */
    public void initializeStore() {
        inventoryManager.initializeDefaultInventory();
        shoppingCart.clear();
    }

    /**
     * @return inventory manager
     */
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    /**
     * @return shopping cart
     */
    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    /**
     * Purchases a product by removing stock from inventory and adding it to the cart.
     *
     * @param productName name of product
     * @param qty         quantity to purchase
     */
    public void purchaseProduct(String productName, int qty) {

        if (!inventoryManager.hasStock(productName, qty)) {
            throw new IllegalArgumentException("Not enough stock to purchase.");
        }

        // Inventory changes should happen first, then cart changes
        inventoryManager.removeStock(productName, qty);
        shoppingCart.addItem(productName, qty);
    }

    /**
     * Cancels a purchase by removing items from the cart and adding stock back to inventory.
     *
     * @param productName name of product
     * @param qty         quantity to cancel
     */
    public void cancelPurchase(String productName, int qty) {

        // Cart changes should happen first to validate quantity exists in cart
        shoppingCart.removeItem(productName, qty);

        // After cart change is valid, return items to inventory
        inventoryManager.addStock(productName, qty);
    }

    /**
     * Returns the current cart total.
     *
     * @return total price of cart items
     */
    public BigDecimal getCartTotal() {
        return shoppingCart.calculateTotal(inventoryManager);
    }
}
