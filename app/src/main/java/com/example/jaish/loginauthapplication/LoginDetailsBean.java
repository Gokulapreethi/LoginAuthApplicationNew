package com.example.jaish.loginauthapplication;

import java.io.Serializable;

/**
 * Created by Jaish on 14-12-2018.
 */

public  class LoginDetailsBean implements Serializable,Cloneable {
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    private String id;
    private String name;
    private String emailid;
    private String password;
    private String mobileno;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }
}
