package model;

public class Patient {

    private int id;
    private String name;
    private String dob;
    private String contact;
    private String address;
    private int cityId;
    private int genderId;
    private String diagnose;

    // Constructor
    public Patient(int id, String name, String dob, String contact, String address,
            int cityId, int genderId, String diagnose) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.contact = contact;
        this.address = address;
        this.cityId = cityId;
        this.genderId = genderId;
        this.diagnose = diagnose;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public String getContact() {
        return contact;
    }

    public String getAddress() {
        return address;
    }

    public int getCityId() {
        return cityId;
    }

    public int getGenderId() {
        return genderId;
    }

    public String getDiagnose() {
        return diagnose;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setGenderId(int genderId) {
        this.genderId = genderId;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }
}
