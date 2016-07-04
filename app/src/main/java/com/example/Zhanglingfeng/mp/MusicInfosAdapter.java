package com.example.hongyu.remote_music_player5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hongyu.remote_music_player5.VO.MusicInfo;

import java.util.ArrayList;

/**
 * Created by hongyu on 16/6/11.
 */
public class MusicInfosAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<MusicInfo> musicInfos;
    private ArrayList<MusicInfo> selectedMusicInfos = new ArrayList<>();
    private SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper();
    private int vibratorlevel = 15;

    public int getVibratorlevel() {
        return vibratorlevel;
    }

    public void setVibratorlevel(int vibratorlevel) {
        this.vibratorlevel = vibratorlevel;
    }

    public MusicInfosAdapter(final Context context, ArrayList<MusicInfo> musicInfos, Button button, final Activity activity)
    {
        this.musicInfos = musicInfos;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMusicInfos.size()!=0) {
                    sharedPreferencesHelper.clear(context);
                    sharedPreferencesHelper.save(context, selectedMusicInfos);
                    Intent intent = new Intent();
                    intent.setClass(context,MainActivity.class);
                    intent.putExtra("vibratorlevel",vibratorlevel);
                    Log.d("1111",Integer.toString(vibratorlevel));
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(context.getApplicationContext(), "至少要选一首歌曲", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    @Override
    public int getCount() {
        return musicInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return musicInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            viewHolder.checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
            viewHolder.tv = (TextView)convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv.setText(musicInfos.get(position).getMusicName());
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedMusicInfos.add(musicInfos.get(position));
                    Log.d("checkbox","add " +  musicInfos.get(position).getMusicName());
                } else {
                    selectedMusicInfos.remove(musicInfos.get(position));
                    Log.d("checkbox","remove " +  musicInfos.get(position).getMusicName());
                }
            }
        });

        return convertView;
    }
    class ViewHolder{
        CheckBox checkBox;
        TextView tv;
    }
}
