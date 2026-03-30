package test;

import model.SalableProduct;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SalableProduct base class.
 */
public class SalableProductTest {

    /**
     * Tests that a product is created with valid values.
     */
    @Test
    public void testConstructorWithValidValues() {
        SalableProduct product = new SalableProduct(
                "Iron Sword",
                "Basic sword",
                new BigDecimal("35.00"),
                5
        );

        assertEquals("Iron Sword", product.getName());
        assertEquals("Basic sword", product.getDescription());
        assertEquals(new BigDecimal("35.00"), product.getPrice());
        assertEquals(5, product.getQuantity());
    }

    /**
     * Tests that a blank name throws an exception.
     */
    @Test
    public void testConstructorWithBlankNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new SalableProduct("", "desc", new BigDecimal("10.00"), 1));
    }

    /**
     * Tests that a negative price throws an exception.
     */
    @Test
    public void testConstructorWithNegativePriceThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new SalableProduct("Item", "desc", new BigDecimal("-1.00"), 1));
    }

    /**
     * Tests that a negative quantity throws an exception.
     */
    @Test
    public void testConstructorWithNegativeQuantityThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new SalableProduct("Item", "desc", new BigDecimal("1.00"), -1));
    }

    /**
     * Tests updating the description.
     */
    @Test
    public void testSetDescription() {
        SalableProduct product = new SalableProduct(
                "Potion",
                "Old",
                new BigDecimal("5.00"),
                2
        );

        product.setDescription("New Description");
        assertEquals("New Description", product.getDescription());
    }

    /**
     * Tests updating the price.
     */
    @Test
    public void testSetPrice() {
        SalableProduct product = new SalableProduct(
                "Potion",
                "desc",
                new BigDecimal("5.00"),
                2
        );

        product.setPrice(new BigDecimal("7.50"));
        assertEquals(new BigDecimal("7.50"), product.getPrice());
    }

    /**
     * Tests that setting a negative price throws an exception.
     */
    @Test
    public void testSetNegativePriceThrowsException() {
        SalableProduct product = new SalableProduct(
                "Potion",
                "desc",
                new BigDecimal("5.00"),
                2
        );

        assertThrows(IllegalArgumentException.class, () ->
                product.setPrice(new BigDecimal("-1.00")));
    }

    /**
     * Tests updating the quantity.
     */
    @Test
    public void testSetQuantity() {
        SalableProduct product = new SalableProduct(
                "Potion",
                "desc",
                new BigDecimal("5.00"),
                2
        );

        product.setQuantity(10);
        assertEquals(10, product.getQuantity());
    }

    /**
     * Tests that setting a negative quantity throws an exception.
     */
    @Test
    public void testSetNegativeQuantityThrowsException() {
        SalableProduct product = new SalableProduct(
                "Potion",
                "desc",
                new BigDecimal("5.00"),
                2
        );

        assertThrows(IllegalArgumentException.class, () ->
                product.setQuantity(-1));
    }

    /**
     * Tests decreasing stock.
     */
    @Test
    public void testDecreaseStock() {
        SalableProduct product = new SalableProduct(
                "Potion",
                "desc",
                new BigDecimal("5.00"),
                10
        );

        product.decreaseStock(3);
        assertEquals(7, product.getQuantity());
    }

    /**
     * Tests that decreasing too much stock throws an exception.
     */
    @Test
    public void testDecreaseStockTooMuchThrowsException() {
        SalableProduct product = new SalableProduct(
                "Potion",
                "desc",
                new BigDecimal("5.00"),
                2
        );

        assertThrows(IllegalArgumentException.class, () ->
                product.decreaseStock(3));
    }

    /**
     * Tests increasing stock.
     */
    @Test
    public void testIncreaseStock() {
        SalableProduct product = new SalableProduct(
                "Potion",
                "desc",
                new BigDecimal("5.00"),
                2
        );

        product.increaseStock(4);
        assertEquals(6, product.getQuantity());
    }

    /**
     * Tests compareTo using names.
     */
    @Test
    public void testCompareTo() {
        SalableProduct a = new SalableProduct("Apple", "desc", new BigDecimal("1.00"), 1);
        SalableProduct b = new SalableProduct("Banana", "desc", new BigDecimal("1.00"), 1);

        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    /**
     * Tests equals and hashCode using case-insensitive names.
     */
    @Test
    public void testEqualsAndHashCode() {
        SalableProduct first = new SalableProduct("Iron Sword", "desc", new BigDecimal("10.00"), 1);
        SalableProduct second = new SalableProduct("iron sword", "different", new BigDecimal("20.00"), 99);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Tests that toString returns a non-null formatted string.
     */
    @Test
    public void testToString() {
        SalableProduct product = new SalableProduct(
                "Potion",
                "Healing item",
                new BigDecimal("9.99"),
                3
        );

        String output = product.toString();

        assertNotNull(output);
        assertTrue(output.contains("Potion"));
        assertTrue(output.contains("Healing item"));
        assertTrue(output.contains("3"));
    }
}
