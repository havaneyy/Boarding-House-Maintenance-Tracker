public class BoardingHouse {

    private String name;
    private String address;

    public BoardingHouse(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void displayDetails() {
        System.out.println("Boarding House: " + name);
        System.out.println("Address: " + address);
    }
}
