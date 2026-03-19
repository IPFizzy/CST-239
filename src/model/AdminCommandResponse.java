package model;

/**
 * Represents a response returned by the administration service.
 */
public class AdminCommandResponse {

    private boolean success;
    private String message;
    private String payload;

    /**
     * Default constructor required for JSON deserialization.
     */
    public AdminCommandResponse() {
    }

    /**
     * Creates a new administration response.
     *
     * @param success true when the request succeeded
     * @param message response message
     * @param payload optional response payload
     */
    public AdminCommandResponse(boolean success, String message, String payload) {
        this.success = success;
        this.message = message;
        this.payload = payload;
    }

    /**
     * @return true when the request succeeded
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success success flag
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return response message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message response message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return response payload
     */
    public String getPayload() {
        return payload;
    }

    /**
     * @param payload response payload
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }
}
