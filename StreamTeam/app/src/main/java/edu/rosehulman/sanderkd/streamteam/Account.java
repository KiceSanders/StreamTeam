package edu.rosehulman.sanderkd.streamteam;

/**
 * Created by sanderkd on 2/16/2016.
 */
public class Account {
    private String mType;
    private String mName;
    private String mPicture;
    private boolean on;
    public Account(String type, String name, String picture, boolean o){
        mType = type;
        mName = name;
        mPicture = picture;
        on = o;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPicture() {
        return mPicture;
    }

    public void setmPicture(String mPicture) {
        this.mPicture = mPicture;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
