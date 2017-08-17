package com.bkoirala.howardchat;

/**
 * Created by bkoirlal on 8/7/17.
 */


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        // Get references to text views to display database data.
        final ListView mListview = v.findViewById(R.id.list_view);
        final EditText mEditText = v.findViewById(R.id.edit_text_msg);
        final Button mButton = v.findViewById(R.id.send_btn);
        MessageSource.get(getContext()).getMessages(new MessageSource.MessageListener() {
            @Override
            public void onMessageReceived(List<Message> mMessageList) {
                ChatAdapter adapter = new ChatAdapter(getContext());
                adapter.setItems(mMessageList);
                mListview.setAdapter(adapter);
                mListview.setSelection(adapter.getCount() - 1);
            }
        });

        mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fbUser == null){
                    Toast.makeText(getContext(), "Not logged in. Message not sent", Toast.LENGTH_SHORT);
                    return;
                }
                Message newMessage = new Message(fbUser.getDisplayName(), fbUser.getUid(), mEditText.getText().toString());
                mEditText.setText("");
                MessageSource.get(getContext()).sendMessage(newMessage);

            }
        });


        return v;
    }

    private class ChatAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<Message> mDataSource;
        private TextView mSender;
        private TextView mMessage;

        public ChatAdapter(Context context) {
            mContext = context;
            mDataSource = new ArrayList<>();
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setItems(List<Message> chatList) {
            mDataSource.clear();
            mDataSource.addAll(chatList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() { return mDataSource.size(); }

        @Override
        public Object getItem(int position) { return mDataSource.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Message chat = mDataSource.get(position);
            View rowView = mInflater.inflate(R.layout.list_item_msg, parent, false);
            mSender = (TextView) rowView.findViewById(R.id.user_id);
            mMessage = (TextView) rowView.findViewById(R.id.user_msg);
            mSender.setText(chat.getmUserName());
            mMessage.setText(chat.getmContent());
            return rowView;
        }
    }



}
