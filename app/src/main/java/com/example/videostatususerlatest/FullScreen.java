package com.example.videostatususerlatest;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.videostatususerlatest.Models.Member;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class FullScreen extends AppCompatActivity {

    private SimpleExoPlayer player;
    private PlayerView playerView;
    TextView textView;
    ImageView backButton;
    boolean fullscreen = false;
    ImageView fullscreenButton;
    Member member;
    private String url;
    private boolean playwhenready = false;
    private  int currentWindow = 0;
    private  long playbackposition = 0;
    FloatingActionButton buttonDownload;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        playerView = findViewById(R.id.exoplayer_fullscreen);
        textView = findViewById(R.id.tv_fullscreen);
        backButton = findViewById(R.id.ivBackButton);
        buttonDownload =findViewById(R.id.downloadBtn);


        fullscreenButton = playerView.findViewById(R.id.exoplayer_fullscreen_icon);

        backButton.setOnClickListener(view -> {
            finish();
        });

        buttonDownload.setOnClickListener(view -> {
            Toast.makeText(FullScreen.this, "Starting Download......", Toast.LENGTH_SHORT).show();
            download(member);
        });

        Intent intent = getIntent();
        url = intent.getExtras().getString("ur");
        title = intent.getExtras().getString("nam");

        textView.setText(title);

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fullscreen){
                    fullscreenButton.setImageDrawable(ContextCompat.getDrawable(FullScreen.this,R.drawable.ic_fullscreen_expand));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if (getSupportActionBar() != null){
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);
                    fullscreen = false;
                }else {
                    fullscreenButton.setImageDrawable(ContextCompat.getDrawable(FullScreen.this,R.drawable.ic_fullscreen_skrink));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if (getSupportActionBar() != null){
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView.setLayoutParams(params);
                    fullscreen = true;


                }
            }
        });
    }

    private void download(Member member) {

        String videoUrl = url;
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(videoUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        String fileName = title;
                        String fileType = storageMetadata.getContentType();
                        String fileDirectory = Environment.DIRECTORY_DOWNLOADS;

                        //init download manager
                        DownloadManager manager = (DownloadManager) FullScreen.this.getSystemService(Context.DOWNLOAD_SERVICE);

                        //get uri of file to be downloaded
                        Uri uri = Uri.parse(videoUrl);

                        //create download request, we can download multiple files at once
                        DownloadManager.Request request = new DownloadManager.Request(uri);

                        //notification visibility
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                        //set destination path
                        request.setDestinationInExternalPublicDir(""+fileDirectory, "" + fileName + ".mp4");

                        //add request to queue
                        manager.enqueue(request);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FullScreen.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private MediaSource buildMediaSource(Uri uri){
        DataSource.Factory datasourcefactory =
                new DefaultHttpDataSourceFactory("video");
        return  new ProgressiveMediaSource.Factory(datasourcefactory)
                .createMediaSource(uri);
    }

    private void initializeplayer(){
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        Uri uri = Uri.parse(url);
        MediaSource mediaSource = buildMediaSource(uri);
        player.setPlayWhenReady(playwhenready);
        player.seekTo(currentWindow,playbackposition);
        player.prepare(mediaSource,false,false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Util.SDK_INT >= 26 ){
            initializeplayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Util.SDK_INT >= 26 || player == null ){
            //  initializeplayer();
             //startPlayer();
        }
    }

    /*private void pausePlayer(){
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }*/
    private void startPlayer(){
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Util.SDK_INT > 26 ){
            releasePlayer();

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (Util.SDK_INT >= 26 ){
            releasePlayer();

        }
    }

    private void releasePlayer(){
        if (player != null){
            playwhenready = player.getPlayWhenReady();
            playbackposition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.setPlayWhenReady(false);
            player = null;
        }
    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();

        player.stop();
        releasePlayer();

        final Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }






}