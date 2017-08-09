package com.orangemuffin.tvnext.models;

import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 7/31/2017 */
public class Overview {
    private int id;
    private String header;
    private String text;
    private List<String> backgrounds = new ArrayList<>();
    private List<Actor> actors = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getBackgrounds() {
        return backgrounds;
    }

    public void setBackgrounds(List<String> backgrounds) {
        this.backgrounds = backgrounds;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }
}
