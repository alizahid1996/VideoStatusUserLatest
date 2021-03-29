package com.example.videostatususerlatest.Dashboard.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.example.videostatususerlatest.FFMpegService;
import com.example.videostatususerlatest.MainActivity;
import com.example.videostatususerlatest.Models.Member;
import com.example.videostatususerlatest.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static com.example.videostatususerlatest.Dashboard.TrimActivity.originalPath;


public class UploadFragment extends Fragment {
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

    int duration;
    String[] command;
    String path;

    ServiceConnection connection;
    FFMpegService ffMpegService;
    Integer res;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        member = new Member();
        storageReference = FirebaseStorage.getInstance().getReference("Video");
        databaseReference = FirebaseDatabase.getInstance().getReference("video");
        auth = FirebaseAuth.getInstance();

        videoView = view.findViewById(R.id.videoview_main);
        button = view.findViewById(R.id.button_upload_main);
        progressBar = view.findViewById(R.id.progressBar_main);
        editText = view.findViewById(R.id.et_video_name);
        showVideo = view.findViewById(R.id.showVideo);
        chooseVideo = view.findViewById(R.id.chooseVideo);
        mediaController = new MediaController(getActivity());
        videoView.setMediaController(mediaController);
        videoName = view.findViewById(R.id.et_video_name);



        Bundle bundle = getArguments();
        if (bundle != null)
        {
            duration = bundle.getInt("duration");
            command = bundle.getStringArray("command");
            path = bundle.getString("destination");

            Intent myIntent = new Intent(getActivity(), FFMpegService.class);
            myIntent.putExtra("duration", String.valueOf(duration));
            myIntent.putExtra("command", command);
            myIntent.putExtra("destination", path);
            getActivity().startService(myIntent);
        }
        videoView.start();

        Log.d("duration", String.valueOf(duration));
        Log.d("command", String.valueOf(command));
        Log.d("path", String.valueOf(path));

        /*Intent i = getActivity().getIntent();
        if (i != null)
        {
            duration = i.getIntExtra("duration", 0);
            command = i.getStringArrayExtra("command");
            path = i.getStringExtra("destination");


        }*/

        if (editText.equals("")) {
            Toast.makeText(getActivity(), "Please enter video name", Toast.LENGTH_SHORT).show();
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

        Toast.makeText(ffMpegService, ""+originalPath, Toast.LENGTH_SHORT).show();

        return view;



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO || resultCode == RESULT_OK ||
                data != null || data.getData() != null) {
            videoUri = data.getData();

            videoView.setVideoURI(videoUri);
        }

    }

    public void ChooseVideo(View view) {

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO);

    }

    private String getExt(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    public void ShowVideo(View view) {

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);

    }

    private void UploadVideo() {

        String videoName = editText.getText().toString();
        String search = editText.getText().toString().toLowerCase();
        if (TextUtils.isEmpty(videoName)) {
            Toast.makeText(getActivity(), "Please enter video name", Toast.LENGTH_SHORT).show();
        } else if (videoUri == null) {
            Toast.makeText(getActivity(), "Please choose a video", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getActivity(), "Data saved", Toast.LENGTH_SHORT).show();

                                member.setName(videoName);
                                member.setVideourl(downloadUrl.toString());
                                member.setSearch(search);
                                member.setUserId(auth.getCurrentUser().getUid());
                                member.setStatus("pending");
                                String i = databaseReference.push().getKey();
                                databaseReference.child(i).setValue(member);
                            } else {
                                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


}