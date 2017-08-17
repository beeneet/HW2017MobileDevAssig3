package com.bkoirala.howardchat;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageSource {
    private Context mContext;
    private static MessageSource mMsgSource;

    public interface  MessageListener{
        void onMessageReceived(List<Message> mMessageList);
    }

    private MessageSource(Context context){
        mContext = context;
    }

    public static MessageSource get(Context context){
        if (mMsgSource == null){
            mMsgSource = new MessageSource(context);
        }
        return mMsgSource;
    }



    public void getMessages(final MessageListener messageListener) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messageRef = databaseRef.child("messages");
        Query last150messages = messageRef.limitToLast(150);
        last150messages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> newMessages = new ArrayList<Message>();
                Iterable<DataSnapshot> iter = dataSnapshot.getChildren();
                for (DataSnapshot messageSnapshot : iter){
                    if ((messageSnapshot.child("content").getValue() != null) && (messageSnapshot.child("fromUserId").getValue() != null)) {
                        Message newMessage = new Message(messageSnapshot);
                        newMessages.add(newMessage);
                    }
                }
                messageListener.onMessageReceived(newMessages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void sendMessage(Message message) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messageRef = databaseRef.child("messages");
        DatabaseReference newMessageRef = messageRef.push();
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("content", message.getmContent());
        messageMap.put("fromUserId", message.getmUserId());
        messageMap.put("fromUserName", message.getmUserName());
        newMessageRef.setValue(messageMap);
    }

}
