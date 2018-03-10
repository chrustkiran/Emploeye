package chrust.emploeye;

/**
 * Created by Chrustkiran on 23/02/2018.
 */

public class User {
    String name;
    String email;
    String address;
    String company;
    String nic;

    public User(String name, String email, String address, String company, String nic) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.company = company;
        this.nic = nic;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getCompany() {
        return company;
    }

    public String getNic() {
        return nic;
    }
}
