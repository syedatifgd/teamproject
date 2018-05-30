package com.example.syedatifhussain.myapplication;

/**
 * Created by SyedAtif on 1/23/2018.
 */

public class users {
    public String name;
    public String image;
    public String role;
    public String phone;

    public users() {
    }

    public users(String name, String image, String role, String phone) {
        this.name = name;
        this.image = image;
        this.role = role;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

}
