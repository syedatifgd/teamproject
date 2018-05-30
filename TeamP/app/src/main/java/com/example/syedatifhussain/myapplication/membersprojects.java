package com.example.syedatifhussain.myapplication;

/**
 * Created by SyedAtif on 5/23/2018.
 */

public class membersprojects {
    public String name;
    public String role;

    public membersprojects(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public membersprojects() {
    }
}
