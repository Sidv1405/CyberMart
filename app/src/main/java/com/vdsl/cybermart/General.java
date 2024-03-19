package com.vdsl.cybermart;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

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
}
