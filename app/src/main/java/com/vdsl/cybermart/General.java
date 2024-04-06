package com.vdsl.cybermart;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class General {


    public static void loadFragment(FragmentManager fragmentManager, Fragment fragment, Bundle bundle) {
        if (bundle != null) fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.frag_container_main, fragment)
                .addToBackStack(null) // Cho phép quay lại fragment trước đó nếu cần
                .commit();
    }

    public static void showSuccessPopup(Context context, String heading, String description, OnDialogButtonClickListener listener) {
        PopupDialog.getInstance(context)
                .setStyle(Styles.SUCCESS)
                .setHeading(heading)
                .setDescription(description)
                .setCancelable(false)
                .showDialog(listener);
    }

    public static void showFailurePopup(Context context, String heading, String description, OnDialogButtonClickListener listener) {
        PopupDialog.getInstance(context)
                .setStyle(Styles.FAILED)
                .setHeading(heading)
                .setDescription(description)
                .setCancelable(false)
                .showDialog(listener);
    }

    public static void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Log.e("check44", "callApi: " + jsonObject.toString() );
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


}
