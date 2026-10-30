public class MaintenanceHistory {

    private String requestId;
    private String tenantName;
    private String roomNumber;
    private String issueType;
    private String description;
    private String priority;
    private String date;

    public MaintenanceHistory(String requestId, String tenantName,
                              String roomNumber, String issueType,
                              String description, String priority,
                              String date) {
        this.requestId = requestId;
        this.tenantName = tenantName;
        this.roomNumber = roomNumber;
        this.issueType = issueType;
        this.description = description;
        this.priority = priority;
        this.date = date;
    }

    public String getRequestId() {
        return requestId;
    }

    // The text block used by the GUI history page (and by displayDetails below).
    public String getDetails() {
        return "Request ID: " + requestId
                + "\nTenant: " + tenantName
                + "\nRoom: " + roomNumber
                + "\nIssue: " + issueType
                + "\nDescription: " + description
                + "\nPriority: " + priority
                + "\nDate: " + date
                + "\nStatus: COMPLETED";
    }

    // Console version, handy for testing.
    public void displayDetails() {
        System.out.println("----------------------------------------");
        System.out.println(getDetails());
        System.out.println("----------------------------------------");
    }

    public String toFile() {
        return requestId + "|" + tenantName + "|" + roomNumber + "|"
                + issueType + "|" + description + "|" + priority + "|" + date;
    }
}
