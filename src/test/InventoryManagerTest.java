package test;

import model.InventoryItemData;
import model.SalableProduct;
import model.Weapon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import service.FileServiceException;
import service.InventoryManager;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the InventoryManager class.
 */
public class InventoryManagerTest {

    /**
     * Tests default inventory initialization.
     */
    @Test
    public void testInitializeDefaultInventory() {
        InventoryManager manager = new InventoryManager();
        manager.initializeDefaultInventory();

        List<SalableProduct> products = manager.listProducts();

        assertFalse(products.isEmpty());
        assertTrue(products.size() >= 5);
    }

    /**
     * Tests adding a new product.
     */
    @Test
    public void testAddProduct() {
        InventoryManager manager = new InventoryManager();
        manager.addProduct(new Weapon("Dagger", "Small blade", new BigDecimal("15.00"), 2));

        assertTrue(manager.findByName("Dagger").isPresent());
        assertEquals(2, manager.findByName("Dagger").get().getQuantity());
    }

    /**
     * Tests adding stock to an existing product by adding the same product again.
     */
    @Test
    public void testAddDuplicateProductIncreasesStock() {
        InventoryManager manager = new InventoryManager();
        manager.addProduct(new Weapon("Dagger", "Small blade", new BigDecimal("15.00"), 2));
        manager.addProduct(new Weapon("Dagger", "Small blade", new BigDecimal("15.00"), 3));

        assertEquals(5, manager.findByName("Dagger").get().getQuantity());
    }

    /**
     * Tests stock availability checks.
     */
    @Test
    public void testHasStock() {
        InventoryManager manager = new InventoryManager();
        manager.addProduct(new Weapon("Dagger", "Small blade", new BigDecimal("15.00"), 5));

        assertTrue(manager.hasStock("Dagger", 3));
        assertFalse(manager.hasStock("Dagger", 10));
    }

    /**
     * Tests removing stock.
     */
    @Test
    public void testRemoveStock() {
        InventoryManager manager = new InventoryManager();
        manager.addProduct(new Weapon("Dagger", "Small blade", new BigDecimal("15.00"), 5));

        manager.removeStock("Dagger", 2);

        assertEquals(3, manager.findByName("Dagger").get().getQuantity());
    }

    /**
     * Tests adding stock back.
     */
    @Test
    public void testAddStock() {
        InventoryManager manager = new InventoryManager();
        manager.addProduct(new Weapon("Dagger", "Small blade", new BigDecimal("15.00"), 5));

        manager.addStock("Dagger", 4);

        assertEquals(9, manager.findByName("Dagger").get().getQuantity());
    }

    /**
     * Tests sorting inventory by name.
     */
    @Test
    public void testGetSortedProductsByName() {
        InventoryManager manager = new InventoryManager();
        manager.addProduct(new Weapon("Zebra Blade", "desc", new BigDecimal("20.00"), 1));
        manager.addProduct(new Weapon("Apple Blade", "desc", new BigDecimal("10.00"), 1));

        List<SalableProduct> products = manager.getSortedProducts("name", true);

        assertEquals("Apple Blade", products.get(0).getName());
        assertEquals("Zebra Blade", products.get(1).getName());
    }

    /**
     * Tests sorting inventory by price descending.
     */
    @Test
    public void testGetSortedProductsByPriceDescending() {
        InventoryManager manager = new InventoryManager();
        manager.addProduct(new Weapon("Cheap", "desc", new BigDecimal("10.00"), 1));
        manager.addProduct(new Weapon("Expensive", "desc", new BigDecimal("50.00"), 1));

        List<SalableProduct> products = manager.getSortedProducts("price", false);

        assertEquals("Expensive", products.get(0).getName());
        assertEquals("Cheap", products.get(1).getName());
    }

    /**
     * Tests saving inventory to JSON and loading it back.
     *
     * @param tempDir temporary folder supplied by JUnit
     * @throws FileServiceException when file operations fail
     */
    @Test
    public void testSaveAndLoadInventoryFromJson(@TempDir Path tempDir) throws FileServiceException {
        InventoryManager manager = new InventoryManager();
        manager.addProduct(new Weapon("Dagger", "Small blade", new BigDecimal("15.00"), 2));

        Path file = tempDir.resolve("inventory.json");
        manager.saveInventoryToJson(file.toString());

        InventoryManager loadedManager = new InventoryManager();
        loadedManager.initializeInventoryFromJson(file.toString());

        assertTrue(loadedManager.findByName("Dagger").isPresent());
        assertEquals(2, loadedManager.findByName("Dagger").get().getQuantity());
    }

    /**
     * Tests replacing inventory from raw item data.
     */
    @Test
    public void testReplaceInventoryFromItemData() {
        InventoryManager manager = new InventoryManager();
        manager.initializeDefaultInventory();

        List<InventoryItemData> items = List.of(
                new InventoryItemData("WEAPON", "Test Sword", "desc", new BigDecimal("99.99"), 7)
        );

        manager.replaceInventoryFromItemData(items);

        assertEquals(1, manager.listProducts().size());
        assertTrue(manager.findByName("Test Sword").isPresent());
    }
}
