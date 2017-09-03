package com.example.tapiwa.collabo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;



public class ChatActivity extends AppCompatActivity {


    private FloatingActionButton btn_send_msg;
    private TextView chat_conversation;
    private EditText input_msg;
    private Toolbar mToolBar;
    private ListView ChatsListView;
    private SwipeRefreshLayout  mSwipeRefreshLayout;
    private ArrayList<Message> messagesList;
    private ChatsAdapter mAdapter;
    private int TOTAL_MESSAGES_TO_LOAD = 10;
    private int mCurrentPage = 1;
    final int NOTIFICATION_BODY_MAX_LENGTH = 30;

    private String user_name, room_name, user_thumb_uri;
    private DatabaseReference root;
    public String temp_key;
    private ScrollView scrollView;
    public String chat_room_key;
    private String user;
    public static DatabaseReference mDatabaseReference;
    public static DatabaseReference mChatRoomDatabaseRoot;
    public static DatabaseReference mChatRoomNamesDatabase;
    public static final String CHAT_ROOM_CHATS_DB_PATH = "Chat_Rooms_Chats";
    private SharedPreferences sharedPreferences;
    public String chat_room_name;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messagesList = new ArrayList<>();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        chat_room_key = getIntent().getStringExtra("chat_room_key");



        btn_send_msg = (FloatingActionButton) findViewById(R.id.sendMessage);
        ChatsListView = (ListView)  findViewById(R.id.chatArea);

        mToolBar = (Toolbar) findViewById(R.id.chats_toolbar);
        mToolBar.setTitle("Discussion");
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new ChatsAdapter(ChatActivity.this, R.layout.activity_chats_item_list, messagesList);
        input_msg = (EditText) findViewById(R.id.typeMessage);
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
        mChatRoomNamesDatabase = FirebaseDatabase.getInstance().getReference().child(GroupChatsActivity.CHAT_ROOMS_DB_REF);
        ChatsListView.setAdapter(mAdapter);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        mDatabaseReference.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NewUser newuser = dataSnapshot.getValue(NewUser.class);

                user_name = newuser.getName();
                user_thumb_uri = newuser.getThumb_image();
                uid = newuser.getUid();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mChatRoomDatabaseRoot = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(CHAT_ROOM_CHATS_DB_PATH)
                .child(chat_room_key);

        mChatRoomDatabaseRoot
                .keepSynced(true);


        loadMessages();


        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });



        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;
                loadMessages();
            }
        });

    }


    @Override
    public void onResume(){
        super.onResume();
       // ShortcutBadger.removeCount(TagChats.this);

    }

  private void append_chat_conversation(DataSnapshot dataSnapshot) {
      Message receivedMessagge = dataSnapshot.getValue(Message.class);
  }



  public void loadMessages() {

      final Query loadSomeMessages = mChatRoomDatabaseRoot.limitToLast(mCurrentPage * TOTAL_MESSAGES_TO_LOAD);

      if (loadSomeMessages.equals(null)) {
          return;
      }

      loadSomeMessages.addChildEventListener(new ChildEventListener() {
          @Override
          public void onChildAdded(DataSnapshot dataSnapshot, String s) {
              //fetch image data from firebase
              Message message = dataSnapshot.getValue(Message.class);
              messagesList.add(message);
              mAdapter.notifyDataSetChanged();

              if(messagesList.size() > 4) {
                  scrollMyListViewToBottom();
              }

              mSwipeRefreshLayout.setRefreshing(false);
          }

          @Override
          public void onChildChanged(DataSnapshot dataSnapshot, String s) {

          }

          @Override
          public void onChildRemoved(DataSnapshot dataSnapshot) {

          }

          @Override
          public void onChildMoved(DataSnapshot dataSnapshot, String s) {

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });









  }

  public void sendMessage() {

      if(input_msg.getText().toString().length() == 0) {
          return;
      }

      temp_key = mChatRoomDatabaseRoot.push().getKey();
      DatabaseReference message_root = mChatRoomDatabaseRoot.child(temp_key);
    /*  Map<String, Object> new_message = new HashMap<String, Object>();
      new_message.put("name",user_name);
      new_message.put("msg", input_msg.getText().toString());
      new_message.put("time", timeSent); */


      Message message = new Message(input_msg.getText().toString(), GenericServices.timeStamp(), user_name, user_thumb_uri, uid);

      //update database
      message_root.setValue(message);

      //Notify other users in the chatrooom of the new message

    /*  String packet = trimText(input_msg.getText().toString());
      try {
          sendNotifications(user_name, packet, room_name, key);
      } catch (IOException e) {
          e.printStackTrace();
      } */

      input_msg.setText(null);
     // scrollToBottom();

  }

  private String trimText(String str) {
      if(str.length() > NOTIFICATION_BODY_MAX_LENGTH) {
        return  str.substring(0,NOTIFICATION_BODY_MAX_LENGTH) + "...";
      } else {
          return str;
      }
  }



    private void scrollMyListViewToBottom() {
        ChatsListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                ChatsListView.setSelection(mAdapter.getCount() - 1);
            }
        });
    }


    public static void sendNotifications(String username,String packetMessage, String chatRoom, String key) throws IOException {

        OkHttpClient client = new OkHttpClient();

        String type = "chat";

        RequestBody body = new FormBody.Builder()
                .add("userName", username)
                .add("type", type)
                .add("chatRoom", chatRoom)
                .add("packetMessage",packetMessage)
                .add("key", key)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.43.229/test/pushNotifications.php")
                .post(body)
                .build();

        client.newCall(request)
                .enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                    }
                });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
