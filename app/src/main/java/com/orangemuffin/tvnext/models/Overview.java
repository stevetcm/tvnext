package com.orangemuffin.tvnext.models;

import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 2/6/2017 */
public class Overview {
    private String header;
    private String id;
    private String text;
    private String text2;
    private int order;
    private List<Actor> actors = new ArrayList<>();
    private List<String> fanarts = new ArrayList<>();

    private String poster;

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

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<String> getFanarts() {
        return fanarts;
    }

    public void setFanarts(List<String> fanarts) {
        this.fanarts = fanarts;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
