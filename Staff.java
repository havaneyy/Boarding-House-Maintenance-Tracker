public class Staff extends User {
    public Staff(String id, String firstName, String lastName, String email,
                 String username, String password) {
        super(id, firstName, lastName, email, username, password);
    }

    @Override
    public void displayDetails() {
        System.out.println("Staff ID: " + getId());
        System.out.println("Full Name: " + getFullName());
        System.out.println("Email: " + getEmail());
    }
}
