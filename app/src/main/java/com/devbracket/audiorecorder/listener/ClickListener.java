package com.devbracket.audiorecorder.listener;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import com.devbracket.audiorecorder.DB;
import com.devbracket.audiorecorder.MySharedPreferences;
import com.devbracket.audiorecorder.R;
import com.devbracket.audiorecorder.fragment.Fr_list;
import com.devbracket.audiorecorder.fragment.Fr_record;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.devbracket.audiorecorder.fragment.Fr_list.adb;
import static com.devbracket.audiorecorder.fragment.Fr_record.button;
import static com.devbracket.audiorecorder.fragment.Fr_record.chronometer;
import static com.devbracket.audiorecorder.fragment.Fr_record.fab;
import static com.devbracket.audiorecorder.fragment.Fr_record.imageView;
import static com.devbracket.audiorecorder.fragment.Fr_record.textView;

public class ClickListener implements View.OnClickListener {

    private Context context;
    private FragmentActivity activity;
    private int cn = 1;
    long timeElapsed = 0;
    private boolean isRec = true;
    private boolean pause = true;

    public ClickListener(Context context, FragmentActivity activity) {
        this.activity = activity;
        this.context = context;
    }

    long duration = System.currentTimeMillis();
    MediaRecorder recorder = new MediaRecorder();
    String recname = "record_";
    NotificationManager manager;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_record:
                DB db = new DB(context);
                if (isRec) {
                    notifyRecord();
                    recname += UUID.randomUUID().toString().substring(0, 7);
                    final String mFilePath = context.getExternalFilesDir("").toString() + "/" + recname + ".3gp";
                    final String REC = "Recording";
                    button.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setAnimation(Fr_record.alphaAnimation);
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                        @Override
                        public void onChronometerTick(Chronometer chronometer) {
                            if (cn == 0) {
                                textView.setText(REC);
                            } else if (cn == 1) {
                                textView.setText(REC + ".");
                            } else if (cn == 2) {
                                textView.setText(REC + "..");
                            } else if (cn == 3) {
                                textView.setText(REC + "...");
                                cn = -1;
                            }
                            cn++;
                        }
                    });
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setOutputFile(mFilePath);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    recorder.setAudioChannels(1);
                    if (MySharedPreferences.getPrefHighQuality(context)) {
                        recorder.setAudioSamplingRate(44100);
                        recorder.setAudioEncodingBitRate(192000);
                    }
                    try {
                        recorder.prepare();
                        recorder.start();
                        duration = System.currentTimeMillis();
                    } catch (IOException e) {
                        Log.e("tag", "prepare() failed");
                    }
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_media_stop);
                    fab.setImageBitmap(bitmap);
                    isRec = false;

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recorder.stop();
                            manager.cancel(0);
                            File file = new File(mFilePath);
                            file.delete();
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            button.setVisibility(View.INVISIBLE);
                            chronometer.stop();
                            imageView.setVisibility(View.INVISIBLE);
                            imageView.setAnimation(null);
                            textView.setText("");
                            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_mic_white_36dp);
                            fab.setImageBitmap(bitmap);
                            recname="record_";
                            isRec=true;

                        }
                    });
                } else {
                    manager.cancel(0);
                    db.addRecording(recname, context.getExternalFilesDir("") + "/" + recname + ".3gp", (System.currentTimeMillis() - duration));
                    recname = "record_";
                    if (recorder != null)
                        recorder.stop();
                    Fr_list.list = db.getall();
                    adb.notifyDataSetChanged();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_mic_white_36dp);
                    fab.setImageBitmap(bitmap);
                    button.setVisibility(View.INVISIBLE);
                    chronometer.stop();
                    imageView.setVisibility(View.INVISIBLE);
                    imageView.setAnimation(null);
                    textView.setText("");
                    isRec = true;
                    pause = true;
                }
                break;
        }
    }

    private void notfy() {
        DB db = new DB(context);
        db.addRecording(recname, context.getExternalFilesDir("") + "/" + recname + ".3gp", (System.currentTimeMillis() - duration));
        recname = "record_";
        if (recorder != null)
            recorder.stop();
        Fr_list.list = db.getall();
        adb.notifyDataSetChanged();
        chronometer.setBase(SystemClock.elapsedRealtime());
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_mic_white_36dp);
        fab.setImageBitmap(bitmap);
        button.setVisibility(View.INVISIBLE);
        chronometer.stop();
        imageView.setVisibility(View.INVISIBLE);
        imageView.setAnimation(null);
        textView.setText("");
        isRec = true;
        pause = true;
    }

    private void notifyRecord() {
        duration = System.currentTimeMillis();
        Notification.Builder builder = new Notification.Builder(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("myId");
        }

        builder.setSmallIcon(R.drawable.icon_web)
                .setContentTitle(context.getResources().getString(R.string.app_name)).setContentText("Recording...");

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("myId", "ch", NotificationManager.IMPORTANCE_LOW);
        }

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(channel);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            manager.notify(0, builder.build());
        }
    }
}








