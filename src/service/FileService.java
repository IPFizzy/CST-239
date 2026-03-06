package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.InventoryItemData;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Handles reading and writing inventory data using JSON files.
 *
 * This class centralizes all file I/O and JSON serialization logic so the rest of the
 * application does not need to worry about ObjectMapper configuration or IOException handling.
 */
public class FileService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Reads an inventory JSON file and returns a list of InventoryItemData objects.
     *
     * @param filePath path to the JSON file
     * @return list of inventory item data
     * @throws FileServiceException when the file cannot be read or JSON cannot be parsed
     */
    public List<InventoryItemData> readInventoryFromJson(String filePath) throws FileServiceException {
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                throw new FileServiceException("Inventory file not found: " + filePath);
            }

            return objectMapper.readValue(file, new TypeReference<List<InventoryItemData>>() {});
        } catch (IOException ex) {
            throw new FileServiceException("Failed to read or parse inventory JSON file: " + filePath, ex);
        }
    }

    /**
     * Writes a list of InventoryItemData objects to a JSON file.
     *
     * This supports the milestone requirement to handle JSON serialization properly,
     * even if your current program flow only uses reading.
     *
     * @param filePath path to write the JSON file
     * @param items items to serialize
     * @throws FileServiceException when the file cannot be written or JSON cannot be created
     */
    public void writeInventoryToJson(String filePath, List<InventoryItemData> items) throws FileServiceException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), items);
        } catch (IOException ex) {
            throw new FileServiceException("Failed to write inventory JSON file: " + filePath, ex);
        }
    }
}