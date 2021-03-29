package com.example.videostatususerlatest;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class CategoriesFragment extends Fragment {

    ImageView loveRomantic, party, sad, dance, comedy, korean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        loveRomantic = view.findViewById(R.id.imageloveRomantic);
        party = view.findViewById(R.id.ic_party);
        sad = view.findViewById(R.id.imagesadStatus);
        dance = view.findViewById(R.id.ic_dance);
        comedy = view.findViewById(R.id.imagecomedy);
        korean = view.findViewById(R.id.ic_korean);

        loveRomantic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UplaodActivity.class);
                startActivity(i);
            }
        });

        party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UplaodActivity.class);
                startActivity(i);
            }
        });

        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UplaodActivity.class);
                startActivity(i);
            }
        });

        dance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UplaodActivity.class);
                startActivity(i);
            }
        });

        comedy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UplaodActivity.class);
                startActivity(i);
            }
        });

        korean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UplaodActivity.class);
                startActivity(i);
            }
        });



        return view;
    }
}