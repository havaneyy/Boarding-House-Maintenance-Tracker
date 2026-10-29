public class Facility {
    private String id, name, status;
    public Facility(String id, String name, String status) { this.id = id; this.name = name; this.status = status; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public void setName(String name) { this.name = name; }
    public void setStatus(String status) { this.status = status; }
    public String toFile() { return id + "|" + name + "|" + status; }
    public String toString() { return "Facility ID: " + id + "\nName: " + name + "\nStatus: " + status; }
}
