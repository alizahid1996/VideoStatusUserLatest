package com.example.videostatususerlatest;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.videostatususerlatest.Models.Member;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.EventLogger;

import java.util.EventListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder{

    SimpleExoPlayer exoPlayer;
    PlayerView playerView;
    private Context context;
    EventListener listener;
    AdaptiveTrackSelection.Factory factory;
    DataSpec dataSpec;
    EventLogger eventLogger;
    PlayerControlView player;
    TrackSelector trackSelector;
    // FloatingActionButton button;
    Member member;

    public ViewHolder(@NonNull View itemView) {

        super(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mClickListener.onItemClick(view,getAdapterPosition());

            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                mClickListener.onItemLongClick(view,getAdapterPosition());
                return false;
            }
        });

        //button = itemView.findViewById(R.id.downloadBtn);


    }


    public void setExoplayer(Application application , String name, String Videourl){

        TextView textView = itemView.findViewById(R.id.tv_item_name);
        playerView = itemView.findViewById(R.id.exoplayer_item);

        textView.setText(name);



        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(application).build();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(application);
            Uri video = Uri.parse(Videourl);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(video,dataSourceFactory,extractorsFactory,null,null);
            playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
           //exoPlayer.getPlayWhenReady();
           exoPlayer.setPlayWhenReady(false);
           //exoPlayer.getPlaybackState();
            //exoPlayer.removeListener((Player.EventListener) listener);
            //exoPlayer.release();

/*
            if (playerView!=null ){
                exoPlayer.stop();

                factory = null;
                dataSpec = null;
                eventLogger = null;
                exoPlayer=null;
                trackSelector = null;

            }*/


        }catch (Exception e){
            Log.e("ViewHolder","exoplayer error"+e.toString());
        }


    }




    private ViewHolder.Clicklistener mClickListener;
    public interface Clicklistener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }

    public void setOnClicklistener(ViewHolder.Clicklistener clicklistener){
        mClickListener = clicklistener;
    }




}
