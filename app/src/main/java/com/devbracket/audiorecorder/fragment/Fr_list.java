package com.devbracket.audiorecorder.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devbracket.audiorecorder.DB;
import com.devbracket.audiorecorder.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Fr_list extends Fragment {

    public static ListView ls;
    public static BaseAdapter adb;
    public static List<DB.RecordItem> list;
    MediaPlayer player;
    AudioManager manager;

    public static Fr_list newInstance() {
        Bundle args = new Bundle();
        Fr_list fragment = new Fr_list();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, null);
        manager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        init(view);
        return view;
    }

    private void init(View view) {
        final DB db = new DB(getContext());
        list = new ArrayList<>(db.getall());
        ls = view.findViewById(R.id.lstv);
        adb = new BaseAdapter() {

            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object getItem(int position) {
                return list.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                DB.RecordItem item = list.get(position);
                TextView name, length, date;
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.recycle_item, null);
                name = convertView.findViewById(R.id.rec_name);
                length = convertView.findViewById(R.id.rec_length);
                date = convertView.findViewById(R.id.rec_date);

                long time = Long.valueOf(item.length);
                long min = TimeUnit.MILLISECONDS.toMinutes(time);
                long sec = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(min);
                name.setText(item.name+".3gp");
                date.setText(
                        DateUtils.formatDateTime(
                                getContext(),
                                Long.valueOf(item.date),
                                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                        ));
                length.setText(String.format("%02d:%02d", min, sec));
                return convertView;
            }
        };
        ls.setAdapter(adb);
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<DB.RecordItem> item = db.getall();
                if (player!=null)player.release();
                player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    player.setDataSource(getContext().getExternalFilesDir("")+"/"+ item.get(position).name+".3gp");
                } catch (IOException e) {
                    Toast.makeText(getContext(), "This Record may be deleted or renamed!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                try {
                    player.prepare();
                } catch (IOException e) {
                    Toast.makeText(getContext(), "io !!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        player.start();
                    }
                });
            }
        });


        ls.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final List<DB.RecordItem> item = db.getall();
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.d_action, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(view1);
                builder.setTitle(item.get(position).name+".3gp");
                final AlertDialog alertDialog =builder.create();
                alertDialog.show();
                view1.findViewById(R.id.ren).setEnabled(false);
                view1.findViewById(R.id.del).setEnabled(false);
                view1.findViewById(R.id.delall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DB dbd = new DB(getContext());
                        dbd.delete();
                        list =db.getall();
                        int i = 0;
                        adb.notifyDataSetChanged();
                        alertDialog.dismiss();
                        while (i<item.size() && item.get(i)!=null){
                            File file = new File(getContext().getExternalFilesDir("")+"/"+ item.get(i).name+".3gp");
                            if (file.exists())
                                file.delete();
                            i++;
                        }
                    }
                });

                view1.findViewById(R.id.pro).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        final List<DB.RecordItem> item = db.getall();
                        View view1 = LayoutInflater.from(getContext()).inflate(R.layout.d_pro, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(view1);
                        builder.setTitle(item.get(position).name+".3gp");
                        final AlertDialog alertDialog =builder.create();
                        alertDialog.show();
                        ((TextView)view1.findViewById(R.id.pro_name)).setText(item.get(position).name+".3gp");
                        ((TextView)view1.findViewById(R.id.pro_dir)).setText(item.get(position).file);
                        ((TextView)view1.findViewById(R.id.pro_length)).setText(((Long.valueOf(item.get(position).length))/1000)+" Sec");
                        ((TextView)view1.findViewById(R.id.pro_type)).setText("3gp encoded");
                    }
                });

                view1.findViewById(R.id.del).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DB dbd = new DB(getContext());
                        dbd.deleteAt(item.get(position).name);
                        list =db.getall();
                        adb.notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                });

                return false;
            }
        });
    }

}








