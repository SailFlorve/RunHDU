package com.cxsj.runhdu.model;

/**
 * Created by chjyp on 2017/5/5.
 */

public class HelpItem {
    private String title;
    private String info;

    public HelpItem(String title, String info){
        this.title = title;
        this.info = info;
    }
    public String getTitle(){
        return title;
    }

    public String getInfo() {
        return info;
    }
}