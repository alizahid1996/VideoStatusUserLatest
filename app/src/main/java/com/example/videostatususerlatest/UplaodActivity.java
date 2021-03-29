package com.example.videostatususerlatest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
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
    Button button;
    TextView showVideo, chooseVideo, videoName;
    ProgressBar progressBar;
    EditText editText;
    private Uri videoUri;
    MediaController mediaController;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Member member;
    UploadTask uploadTask;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uplaod);

        member = new Member();
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
            videoView.setVideoURI(videoUri);
            Intent i = new Intent(UplaodActivity.this, TrimActivity.class);
            i.putExtra("uri", videoUri.toString());
            startActivity(i);

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

                                member.setName(videoName);
                                member.setVideourl(downloadUrl.toString());
                                member.setSearch(search);
                                member.setUserId(auth.getCurrentUser().getUid());
                                member.setStatus("pending");
                                String i = databaseReference.push().getKey();
                                databaseReference.child(i).setValue(member);
                            } else {
                                Toast.makeText(UplaodActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}