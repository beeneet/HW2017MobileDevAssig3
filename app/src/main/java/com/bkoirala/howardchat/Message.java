package com.bkoirala.howardchat;

import com.google.firebase.database.DataSnapshot;


public class Message {

    protected String mUserName;
    protected String mUserId;
    protected String mContent;

    public String getmUserName() {
        return mUserName;
    }

    public String getmUserId() {
        return mUserId;
    }

    public String getmContent() {
        return mContent;
    }

    public Message(String mUserName, String mUserId, String mContent){
        this.mUserName = mUserName;
        this.mUserId = mUserId;
        this.mContent = mContent;
    }

    public Message(DataSnapshot mDataSnapShot){
        mUserName = mDataSnapShot.child("fromUserName").getValue().toString();
        mUserId = mDataSnapShot.child("fromUserId").getValue().toString();
        mContent = mDataSnapShot.child("content").getValue().toString();
    }


}
