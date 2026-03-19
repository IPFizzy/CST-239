package service;

import model.InventoryItemData;
import model.SalableProduct;

import java.math.BigDecimal;
import java.util.List;

/**
 * Coordinates user actions across InventoryManager and ShoppingCart.
 *
 * StoreFront acts as the main middle layer for both the user console
 * and the background administration service.
 */
public class StoreFront {

    private final InventoryManager inventoryManager;
    private final ShoppingCart shoppingCart;
    private String activeInventoryFilePath;

    /**
     * Creates a StoreFront with fresh service objects.
     */
    public StoreFront() {
        this.inventoryManager = new InventoryManager();
        this.shoppingCart = new ShoppingCart();
    }

    /**
     * Initializes the store from a JSON inventory file and clears the cart.
     *
     * @param inventoryFilePath inventory file path
     * @throws FileServiceException when the file cannot be read
     */
    public synchronized void initializeStore(String inventoryFilePath) throws FileServiceException {
        inventoryManager.initializeInventoryFromJson(inventoryFilePath);
        shoppingCart.clear();
        this.activeInventoryFilePath = inventoryFilePath;
    }

    /**
     * Initializes the store with default inventory and clears the cart.
     */
    public synchronized void initializeStore() {
        inventoryManager.initializeDefaultInventory();
        shoppingCart.clear();
    }

    /**
     * Saves the current inventory to a JSON file.
     *
     * @param inventoryFilePath file path to save
     * @throws FileServiceException when the file cannot be written
     */
    public synchronized void saveInventoryToJson(String inventoryFilePath) throws FileServiceException {
        inventoryManager.saveInventoryToJson(inventoryFilePath);
        this.activeInventoryFilePath = inventoryFilePath;
    }

    /**
     * Provides access to the inventory manager.
     *
     * @return inventory manager
     */
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    /**
     * Provides access to the shopping cart.
     *
     * @return shopping cart
     */
    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    /**
     * Returns a sorted inventory view.
     *
     * @param sortBy sort field
     * @param ascending true for ascending order
     * @return sorted products
     */
    public synchronized List<SalableProduct> getSortedProducts(String sortBy, boolean ascending) {
        return inventoryManager.getSortedProducts(sortBy, ascending);
    }

    /**
     * Purchases a product by moving stock from inventory to cart.
     *
     * @param productName product name
     * @param qty quantity to purchase
     * @throws FileServiceException when updated inventory cannot be saved
     */
    public synchronized void purchaseProduct(String productName, int qty) throws FileServiceException {
        if (!inventoryManager.hasStock(productName, qty)) {
            throw new IllegalStateException("Not enough stock to purchase.");
        }

        inventoryManager.removeStock(productName, qty);
        shoppingCart.addItem(productName, qty);

        if (activeInventoryFilePath != null) {
            inventoryManager.saveInventoryToJson(activeInventoryFilePath);
        }
    }

    /**
     * Cancels a purchase by moving quantity from cart back to inventory.
     *
     * @param productName product name
     * @param qty quantity to cancel
     * @throws FileServiceException when updated inventory cannot be saved
     */
    public synchronized void cancelPurchase(String productName, int qty) throws FileServiceException {
        shoppingCart.removeItem(productName, qty);
        inventoryManager.addStock(productName, qty);

        if (activeInventoryFilePath != null) {
            inventoryManager.saveInventoryToJson(activeInventoryFilePath);
        }
    }

    /**
     * Returns the current cart total.
     *
     * @return cart total
     */
    public synchronized BigDecimal getCartTotal() {
        return shoppingCart.calculateTotal(inventoryManager);
    }

    /**
     * Replaces the full inventory using admin-provided data and saves it.
     *
     * @param items admin item data
     * @param inventoryFilePath file path to save
     * @throws FileServiceException when save fails
     */
    public synchronized void replaceInventoryFromAdmin(List<InventoryItemData> items,
                                                       String inventoryFilePath) throws FileServiceException {
        inventoryManager.replaceInventoryFromItemData(items);
        inventoryManager.saveInventoryToJson(inventoryFilePath);
        this.activeInventoryFilePath = inventoryFilePath;
    }

    /**
     * Returns the current inventory as a JSON string.
     *
     * @param inventoryFilePath inventory file path
     * @return inventory JSON
     * @throws FileServiceException when reading or converting fails
     */
    public synchronized String getInventoryAsJson(String inventoryFilePath) throws FileServiceException {
        saveInventoryToJson(inventoryFilePath);
        return inventoryManager.getInventoryAsJson();
    }
}
