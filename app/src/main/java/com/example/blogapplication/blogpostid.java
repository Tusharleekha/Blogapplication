package com.example.blogapplication;

public class blogpostid {

    public String blogpostid;

    public <T extends blogpostid> T withId(final String id)
    {
        this.blogpostid=id;
        return (T) this;
    }
}
