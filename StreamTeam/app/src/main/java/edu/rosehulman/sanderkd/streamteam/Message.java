package edu.rosehulman.sanderkd.streamteam;

/**
 * Created by sanderkd on 2/11/2016.
 */
public class Message {
    private String User;
    private int timeStamp;
    private String message;
    Message(String u, int t, String m){
        this.User = u;
        this.timeStamp = t;
        this.message = m;
    }
}
