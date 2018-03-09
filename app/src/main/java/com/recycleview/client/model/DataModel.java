package com.recycleview.client.model;

/**
 * Created by hongmingwei on 2017/5/4 0004.
 */
public class DataModel {

    private int id;
    private String name;
    private String title;
    private String text;


    public DataModel(int id, String name){
        this.id = id;
        this.name = name;
    }
    public DataModel(int id, String name, String title){
        this.id = id;
        this.name = name;
        this.title = title;
    }
    public DataModel(int id, String name, String title, String text){
        this.id = id;
        this.name = name;
        this.title = title;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
