package com.example.tapiwa.collabo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.RecursiveAction;

import static com.facebook.FacebookSdk.getApplicationContext;


public class ChatsFragment extends Fragment {

    private FloatingActionButton mSearchForNewBuddies;
   //
   //
   // +6private ListView activeChatsListView;
    private DatabaseReference mRequestsDatabase;
    private String mCurrent_user_id;
    private View mMainView;
    private ChatListAdapter adapter;
    private FirebaseAuth Auth;
    private ArrayList<NewChat> list;
    private String userID;
    private final String DIRECT_CHAT_FIREBASE_REFERENCE = "Direct_Chat_List";
    private FirebaseDatabase firebaseDatabase;
    public static  DatabaseReference personalChatsDbRef;
    private RecyclerView mChatsList;
    private static final int VERTICAL_ITEM_SPACE = 48;



    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.activity_active_chats, container, false);



        firebaseDatabase = FirebaseDatabase.getInstance();
        list = new ArrayList<>();
        Auth = FirebaseAuth.getInstance();
        userID = Auth.getCurrentUser().getUid();
        mChatsList = (RecyclerView) mMainView.findViewById(R.id.active_chats_list);


        mChatsList.setHasFixedSize(true);
        mChatsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mChatsList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mChatsList.setItemAnimator(new DefaultItemAnimator());


        personalChatsDbRef = firebaseDatabase.getReference(DIRECT_CHAT_FIREBASE_REFERENCE).child(userID);






      /*  activeChatsListView = (ListView) mMainView.findViewById(R.id.active_chats_list);
        adapter = new ChatListAdapter(mMainView.getContext(), R.layout.chat_list_item, list);
        activeChatsListView.setAdapter(adapter);



        personalChatsDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    NewChat newChat = Snapshot1.getValue(NewChat.class);
                    list.add(newChat);
                }

                adapter = new ChatListAdapter(getApplicationContext(), R.layout.buddies_item_list, list);
                activeChatsListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        /*personalChatsDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

      /*  personalChatsDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                //fetch from firebase
                    NewChat chats =  dataSnapshot.getValue(NewChat.class);

                list.add(chats);
                adapter.notifyDataSetChanged();
              //  Collections.reverse(list);
              //  adapter.notifyDataSetChanged();
              //  adapter = new ChatListAdapter(getApplicationContext(), R.layout.chat_list_item, list);
             //   activeChatsListView.setAdapter(adapter);
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
        }); */



      //  activeChatsListView = (ListView) mMainView.findViewById(R.id.active_chats_list);
       // mCurrent_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            return mMainView;
    }



    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<NewChat, ChatsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NewChat, ChatsViewHolder>
                (NewChat.class, R.layout.chat_list_item, ChatsViewHolder.class, personalChatsDbRef) {
            @Override
            protected void populateViewHolder(final ChatsViewHolder viewHolder, final NewChat newChat, int position) {
                viewHolder.setName(newChat.getName());
                viewHolder.setImage(newChat.getThumbUri(), getContext());
                viewHolder.setChatKey(newChat.getChatKey());

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference onlinestatusCheck;
                onlinestatusCheck = database.getReference(OneToOneChats.ONLINE_STATUS).child(newChat.getChatKey());

                onlinestatusCheck.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.getValue(OnlineStatus.class).status;
                        viewHolder.setOnlineStatus(status);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                onlinestatusCheck.keepSynced(true);
            }
        };

        mChatsList.setAdapter(firebaseRecyclerAdapter);
    }



    public static class ChatsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        String chatKey;

        public ChatsViewHolder(View itemView) {
        super(itemView);

            mView = itemView;
            }

            public void setChatKey(String chatKey) {
                this.chatKey = chatKey;
            }

            public void setName(String name) {
                TextView chatName = (TextView)mView.findViewById(R.id.chat_item_list_name);
                chatName.setText(name);
            }

            public void setOnlineStatus(String chatOnlinestatus) {
                TextView chatstatus = (TextView)mView.findViewById(R.id.chats_fragment_last_seen);
                ImageView onlineIcon = (ImageView)mView.findViewById(R.id.chats_fragment_online_status);

                if(chatOnlinestatus.equals("online")) {
                    chatstatus.setTextColor(Color.GREEN);
                    onlineIcon.setVisibility(View.VISIBLE);
                } else {
                    chatstatus.setTextColor(Color.BLACK);
                    onlineIcon.setVisibility(View.INVISIBLE);
                }
                chatstatus.setText(chatOnlinestatus);
            }

            public void setImage(final String thumbUri, final Context context) {

                final ImageView imageView = (ImageView)mView.findViewById(R.id.chat_item_list_image);

                Picasso.with(context)
                        .load(thumbUri)
                        .placeholder(R.drawable.new_default_image)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError() {
                                // Try again online if cache failed
                                Picasso.with(context)
                                        .load(thumbUri)
                                        .placeholder(R.drawable.new_default_image)
                                        .into(imageView);
                            }
                        });

                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       Intent openChat = new Intent(getApplicationContext(), OneToOneChats.class);
                        openChat.putExtra("chat_room_key", chatKey);
                        getApplicationContext().startActivity(openChat);

                    }
                });

        }

            }


    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }


    }

