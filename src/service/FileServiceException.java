package service;

/**
 * Represents failures related to file I/O or JSON serialization/deserialization.
 *
 * This keeps the rest of the application clean because we can catch one
 * meaningful exception type for any inventory file operation.
 */
public class FileServiceException extends Exception {

    /**
     * Serial version identifier for this exception class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new FileServiceException with a message.
     *
     * @param message explanation of the failure
     */
    public FileServiceException(String message) {
        super(message);
    }

    /**
     * Creates a new FileServiceException with a message and root cause.
     *
     * @param message explanation of the failure
     * @param cause the original exception that caused this failure
     */
    public FileServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}