package model;

/**
 * Represents an administration request sent over the network.
 *
 * The command field identifies the action.
 * The payload field carries any JSON data needed for that action.
 */
public class AdminCommandRequest {

    private String command;
    private String payload;

    /**
     * Default constructor required for JSON deserialization.
     */
    public AdminCommandRequest() {
    }

    /**
     * Creates a new administration request.
     *
     * @param command command code, such as U or R
     * @param payload request payload
     */
    public AdminCommandRequest(String command, String payload) {
        this.command = command;
        this.payload = payload;
    }

    /**
     * @return command code
     */
    public String getCommand() {
        return command;
    }

    /**
     * @param command command code
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * @return request payload
     */
    public String getPayload() {
        return payload;
    }

    /**
     * @param payload request payload
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }
}
