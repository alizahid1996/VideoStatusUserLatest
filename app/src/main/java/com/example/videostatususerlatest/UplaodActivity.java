package com.example.videostatususerlatest;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.videostatususerlatest.Dashboard.TrimActivity;
import com.example.videostatususerlatest.Models.MediaObject;
import com.example.videostatususerlatest.Models.Member;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UplaodActivity extends AppCompatActivity {
    private static final int PICK_VIDEO = 1;
    VideoView videoView;
    Button button, button2;
    TextView showVideo, chooseVideo, videoName;
    ProgressBar progressBar;
    EditText editText;
    private Uri videoUri;
    MediaController mediaController;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    /*Member member;*/
    MediaObject mediaObject;
    UploadTask uploadTask;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uplaod);

        /*member = new Member();*/
        mediaObject = new MediaObject();
        storageReference = FirebaseStorage.getInstance().getReference("Video");
        databaseReference = FirebaseDatabase.getInstance().getReference("video");
        auth = FirebaseAuth.getInstance();

        videoView = findViewById(R.id.videoview_main);
        button = findViewById(R.id.button_upload_main);
        progressBar = findViewById(R.id.progressBar_main);
        editText = findViewById(R.id.et_video_name);
        showVideo = findViewById(R.id.showVideo);
        chooseVideo = findViewById(R.id.chooseVideo);
        mediaController = new MediaController(UplaodActivity.this);
        videoView.setMediaController(mediaController);
        videoName = findViewById(R.id.et_video_name);
        videoView.start();

        if (editText.equals("")) {
            Toast.makeText(UplaodActivity.this, "Please enter video name", Toast.LENGTH_SHORT).show();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadVideo();
            }
        });

        showVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowVideo(view);
            }
        });

        chooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseVideo(view);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO || resultCode == RESULT_OK ||
                data != null || data.getData() != null) {
            videoUri = data.getData();
            /*Intent i = new Intent(UplaodActivity.this, TrimActivity.class);
            i.putExtra("uri", videoUri.toString());
            startActivity(i);*/

            /*Toast to tell user not upload video < 15 sec*/
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(UplaodActivity.this, videoUri);

            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMilliSec = Long.parseLong(time);
            long duration = timeInMilliSec/1000;
            long hours = duration / 3600;
            long minutes = (duration - hours * 3600) / 60;
            long seconds = duration - (hours * 3600 + minutes * 60);

            if (seconds >= 30)
            {
                Toast.makeText(UplaodActivity.this, "You cannot upload a video more than 30 seconds", Toast.LENGTH_SHORT).show();
                button.setClickable(false);
                button.setText("Can't Upload");
            }
            else
            {
                videoView.setVideoURI(videoUri);
                button.setClickable(true);
                button.setText("Upload");
            }
        }

    }

    public void ChooseVideo(View view) {

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO);

    }

    private String getExt(Uri uri) {
        ContentResolver contentResolver = UplaodActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    public void ShowVideo(View view) {

        Intent intent = new Intent(UplaodActivity.this, MainActivity.class);
        startActivity(intent);

    }

    private void UploadVideo() {
        String videoName = editText.getText().toString();
        String search = editText.getText().toString().toLowerCase();
        if (TextUtils.isEmpty(videoName)) {
            Toast.makeText(UplaodActivity.this, "Please enter video name", Toast.LENGTH_SHORT).show();
        } else if (videoUri == null) {
            Toast.makeText(UplaodActivity.this, "Please choose a video", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getExt(videoUri));
            uploadTask = reference.putFile(videoUri);

            Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(UplaodActivity.this, "Data saved", Toast.LENGTH_SHORT).show();

                                mediaObject.setTitle(videoName);
                                mediaObject.setMedia_url(downloadUrl.toString());
                                //mediaObject.setDescription(downloadUrl.toString());
                                mediaObject.setThumbnail(downloadUrl.toString());
                                /*member.setSearch(search);*/
                                /*mediaObject.setUserId(auth.getCurrentUser().getUid());*/
                                /*member.setStatus("pending");*/
                                String i = databaseReference.push().getKey();
                                databaseReference.child(i).setValue(mediaObject);
                            } else {
                                Toast.makeText(UplaodActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}