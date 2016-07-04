package com.example.hongyu.remote_music_player5;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.hongyu.remote_music_player5.VO.MusicInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hongyu on 16/6/23.
 */
public class SharedPreferencesHelper {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor ;
    private final String FILE_NAME = "stableMusicList";
    public SharedPreferencesHelper() {

    }

    public void save(Context context, String key, String value){
        sharedPreferences = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void save(Context context, ArrayList<MusicInfo> musicInfos){
        sharedPreferences = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        for (MusicInfo m : musicInfos) {
            editor.putString(m.getMusicName(), m.getMusicPath());
            editor.commit();
        }

    }

    public ArrayList<MusicInfo> read(Context context){
        sharedPreferences = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        Map<String, ?> map = sharedPreferences.getAll();
        ArrayList<MusicInfo>  musicInfos = new ArrayList<>();
        for (String key : map.keySet()) {
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.setMusicName(key);
            musicInfo.setMusicPath(map.get(key).toString());
            musicInfos.add(musicInfo);
        }
        return musicInfos;
    }

    public void remove(Context context, String key){
        sharedPreferences = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public void clear(Context context){
        sharedPreferences = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
