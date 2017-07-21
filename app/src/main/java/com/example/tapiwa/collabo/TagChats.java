package com.example.tapiwa.collabo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;



public class TagChats extends AppCompatActivity {


    private FloatingActionButton btn_send_msg;
    private TextView chat_conversation;
    private EditText input_msg;
    private String timeSent;
   private SharedPreferences sharedPreferences;
    final int NOTIFICATION_BODY_MAX_LENGTH = 30;

    private String user_name, room_name;
    private DatabaseReference root;
    public String temp_key;
    private ScrollView scrollView;
    public String key;
    private String profilename;
    SortMessages sortMessages;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_chats);
        ShortcutBadger.removeCount(TagChats.this);


        btn_send_msg = (FloatingActionButton) findViewById(R.id.sendMessage);
        input_msg = (EditText) findViewById(R.id.typeMessage);
        chat_conversation = (TextView) findViewById(R.id.chatArea);
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(TagChats.this);
        scrollView = ((ScrollView) findViewById(R.id.scrollView));

        sortMessages = new SortMessages(getApplicationContext());


        room_name = getIntent().getExtras().get("chatRoom").toString();
        key = getIntent().getStringExtra("key");
        profilename = getIntent().getStringExtra("profilename");

        user_name =  "@" + sharedPreferences.getString("example_text", null);

        setTitle(room_name);

        root = FirebaseDatabase.getInstance().getReference().child(room_name);
        root.keepSynced(true);


        input_msg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            sendMessage();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });



        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
              //  append_chat_conversation(dataSnapshot);
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


    @Override
    public void onResume(){
        super.onResume();
        ShortcutBadger.removeCount(TagChats.this);

    }

    private String chat_msg, chat_user_name;

  private void append_chat_conversation(DataSnapshot dataSnapshot) {

      Iterator i = dataSnapshot.getChildren().iterator();

      while (i.hasNext()) {

          chat_msg = (String) ((DataSnapshot) i.next()).getValue();
          chat_user_name = (String) ((DataSnapshot) i.next()).getValue();
          timeSent = (String) ((DataSnapshot) i.next()).getValue();


          chat_conversation.append(Html.fromHtml("<b>" + chat_user_name + "</b>"
                  + ": " + chat_msg + "<br />"
                  + "<small align = \"right\"> " + timeSent + "</small>" + "<br />"));

          scrollToBottom();

      }
  }

  public void sendMessage() {

      Map<String, Object> map = new HashMap<String, Object>();
      temp_key = root.push().getKey();
      root.updateChildren(map);

      timeSent = Collabos.getTime();

      DatabaseReference message_root = root.child(temp_key);
      Map<String, Object> map2 = new HashMap<String, Object>();
      map2.put("name",user_name);
      map2.put("msg", input_msg.getText().toString());
      map2.put("time", timeSent);

      //update database
      message_root.updateChildren(map2);

      //Notify other users in the chatrooom of the new message

      String packet = trimText(input_msg.getText().toString());
      try {
          sendNotifications(user_name, packet, room_name, key);
      } catch (IOException e) {
          e.printStackTrace();
      }

      input_msg.setText("");
      scrollToBottom();

  }

  private String trimText(String str) {
      if(str.length() > NOTIFICATION_BODY_MAX_LENGTH) {
        return  str.substring(0,NOTIFICATION_BODY_MAX_LENGTH) + "...";
      } else {
          return str;
      }
  }


  private void scrollToBottom() {
      scrollView.post(new Runnable() {
          @Override
          public void run() {
              scrollView.fullScroll(ScrollView.FOCUS_DOWN);
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



}
