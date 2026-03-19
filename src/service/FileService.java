package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.InventoryItemData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Handles JSON file reading, writing, and object conversion.
 *
 * This class centralizes JSON-related logic so the rest of the application
 * can stay focused on business behavior.
 */
public class FileService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Reads an inventory JSON file into item data objects.
     *
     * @param filePath file path
     * @return inventory item data list
     * @throws FileServiceException when file reading or parsing fails
     */
    public List<InventoryItemData> readInventoryFromJson(String filePath) throws FileServiceException {
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                throw new FileServiceException("Inventory file not found: " + filePath);
            }

            return objectMapper.readValue(file, new TypeReference<List<InventoryItemData>>() { });
        } catch (IOException ex) {
            throw new FileServiceException("Failed to read or parse inventory JSON file: " + filePath, ex);
        }
    }

    /**
     * Writes inventory item data to a JSON file.
     *
     * @param filePath file path
     * @param items item data to write
     * @throws FileServiceException when writing fails
     */
    public void writeInventoryToJson(String filePath, List<InventoryItemData> items) throws FileServiceException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), items);
        } catch (IOException ex) {
            throw new FileServiceException("Failed to write inventory JSON file: " + filePath, ex);
        }
    }

    /**
     * Converts a JSON string into a list of inventory item data.
     *
     * @param json JSON string
     * @return parsed item data list
     * @throws FileServiceException when parsing fails
     */
    public List<InventoryItemData> parseInventoryJsonString(String json) throws FileServiceException {
        try {
            return objectMapper.readValue(json, new TypeReference<List<InventoryItemData>>() { });
        } catch (IOException ex) {
            throw new FileServiceException("Failed to parse inventory JSON payload.", ex);
        }
    }

    /**
     * Converts item data to a JSON string.
     *
     * @param items item data list
     * @return JSON string
     * @throws FileServiceException when conversion fails
     */
    public String inventoryItemsToJson(List<InventoryItemData> items) throws FileServiceException {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(items);
        } catch (JsonProcessingException ex) {
            throw new FileServiceException("Failed to convert inventory to JSON string.", ex);
        }
    }

    /**
     * Reads a JSON file as raw text.
     *
     * @param filePath file path
     * @return raw JSON string
     * @throws FileServiceException when the file cannot be read
     */
    public String readInventoryJsonAsString(String filePath) throws FileServiceException {
        try {
            return Files.readString(Path.of(filePath));
        } catch (IOException ex) {
            throw new FileServiceException("Failed to read JSON file as text: " + filePath, ex);
        }
    }

    /**
     * Converts an object to a JSON string.
     *
     * @param value object to convert
     * @return JSON string
     * @throws FileServiceException when conversion fails
     */
    public String toJson(Object value) throws FileServiceException {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new FileServiceException("Failed to convert object to JSON.", ex);
        }
    }

    /**
     * Converts a JSON string to an object of the requested type.
     *
     * @param json JSON string
     * @param clazz target class
     * @param <T> type to return
     * @return parsed object
     * @throws FileServiceException when parsing fails
     */
    public <T> T fromJson(String json, Class<T> clazz) throws FileServiceException {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException ex) {
            throw new FileServiceException("Failed to convert JSON to object.", ex);
        }
    }
}
