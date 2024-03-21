package org.example.socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Prietenie extends Entity<Tuple<Utilizator,Utilizator>> {

    private Utilizator user1;
    private Utilizator user2;

    private LocalDateTime friendsSince;

    /**
     * constructor pentru clasa Prietenie
     *
     * @param user1       - primul utilizator
     * @param user2       - al doilea utilizator

     */

    public Prietenie(Utilizator user1, Utilizator user2, LocalDateTime friendsSince) {
        this.setId(new Tuple<>(user1,user2));
        this.user1 = user1;
        this.user2 = user2;
        this.friendsSince = friendsSince;
    }



    /**
     * Getter pentru user1
     *
     * @return user1 - primul utilizator
     */
    public Utilizator getUser1() {
        return user1;
    }

    /**
     * Setter pentru user1
     *
     * @param user1 - noul user1
     */
    public void setUser1(Utilizator user1) {
        this.user1 = user1;
    }

    /**
     * Getter pentur user2
     *
     * @return user2 - al doilea utlizator
     */
    public Utilizator getUser2() {
        return user2;
    }

    /**
     * Setter pentru user2
     *
     * @param user2 - al doilea user2
     */
    public void setUser2(Utilizator user2) {
        this.user2 = user2;
    }

    /**
     * Returneaza data creearii prieteniei
     * @return friendsSince - data la care s-a creat prietenia
     */
    public LocalDateTime getFriendsSince() {
        return friendsSince;
    }

    @Override
    public String toString() {
        return "Prietenie{" +
                "user1=" + user1.getUserName() +
                ", user2=" + user2.getUserName() +
                ", friendsSince=" + friendsSince +
                //", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prietenie that)) return false;
        return Objects.equals(user1, that.user1) && Objects.equals(user2, that.user2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2, friendsSince);
    }
}


