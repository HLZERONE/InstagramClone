package com.example.instagramclone;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_USER = "user";
    static final String KEY_IMAGE = "image";

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile file){
        put(KEY_IMAGE, file);
    }

    public String getFormattedTimestamp(){
        return TimeFormatter.getTimeDifference(getCreatedAt().toString());
    }
}
