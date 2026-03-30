package test;

import model.AdminCommandRequest;
import model.InventoryItemData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import service.FileService;
import service.FileServiceException;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the FileService class.
 */
public class FileServiceTest {

    /**
     * Tests writing inventory to JSON and reading it back.
     *
     * @param tempDir temporary folder
     * @throws FileServiceException when file operations fail
     */
    @Test
    public void testWriteAndReadInventoryJson(@TempDir Path tempDir) throws FileServiceException {
        FileService fileService = new FileService();

        List<InventoryItemData> items = List.of(
                new InventoryItemData("WEAPON", "Iron Sword", "Basic sword", new BigDecimal("35.00"), 6)
        );

        Path file = tempDir.resolve("inventory.json");

        fileService.writeInventoryToJson(file.toString(), items);
        List<InventoryItemData> loadedItems = fileService.readInventoryFromJson(file.toString());

        assertEquals(1, loadedItems.size());
        assertEquals("Iron Sword", loadedItems.get(0).getName());
    }

    /**
     * Tests parsing inventory JSON from a string.
     *
     * @throws FileServiceException when parsing fails
     */
    @Test
    public void testParseInventoryJsonString() throws FileServiceException {
        FileService fileService = new FileService();

        String json =
                "[\n" +
                "  {\n" +
                "    \"type\": \"WEAPON\",\n" +
                "    \"name\": \"Iron Sword\",\n" +
                "    \"description\": \"Basic sword\",\n" +
                "    \"price\": 35.00,\n" +
                "    \"quantity\": 6\n" +
                "  }\n" +
                "]";

        List<InventoryItemData> items = fileService.parseInventoryJsonString(json);

        assertEquals(1, items.size());
        assertEquals("Iron Sword", items.get(0).getName());
    }

    /**
     * Tests converting inventory items to JSON.
     *
     * @throws FileServiceException when conversion fails
     */
    @Test
    public void testInventoryItemsToJson() throws FileServiceException {
        FileService fileService = new FileService();

        List<InventoryItemData> items = List.of(
                new InventoryItemData("WEAPON", "Iron Sword", "Basic sword", new BigDecimal("35.00"), 6)
        );

        String json = fileService.inventoryItemsToJson(items);

        assertNotNull(json);
        assertTrue(json.contains("Iron Sword"));
    }

    /**
     * Tests reading a JSON file as raw text.
     *
     * @param tempDir temporary folder
     * @throws FileServiceException when file operations fail
     */
    @Test
    public void testReadInventoryJsonAsString(@TempDir Path tempDir) throws FileServiceException {
        FileService fileService = new FileService();

        List<InventoryItemData> items = List.of(
                new InventoryItemData("WEAPON", "Iron Sword", "Basic sword", new BigDecimal("35.00"), 6)
        );

        Path file = tempDir.resolve("inventory.json");
        fileService.writeInventoryToJson(file.toString(), items);

        String json = fileService.readInventoryJsonAsString(file.toString());

        assertNotNull(json);
        assertTrue(json.contains("Iron Sword"));
    }

    /**
     * Tests object to JSON and JSON back to object conversion.
     *
     * @throws FileServiceException when conversion fails
     */
    @Test
    public void testToJsonAndFromJson() throws FileServiceException {
        FileService fileService = new FileService();

        AdminCommandRequest request = new AdminCommandRequest("R", "");
        String json = fileService.toJson(request);
        AdminCommandRequest converted = fileService.fromJson(json, AdminCommandRequest.class);

        assertEquals("R", converted.getCommand());
        assertEquals("", converted.getPayload());
    }

    /**
     * Tests that reading a missing file throws an exception.
     */
    @Test
    public void testReadMissingFileThrowsException() {
        FileService fileService = new FileService();

        assertThrows(FileServiceException.class, () ->
                fileService.readInventoryFromJson("does_not_exist.json"));
    }
}
