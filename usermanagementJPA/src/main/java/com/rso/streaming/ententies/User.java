package com.rso.streaming.ententies;

import javax.persistence.*;

@Entity(name = "user")
@Table
@NamedQueries({
        @NamedQuery(name="User.findAll", query="SELECT u FROM user u"),
        @NamedQuery(name="User.findOne", query="SELECT u FROM user u WHERE u.name = :name AND u.password = :pass"),
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    private String name;
    private String password;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
