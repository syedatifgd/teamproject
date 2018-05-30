package com.example.syedatifhussain.myapplication;

/**
 * Created by SyedAtif on 5/13/2018.
 */

public class stories {
    public String description;
    public String priority;
    public String type;

    public stories() {
    }

    public stories(String description, String priority, String type) {
        this.description = description;
        this.priority = priority;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
