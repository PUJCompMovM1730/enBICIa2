package com.example.clases;

import java.util.Date;

/**
 * Created by juanpablorn30 on 2/10/17.
 */

public abstract  class Usuario {

    protected String password;
    protected String email;
    protected Date date_birth;
    protected String username;
    protected String name;

    public Usuario() {
    }

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

    public Usuario(String name, String email, Date date_birth) {
        this.name = name;
        this.email = email;
        this.date_birth = date_birth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getDate_birth() {
        if(date_birth == null) return 0L;
        return date_birth.getTime();
    }

    public void setDate_birth(Date date_birth) {
        this.date_birth = date_birth;
    }
}
