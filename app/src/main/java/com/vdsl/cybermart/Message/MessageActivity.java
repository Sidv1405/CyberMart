package com.vdsl.cybermart.Message;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.annotations.concurrent.Background;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Account.Model.UserModel;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.MainActivity;
import com.vdsl.cybermart.Message.Adapter.MessageAdapter;
import com.vdsl.cybermart.Message.Model.Chat;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.ActivityMessageBinding;
import com.vdsl.cybermart.databinding.ActivityVoucherBinding;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding binding;

    FirebaseUser firebaseUser;

    MessageAdapter messageAdapter;

    ArrayList<Chat> messageList = new ArrayList<>();

    DatabaseReference reference;

    Intent intent;

    ValueEventListener seenListener;

    String userEmail,userFCM;

    private Drawable defaultBackground;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> {
            Intent intent1 = new Intent(MessageActivity.this, MainActivity.class);
            startActivity(intent1);
        });

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        binding.rcvChat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessageActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rcvChat.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(true);


        intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        userFCM = intent.getStringExtra("fcmToken");
        Log.d("Tagne", "onCreate: " + userEmail);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.btnSend.setOnClickListener(v -> {
            String msg = binding.edtMessage.getText().toString();
            if (!msg.equals("")) {
                sendMessage(firebaseUser.getEmail(), userEmail, msg);
            } else {
                Toast.makeText(this, "Can't Send Message", Toast.LENGTH_SHORT).show();
            }
            binding.edtMessage.setText("");
        });

        getIdFromEmail(userEmail, new FragmentMessage.OnIdReceivedListener() {
            @Override
            public void onIdReceived(String id) {
                if (id != null) {
                    reference = FirebaseDatabase.getInstance().getReference("Account").child(id);

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d("FragmentMessage", "Snapshot value: " + firebaseUser.getUid());
                            UserModel user = snapshot.getValue(UserModel.class);

                            if (user != null) {
                                binding.txtUsername.setText(user.getFullName());
                                SharedPreferences pref = getSharedPreferences("user",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("avatar", user.getAvatar());
                                editor.commit();

                            }if (user.getAvatar() != null && !user.getAvatar().isEmpty() ){
                                Picasso.get().load(user.getAvatar()).into(binding.imgProfile, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("check47", "onSuccess: " + user.getAvatar());
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        binding.imgProfile.setImageResource(R.drawable.img_default_profile_image);
                                    }
                                });
                            }else {
                                binding.imgProfile.setImageResource(R.drawable.img_default_profile_image);
                            }
                            readMessage(firebaseUser.getEmail(), userEmail);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý khi có lỗi xảy ra trong quá trình truy vấn
                        }
                    });
                } else {
                    Log.d("FragmentMessage", "No user found with this email");
                }
            }
        });
        seenMessage(userEmail);
        defaultBackground = ContextCompat.getDrawable(this, R.drawable.bg_chat);
        int backgroundColor = sharedPreferences.getInt("backgroundColor", R.drawable.bg_chat);
        binding.rcvChat.setBackground(ContextCompat.getDrawable(this, backgroundColor));
    }

    private void getIdFromEmail(String email, final FragmentMessage.OnIdReceivedListener listener) {
        DatabaseReference peopleReference = FirebaseDatabase.getInstance().getReference().child("Account");
        peopleReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String accountId = null;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        accountId = dataSnapshot1.getKey();
                        Log.d("TAG", "onDataChange: sdone" + accountId);
                        break;
                    }
                }
                listener.onIdReceived(accountId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onIdReceived(null);
            }
        });
    }

    public interface OnIdReceivedListener {
        void onIdReceived(String id);
    }

    private void seenMessage(final String userId) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chat chat = snapshot1.getValue(Chat.class);
                    Log.e("checkRe", "onDataChange: " + firebaseUser.getEmail());
                    Log.e("checkRe", "onDataChange: " + chat.getReceiver());
                    Log.e("checkRe", "onDataChange: " + userId);


                    if (chat.getReceiver().equals(firebaseUser.getEmail()) && chat.getSender().equals(userId)) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("statusSeen", true);
                        snapshot1.getRef().updateChildren(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String encodeString(String string) {
        return string.replace(".", ",");
    }


    private void sendMessage(String sender, String receiver, String mesage) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", sender);
        map.put("receiver", receiver);
        map.put("message", mesage);
        map.put("statusSeen", false);

        reference1.child("Chats").push().setValue(map);
        sendNotification(mesage);

        updateChatList(sender, receiver);
        updateChatList(receiver, sender);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_message,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_change_bg) {
            if (binding.rcvChat.getBackground() == defaultBackground) {
                binding.rcvChat.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_chat1));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("backgroundColor", R.drawable.bg_chat1);
                editor.apply();
            } else {
                binding.rcvChat.setBackground(defaultBackground);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("backgroundColor", R.drawable.bg_chat);
                editor.apply();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateChatList(String userId, String chatUserId) {


        getIdFromEmail(userId, new FragmentMessage.OnIdReceivedListener() {
            @Override
            public void onIdReceived(String id) {
                final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(id)
                        .child(encodeString(chatUserId));

                chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists() ) {
                            chatRef.child("email").setValue(chatUserId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void readMessage(String myId, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();

                if (snapshot.exists()) {
                    Log.d("VoucherActivity", "Vouchers data received from Firebase." + snapshot.toString());
                } else {
                    Log.d("VoucherActivity", "No Voucher data found in Firebase.");
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat != null &&
                            (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
                                    chat.getReceiver().equals(userId) && chat.getSender().equals(myId))) {
                        messageList.add(chat);
                    }
                }

                checkReceiverStatus(userId);
                Log.e("lattet", "onDataChange: " + messageList.toString());
                messageAdapter = new MessageAdapter(MessageActivity.this, messageList);
                binding.rcvChat.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();
                /*binding.rcvChat.smoothScrollToPosition(messageList.size() - 1);*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void checkReceiverStatus(String receiverId) {
        getIdFromEmail(receiverId, new FragmentMessage.OnIdReceivedListener() {
            @Override
            public void onIdReceived(String id) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Account").child(id);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        if (user != null && user.getStatus() != null && user.getStatus().equals("online")) {
                            binding.txtStatus.setText("Đang hoạt động");
                            binding.imgOnline.setVisibility(View.VISIBLE);
                        } else {
                            binding.txtStatus.setText("Hoạt Động Gần Đây");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void status(String status) {
        String userEmail = firebaseUser.getEmail();

        getIdFromEmail(userEmail, new FragmentMessage.OnIdReceivedListener() {
            @Override
            public void onIdReceived(String id) {
                Log.d("TAG", "status: " + id);
                if (id != null) {
                    reference = FirebaseDatabase.getInstance().getReference("Account").child(id);
                    Log.d("TAG", "onIdReceived: " + id);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("status", status);
                    reference.updateChildren(hashMap);
                } else {
                    Log.d("FragmentMessage", "No user found with this email");
                }
            }
        });

    }


    private void sendNotification(String message) {
        Log.e("check37", "sendNotification: " + firebaseUser.getEmail() );
        getIdFromEmail(firebaseUser.getEmail(), new FragmentMessage.OnIdReceivedListener() {
            @Override
            public void onIdReceived(String id) {
                Log.e("check40", "onIdReceived: " + id );
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Account").child(id);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UserModel model = snapshot.getValue(UserModel.class);
                            try {
                                JSONObject jsonObject = new JSONObject();
                                JSONObject notificationObj = new JSONObject();
                                notificationObj.put("title",model.getFullName());
                                notificationObj.put("body",message);
                                JSONObject dataObj = new JSONObject();
                                dataObj.put("email",model.getEmail());

                                jsonObject.put("notification",notificationObj);
                                jsonObject.put("data",dataObj);
                                jsonObject.put("to",userFCM);
                                Log.e("check39", "onDataChange: " + userFCM );
                                callApi(jsonObject);
                            }catch (Exception e){

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Log.e("check44", "callApi Message: " + jsonObject.toString() );
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAA5pa11nw:APA91bH9w1IfcYjdTiuQsj-o3Ttrh689JQxiL0ydOQf6qyEeSxlkbznOz7IYG6yC3rVEo6mCAM7CenfstwWe6nXPsirmoI43hcNVqpcxuNZ5uSWhNHImi0fMI-VXbirIX2GX1zWdzf80")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        status(("online"));
    }

    @Override
    public void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Loại bỏ ValueEventListener khi người dùng rời khỏi Activity
        removeSeenListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Loại bỏ ValueEventListener khi người dùng rời khỏi Activity
        removeSeenListener();
    }

    private void removeSeenListener() {
        // Kiểm tra xem seenListener có tồn tại không trước khi loại bỏ
        if (seenListener != null && reference != null) {
            reference.removeEventListener(seenListener);
        }
    }


}