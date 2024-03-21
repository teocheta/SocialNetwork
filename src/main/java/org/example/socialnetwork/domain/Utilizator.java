package org.example.socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private List<Utilizator> friends;

    /**
     * Constructorul clasei Utilizator
     * @param firstName,String
     * @param lastName,String
     * @param userName,String
     */
    public Utilizator(String firstName, String lastName,String userName,String password) {
        // super.setId(Long);
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.friends = new ArrayList<>();
    }

    /**
     * getter pentru prenume
     * @return firstName - prenumele utilizatorului
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *  setter pentru prenume
     * @param firstName -noul prenume
     */

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *  getter pentru nume
     * @return lastName, numele utilizatorului
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *  setter pentru nume
     * @param lastName - noul nume
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * getter pentru username
     * @return userName -usernameul utilizatorului
     */
    public String getUserName(){
        return userName;
    }
    /**
     *  setter pentru username
     * @param userName -noul username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * returneaza prietenii utilizatorului
     * @return lista prietenilor utilizatorului
     */
    public List<Utilizator> getFriends() {
        return friends;
    }

    public String getPassword(){return password;}

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Utilizator{" +
                "firstName='" + firstName +
                ", lastName='" + lastName +
                ", userName=" + userName +
                //", friends=" + friends +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) && getUserName().equals(that.getUserName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getUserName());
    }

    /**
     * adauga un prieten utilizatorului
     * @param utilizator -utilizatorul de adaugat
     */
    public void addFriend(Utilizator utilizator) {
        this.friends.add(utilizator);
    }

    /**
     * sterge un prieten de-al utilizatorului
     * @param utilizator -utilizatorul de sters
     */

    public void deleteFriend(Utilizator utilizator){
        this.friends.remove(utilizator);
    }
}

