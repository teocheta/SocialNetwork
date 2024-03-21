package org.example.socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {
    private Utilizator from;

    private List<Utilizator> to;

    private String mesaj;

    private LocalDateTime data;

    private Message reply;

    public Message(Utilizator from,List<Utilizator> to,String mesaj,LocalDateTime data){
        this.from = from;
        this.to = to;
        this.mesaj = mesaj;
        this.data = data;
        this.reply = null;
    }

    public Message(Utilizator from,List<Utilizator> to,String mesaj,LocalDateTime data,Message reply){
        this.from = from;
        this.to = to;
        this.mesaj = mesaj;
        this.data = data;
        this.reply = reply;
    }

    public Utilizator getFrom() {
        return from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public String getMesaj() {
        return mesaj;
    }

    public LocalDateTime getData() {
        return data;
    }

    public Message getReply() {
        return reply;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo(), getMesaj(),getData(),getReply());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Message message = (Message) o;
        return Objects.equals(from, message.from) && Objects.equals(to, message.to) && Objects.equals(mesaj, message.mesaj) && Objects.equals(data, message.data) && Objects.equals(reply, message.reply);
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", mesaj='" + mesaj + '\'' +
                ", data=" + data +
                ", reply=" + reply +
                '}';
    }
}
