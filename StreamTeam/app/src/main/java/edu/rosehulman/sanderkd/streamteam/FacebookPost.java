package edu.rosehulman.sanderkd.streamteam;

/**
 * Created by derrowap on 2/11/2016.
 */
public class FacebookPost {
    public String message;
    public String story;
    public String time;

    public FacebookPost(String m, String s, String t) {
        this.message = m;
        this.story = s;
        this.time = t;
    }

    public String getMessage(){
        return this.message;
    }

    public String getStory(){
        return this.story;
    }

    public String getTime(){
        return this.time;
    }
}
