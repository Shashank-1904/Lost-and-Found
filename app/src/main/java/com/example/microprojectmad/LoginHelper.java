package com.example.microprojectmad;

public class LoginHelper {
    String name;
    String email;
    String pass;
    String role;
    String cnfpass;

    public LoginHelper() {
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

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCnfpass() {
        return cnfpass;
    }

    public void setCnfpass(String cnfpass) {
        this.cnfpass = cnfpass;
    }

    public LoginHelper(String name, String email, String pass, String cnfpass, String role) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.role = role;
        this.cnfpass = cnfpass;
    }
}
