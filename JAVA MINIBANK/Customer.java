import java.io.Serializable;

public class Customer implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String email;
    private String mobile;
    @Id
    private final String customerid;

    private static long customercnt = 100;
    private Address address;

    public static class Address implements Serializable {

        private static final long serialVersionUID = 1L;

        private String line;
        private String city;
        private String pincode;

        public Address(String line, String city, String pincode) {
            this.line = line;
            this.city = city;
            this.pincode = pincode;
        }

        public String getLine() {
            return line;
        }

        public String getCity() {
            return city;
        }

        public String getPincode() {
            return pincode;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        @Override
        public String toString() {
            return line + ", " + city + " - " + pincode;
        }
    }

    private static String generateCustomerId() {
        customercnt++;
        return "CUST" + customercnt;
    }

    public Customer(String name, String email, String mobile) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.customerid = generateCustomerId();
    }

    public Customer(String name, String email, String mobile, Address address) {
        this(name, email, mobile);
        this.address = address;
    }

    @Override
    public Customer clone() {
        try {
            Customer copy = (Customer) super.clone();
            if (this.address != null) {
                copy.address = new Address(this.address.line, this.address.city, this.address.pincode);
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public String getName() {
        return name;
    }

    public String getname() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getemail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getmobile() {
        return mobile;
    }

    public String getCustomerId() {
        return customerid;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return customerid + " - " + name + " (" + email + ", " + mobile + ")"
                + (address != null ? " - Address: " + address : "");
    }
}
