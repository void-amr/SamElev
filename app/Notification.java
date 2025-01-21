
public class Notification {
    private String message;
    private String taskId;

    // Default no-argument constructor (required by Firestore)
    public Notification() {
    }

    // Constructor with arguments
    public Notification(String message, String taskId) {
        this.message = message;
        this.taskId = taskId;
    }

    // Getters and setters (required for Firestore to map fields)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
