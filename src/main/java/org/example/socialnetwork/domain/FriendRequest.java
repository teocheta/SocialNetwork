package org.example.socialnetwork.domain;

import java.util.Objects;

public class FriendRequest extends Entity<Tuple<Utilizator,Utilizator>> {

    private Utilizator user1;
    private Utilizator user2;
    private FriendRequestStatus status;

    public FriendRequest(Utilizator user1,Utilizator user2,FriendRequestStatus status){
        this.user1 = user1;
        this.user2 = user2;
        this.status = status;
        this.setId(new Tuple<>(user1,user2));
    }

    public Utilizator getUser1() {
        return user1;
    }

    public void setUser1(Utilizator user1) {
        this.user1 = user1;
    }

    public Utilizator getUser2() {
        return user2;
    }

    public void setUser2(Utilizator user2) {
        this.user2 = user2;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "user1=" + user1 +
                ", user2=" + user2 +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendRequest that)) return false;
        return Objects.equals(user1, that.user1) && Objects.equals(user2, that.user2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2, status);
    }
}
