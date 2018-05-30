package com.example.syedatifhussain.myapplication;

/**
 * Created by SyedAtif on 1/24/2018.
 */

public class Messages {
    public String message;
    public String type;
    public long time;
    private String pid;
    private String from;


    public Messages() {

    }

    public Messages(String message, String type, long time,String pid,String from) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.pid = pid;
        this.from=from;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
