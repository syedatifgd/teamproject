package com.example.syedatifhussain.myapplication;

/**
 * Created by SyedAtif on 5/13/2018.
 */

public class projects {

    public String code;
    public String description;
    public String head;
    public String uid;
    public String upass;

    public projects(String code, String description, String head, String uid, String upass) {
        this.code = code;
        this.description = description;
        this.head = head;
        this.uid = uid;
        this.upass = upass;
    }

    public projects() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUpass() {
        return upass;
    }

    public void setUpass(String upass) {
        this.upass = upass;
    }
}
