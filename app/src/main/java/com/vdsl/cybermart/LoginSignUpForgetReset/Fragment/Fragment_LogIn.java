package com.vdsl.cybermart.LoginSignUpForgetReset.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.vdsl.cybermart.R;

public class Fragment_LogIn extends Fragment {

    TextView txt_SignUp,txt_forgotPass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_in,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txt_SignUp = view.findViewById(R.id.txt_signUp);
        txt_forgotPass = view.findViewById(R.id.txt_forgotPass);

        txt_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_SignUp fragmentSignUp = new Fragment_SignUp();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fr_framemain, fragmentSignUp);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        txt_forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_ForgotPassword fragmentForgotPassword = new Fragment_ForgotPassword();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fr_framemain, fragmentForgotPassword);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


    }
}
