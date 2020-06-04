package com.example.blogapplication;

import java.util.Date;

public class blogpost extends blogpostid
{
    public String user_id,imageurl,desc;
    public Date timestamp;



    public blogpost() {
    }

    public blogpost(String user_id, String imageurl, String desc,Date timestamp) {
        this.user_id = user_id;
        this.imageurl = imageurl;
        this.desc = desc;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }


    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }



}
