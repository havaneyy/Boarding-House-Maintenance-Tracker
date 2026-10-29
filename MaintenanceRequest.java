public class MaintenanceRequest {
    private String id, tenantId, tenantName, roomNumber, issueType, description, priority, date, status;

    public MaintenanceRequest(String id, String tenantId, String tenantName, String roomNumber,
                              String issueType, String description, String priority, String date) {
        this.id = id; this.tenantId = tenantId; this.tenantName = tenantName; this.roomNumber = roomNumber;
        this.issueType = issueType; this.description = description; this.priority = priority; this.date = date; this.status = "PENDING";
    }

    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getTenantName() { return tenantName; }
    public String getRoomNumber() { return roomNumber; }
    public String getIssueType() { return issueType; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Text block shown to staff on the requests page.
    public String getDetails() {
        return "Request ID: " + id
                + "\nTenant: " + tenantName
                + "\nRoom: " + roomNumber
                + "\nIssue: " + issueType
                + "\nDescription: " + description
                + "\nPriority: " + priority
                + "\nDate: " + date
                + "\nStatus: " + status;
    }

    public String toFile() { return id + "|" + tenantId + "|" + tenantName + "|" + roomNumber + "|" + issueType + "|" + description + "|" + priority + "|" + date + "|" + status; }
}
