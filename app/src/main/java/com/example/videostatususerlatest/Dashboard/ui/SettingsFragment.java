package com.example.videostatususerlatest.Dashboard.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.videostatususerlatest.AboutActivity;
import com.example.videostatususerlatest.Dashboard.UserProfile;
import com.example.videostatususerlatest.FeedbackActivity;
import com.example.videostatususerlatest.LoginActivity;
import com.example.videostatususerlatest.R;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


public class SettingsFragment extends Fragment {

    LinearLayout logout, myProfile;
    FirebaseAuth auth;
    TextView gotoFeedback, gotoAboutActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        logout = view.findViewById(R.id.logout);
        myProfile  = view.findViewById(R.id.myProfile);
        auth = FirebaseAuth.getInstance();
        gotoFeedback = view.findViewById(R.id.feedbackForm);
        gotoAboutActivity = view.findViewById(R.id.aboutActivity);

        logout.setOnClickListener(view1 -> {
            showLogoutPopup();
        });

        gotoAboutActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AboutActivity.class);
                startActivity(i);

            }
        });

        myProfile.setOnClickListener(view1 -> {
            Intent i = new Intent(getActivity(), UserProfile.class);
            startActivity(i);
        });

        gotoFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(i);

            }
        });

        return view;

    }

    private void showLogoutPopup() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.back_pressed_dialog, null);

        Button yes = dialogView.findViewById(R.id.btnYes);
        Button no = dialogView.findViewById(R.id.btnNo);

        yes.setOnClickListener(v -> {
            dialogBuilder.dismiss();
            auth.signOut();
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
            getActivity().finishAffinity();
        });
        no.setOnClickListener(v -> {
            dialogBuilder.dismiss();
        });

        dialogBuilder.setView(dialogView);

        dialogBuilder.show();
    }


}