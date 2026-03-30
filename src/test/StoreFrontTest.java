package test;

import model.InventoryItemData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import service.FileService;
import service.FileServiceException;
import service.StoreFront;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the StoreFront class.
 */
public class StoreFrontTest {

    /**
     * Creates a small inventory JSON file for testing.
     *
     * @param tempDir temporary folder
     * @return path to the created inventory file
     * @throws FileServiceException when file writing fails
     */
    private String createInventoryFile(Path tempDir) throws FileServiceException {
        FileService fileService = new FileService();

        List<InventoryItemData> items = List.of(
                new InventoryItemData("WEAPON", "Iron Sword", "Basic sword", new BigDecimal("35.00"), 6),
                new InventoryItemData("HEALTH", "Small Health Potion", "Potion", new BigDecimal("10.00"), 12)
        );

        Path file = tempDir.resolve("inventory.json");
        fileService.writeInventoryToJson(file.toString(), items);
        return file.toString();
    }

    /**
     * Tests default store initialization.
     */
    @Test
    public void testInitializeStoreDefault() {
        StoreFront storeFront = new StoreFront();
        storeFront.initializeStore();

        assertFalse(storeFront.getInventoryManager().listProducts().isEmpty());
        assertTrue(storeFront.getShoppingCart().getItems().isEmpty());
    }

    /**
     * Tests JSON store initialization.
     *
     * @param tempDir temporary folder
     * @throws FileServiceException when file operations fail
     */
    @Test
    public void testInitializeStoreFromJson(@TempDir Path tempDir) throws FileServiceException {
        String filePath = createInventoryFile(tempDir);

        StoreFront storeFront = new StoreFront();
        storeFront.initializeStore(filePath);

        assertTrue(storeFront.getInventoryManager().findByName("Iron Sword").isPresent());
        assertTrue(storeFront.getShoppingCart().getItems().isEmpty());
    }

    /**
     * Tests purchasing a product.
     *
     * @param tempDir temporary folder
     * @throws FileServiceException when file operations fail
     */
    @Test
    public void testPurchaseProduct(@TempDir Path tempDir) throws FileServiceException {
        String filePath = createInventoryFile(tempDir);

        StoreFront storeFront = new StoreFront();
        storeFront.initializeStore(filePath);

        storeFront.purchaseProduct("Iron Sword", 2);

        assertEquals(2, storeFront.getShoppingCart().getItems().get("Iron Sword"));
        assertEquals(4, storeFront.getInventoryManager().findByName("Iron Sword").get().getQuantity());
    }

    /**
     * Tests that purchasing too much stock throws an exception.
     *
     * @param tempDir temporary folder
     * @throws FileServiceException when file operations fail
     */
    @Test
    public void testPurchaseProductWithInsufficientStock(@TempDir Path tempDir) throws FileServiceException {
        String filePath = createInventoryFile(tempDir);

        StoreFront storeFront = new StoreFront();
        storeFront.initializeStore(filePath);

        assertThrows(IllegalStateException.class, () ->
                storeFront.purchaseProduct("Iron Sword", 100));
    }

    /**
     * Tests canceling a purchase.
     *
     * @param tempDir temporary folder
     * @throws FileServiceException when file operations fail
     */
    @Test
    public void testCancelPurchase(@TempDir Path tempDir) throws FileServiceException {
        String filePath = createInventoryFile(tempDir);

        StoreFront storeFront = new StoreFront();
        storeFront.initializeStore(filePath);

        storeFront.purchaseProduct("Iron Sword", 2);
        storeFront.cancelPurchase("Iron Sword", 1);

        assertEquals(1, storeFront.getShoppingCart().getItems().get("Iron Sword"));
        assertEquals(5, storeFront.getInventoryManager().findByName("Iron Sword").get().getQuantity());
    }

    /**
     * Tests the cart total.
     *
     * @param tempDir temporary folder
     * @throws FileServiceException when file operations fail
     */
    @Test
    public void testGetCartTotal(@TempDir Path tempDir) throws FileServiceException {
        String filePath = createInventoryFile(tempDir);

        StoreFront storeFront = new StoreFront();
        storeFront.initializeStore(filePath);

        storeFront.purchaseProduct("Iron Sword", 2);

        assertEquals(new BigDecimal("70.00"), storeFront.getCartTotal());
    }

    /**
     * Tests replacing inventory from admin data.
     *
     * @param tempDir temporary folder
     * @throws FileServiceException when file operations fail
     */
    @Test
    public void testReplaceInventoryFromAdmin(@TempDir Path tempDir) throws FileServiceException {
        Path file = tempDir.resolve("inventory.json");

        StoreFront storeFront = new StoreFront();
        storeFront.initializeStore();

        List<InventoryItemData> items = List.of(
                new InventoryItemData("ARMOR", "Steel Armor", "Heavy armor", new BigDecimal("80.00"), 3)
        );

        storeFront.replaceInventoryFromAdmin(items, file.toString());

        assertEquals(1, storeFront.getInventoryManager().listProducts().size());
        assertTrue(storeFront.getInventoryManager().findByName("Steel Armor").isPresent());
    }
}
