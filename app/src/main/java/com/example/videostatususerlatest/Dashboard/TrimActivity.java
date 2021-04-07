package com.example.videostatususerlatest.Dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.LogCallback;
import com.arthenica.mobileffmpeg.LogMessage;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.example.videostatususerlatest.Dashboard.ui.UploadFragment;
import com.example.videostatususerlatest.ProgressBarActivity;
import com.example.videostatususerlatest.R;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;
import java.util.Arrays;

public class TrimActivity extends AppCompatActivity {

    Uri uri;
    ImageView imageView;
    VideoView videoView;
    TextView tvleft, tvRight;
    RangeSeekBar rangeSeekbar;
    int duration;
    String filePrefix;
    String[] command;
    File dest;
    public static String originalPath;
    boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);

        imageView = findViewById(R.id.pause);
        videoView = findViewById(R.id.trimVideView);
        tvleft = findViewById(R.id.tvLeft);
        tvRight = findViewById(R.id.tvRight);
        rangeSeekbar = findViewById(R.id.seekbar);

        Intent i = getIntent();

        if (i != null) {
            String imgPath = i.getStringExtra("uri");
            uri = Uri.parse(imgPath);
            isPlaying = true;
            videoView.setVideoURI(uri);
            videoView.start();
        }

        setListeners();

    }

    private void setListeners() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    imageView.setImageResource(R.drawable.ic_play_icon);
                    videoView.pause();
                    isPlaying = false;
                } else {

                    videoView.start();
                    imageView.setImageResource(R.drawable.ic_pause_icon);
                    isPlaying = true;
                }

            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                duration = mp.getDuration() / 1000;
                tvleft.setText("00:00:00");
                tvRight.setText(getTime(mp.getDuration() / 1000));
                mp.setLooping(true);
                rangeSeekbar.setRangeValues(0, duration);
                rangeSeekbar.setSelectedMaxValue(duration);
                rangeSeekbar.setSelectedMinValue(0);
                rangeSeekbar.setEnabled(true);

                rangeSeekbar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
                    @Override
                    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                        videoView.seekTo((int) minValue * 1000);
                        tvleft.setText(getTime((int) bar.getSelectedMinValue()));
                        tvRight.setText(getTime((int) bar.getSelectedMaxValue()));

                    }
                });

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (videoView.getCurrentPosition() >= rangeSeekbar.getSelectedMaxValue()
                                .intValue() * 1000) {
                            videoView.seekTo(rangeSeekbar.getSelectedMinValue().intValue() * 1000);
                        }


                    }
                }, 1000);
            }
        });
    }

    private String getTime(int seconds) {

        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int min = rem / 60;
        int sec = rem % 60;

        return String.format("%02d", hr) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.trim) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(TrimActivity.this);
            LinearLayout linearLayout = new LinearLayout(TrimActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(50, 0, 50, 100);
            final EditText input = new EditText(TrimActivity.this);
            input.setLayoutParams(lp);
            input.setGravity(Gravity.TOP | Gravity.START);
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            linearLayout.addView(input, lp);

            alert.setMessage("Set Video Name");
            alert.setTitle("Change Video Name");
            alert.setView(linearLayout);
            alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filePrefix = input.getText().toString();
                    trimVideo(rangeSeekbar.getSelectedMinValue().intValue() * 1000,
                            rangeSeekbar.getSelectedMaxValue().intValue() * 1000, filePrefix);
                    Intent i = new Intent(TrimActivity.this, ProgressBarActivity.class);
                    i.putExtra("duration", duration);
                    i.putExtra("command", command);
                    i.putExtra("destination", dest.getAbsolutePath());
                    startActivity(i);
                    finish();
                    dialog.dismiss();


                    /*Bundle bundle = new Bundle();
                    bundle.putInt("duration", 0);
                    bundle.putStringArray("destination", new String[]{dest.toString()});
                    UploadFragment uploadFragment = new UploadFragment();
                    uploadFragment.setArguments(bundle);
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.add(R.id.fragmentContainer, new UploadFragment(), "");
                    transaction.addToBackStack(null);
                    transaction.commit();
                    dialog.dismiss();*/

                   /* Intent i = new Intent(TrimActivity.this, UploadFragment.class);
                    i.putExtra("duration", duration);
                    i.putExtra("command", command);
                    i.putExtra("destination", dest);
                    startActivity(i);*/
                }
            });

            alert.show();


        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void trimVideo(int startMs, int endMs, String fileName) {
        File folder = new File(getExternalFilesDir(fileName) + "/TrimVideo");
        if (!folder.exists()) {
            folder.mkdir();
        }
        filePrefix = fileName;

        String fileExt = ".mp4";
        dest = new File(folder, filePrefix + fileExt);
        originalPath = getRealPathFromUri(getApplicationContext(), uri);

        duration = (endMs - startMs) / 1000;
        command = new String[]{"-ss", "" + startMs / 1000, "-y", "-i", originalPath, "-t", ""
                + (endMs - startMs) / 1000, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "-48000",
                "-ac", "2", "-ar", "22050", dest.getAbsolutePath()};

       // execFFMpegBinary(command);

        Toast.makeText(this, "Video Trimmed Successfully", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getRealPathFromUri(Context ctx, Uri contentUri) {
        Cursor cursor = null;

        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = ctx.getContentResolver().query(uri, projection, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (cursor != null
            ) {

                cursor.close();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trim, menu);
        return true;
    }

   /* private void execFFMpegBinary(String[] command) {
        Config.enableLogCallback(new LogCallback() {
            @Override
            public void apply(LogMessage message) {
                Log.e(Config.TAG, message.getText());

            }
        });

        Config.enableStatisticsCallback(new StatisticsCallback() {
            @Override
            public void apply(Statistics statistics) {
                Log.e(Config.TAG, String.format("frame %d, time: %d" , statistics.getVideoFrameNumber(), statistics.getTime()));


            }
        });

        long executionId = FFmpeg.executeAsync(command, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == Config.RETURN_CODE_SUCCESS)
                {
                    Log.d(Config.TAG, "finished command: ffmpeg" + Arrays.toString(command));

                }
                else if (returnCode == RESULT_CANCELED)
                {
                    Log.d(Config.TAG, "Async commadn execution canceled by user" + Arrays.toString(command));
                }
                else {
                    Log.e(Config.TAG, String.format("Aysnc command execution failed with returned code = %d", returnCode));

                }

            }
        });
    }*/


}