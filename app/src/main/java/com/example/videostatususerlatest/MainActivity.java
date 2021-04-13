package com.example.videostatususerlatest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.videostatususerlatest.Adapters.VideoPlayerRecyclerAdapter;
import com.example.videostatususerlatest.Adapters.VideoPlayerRecyclerView;
import com.example.videostatususerlatest.Models.MediaObject;
import com.example.videostatususerlatest.Models.Member;
import com.example.videostatususerlatest.utils.VerticalSpacingItemDecorator;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ArrayList<MediaObject> mediaObjects;
    VideoPlayerRecyclerAdapter adapter;
    private VideoPlayerRecyclerView mRecyclerView;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        mRecyclerView = findViewById(R.id.recycler_view);
        mediaObjects = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        mRecyclerView.addItemDecoration(itemDecorator);
        fetchData();


        //initRecyclerView();

    }


   /* private void initRecyclerView(){



    }*/

    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }


    @Override
    protected void onDestroy() {
        if(mRecyclerView!=null)
            mRecyclerView.releasePlayer();
        super.onDestroy();
    }

    private void fetchData()
    {
        database.getReference().child("Video").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mediaObjects.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    MediaObject mediaObject = dataSnapshot.child("1").getValue(MediaObject.class);
//                    url = mediaObject.getMedia_url();
                    mediaObjects.add(mediaObject);
                }
  //              Toast.makeText(MainActivity.this, ""+url, Toast.LENGTH_SHORT).show();
                adapter = new VideoPlayerRecyclerAdapter(mediaObjects, initGlide());
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setMediaObjects(mediaObjects);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}













