package com.understd.userdata;

/**
 * Created by understd on 13/02/17.
 */

public class ToDoList {
    private String date;
    private String desc;
    private String time;
    private String title;
    private String owner;

    public ToDoList() {

    }

    public ToDoList(String date, String desc, String time, String title, String owner) {
        this.date = date;
        this.desc = desc;
        this.time = time;
        this.title = title;
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
