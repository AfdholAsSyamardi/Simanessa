
package model;

public class Guru extends User {

    public Guru(String username, String password) {
        super(username, password);
    }

    @Override
    public String showRole() {
        return "Guru";
    }
    
}
