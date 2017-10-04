package com.example.clases;

import java.util.Date;

/**
 * Created by juanpablorn30 on 2/10/17.
 */

public class Usuario {

    private String password;
    private String email;
    private Date date_birth;
    private String username;

    /**
     * @param password
     * @param email
     * @param date_birth
     * @param username
     */
    public Usuario(String password, String email, Date date_birth, String username) {
        this.password = password;
        this.email = email;
        this.date_birth = date_birth;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDate_birth() {
        return date_birth;
    }

    public void setDate_birth(Date date_birth) {
        this.date_birth = date_birth;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
