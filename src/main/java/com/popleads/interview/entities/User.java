package com.popleads.interview.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity(name = "USER")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;

    private String password;

    @Column(name = "try")
    private Double tl;

    private Double usd;

    private Double eur;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<History> histories;

    public User() {}

    public User(String username, String password, Double tl, Double usd, Double eur) {
        this.username = username;
        this.password = password;
        this.tl = tl;
        this.usd = usd;
        this.eur = eur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getTl() {
        return tl;
    }

    public void setTl(Double tl) {
        this.tl = tl;
    }

    public Double getUsd() {
        return usd;
    }

    public void setUsd(Double usd) {
        this.usd = usd;
    }

    public Double getEur() {
        return eur;
    }

    public void setEur(Double eur) {
        this.eur = eur;
    }

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }
}
