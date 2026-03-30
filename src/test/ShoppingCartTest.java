package test;

import model.Weapon;
import org.junit.jupiter.api.Test;
import service.InventoryManager;
import service.ShoppingCart;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ShoppingCart class.
 */
public class ShoppingCartTest {

    /**
     * Tests adding an item to the cart.
     */
    @Test
    public void testAddItem() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Iron Sword", 2);

        assertEquals(2, cart.getItems().get("Iron Sword"));
    }

    /**
     * Tests adding the same item again increases the quantity.
     */
    @Test
    public void testAddSameItemTwice() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Iron Sword", 2);
        cart.addItem("Iron Sword", 3);

        assertEquals(5, cart.getItems().get("Iron Sword"));
    }

    /**
     * Tests removing part of an item quantity.
     */
    @Test
    public void testRemoveItemPartialQuantity() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Iron Sword", 5);

        cart.removeItem("Iron Sword", 2);

        assertEquals(3, cart.getItems().get("Iron Sword"));
    }

    /**
     * Tests removing the full quantity removes the entry.
     */
    @Test
    public void testRemoveItemCompletely() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Iron Sword", 2);

        cart.removeItem("Iron Sword", 2);

        assertFalse(cart.getItems().containsKey("Iron Sword"));
    }

    /**
     * Tests that removing too much throws an exception.
     */
    @Test
    public void testRemoveTooMuchThrowsException() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Iron Sword", 1);

        assertThrows(IllegalStateException.class, () ->
                cart.removeItem("Iron Sword", 2));
    }

    /**
     * Tests clearing the cart.
     */
    @Test
    public void testClear() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Iron Sword", 1);
        cart.clear();

        assertTrue(cart.getItems().isEmpty());
    }

    /**
     * Tests cart total calculation.
     */
    @Test
    public void testCalculateTotal() {
        InventoryManager manager = new InventoryManager();
        manager.addProduct(new Weapon("Iron Sword", "desc", new BigDecimal("35.00"), 10));

        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Iron Sword", 2);

        assertEquals(new BigDecimal("70.00"), cart.calculateTotal(manager));
    }
}
