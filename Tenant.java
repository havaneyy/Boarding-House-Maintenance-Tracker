public class Tenant extends User {

    private String roomNumber;

    public Tenant(String id, String firstName, String lastName,
                  String email, String username, String password,
                  String roomNumber) {
        super(id, firstName, lastName, email, username, password);
        this.roomNumber = roomNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public void displayDetails() {
        System.out.println("Tenant ID: " + getId());
        System.out.println("Full Name: " + getFullName());
        System.out.println("Email: " + getEmail());
        System.out.println("Room Number: " + roomNumber);
    }
}
