package com.example.hongyu.remote_music_player5;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hongyu.remote_music_player5.VO.MusicInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener{

    private ListView listView;
    ArrayList<String> musicNames = new ArrayList<String>();
    private MusicInfosAdapter musicInfosAdapter;
    private Button button_confirm;
    private Button button_shakeControl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        listView = (ListView)findViewById(R.id.listView_musicList);

        ArrayList<MusicInfo> musicInfos = JsonObject2MusicInfo(getJson("http://10.42.0.1/pi-dev-php/index1.php"));
        for (MusicInfo musicInfo : musicInfos) {
            musicNames.add(musicInfo.getMusicName());
        }
        //musicInfosAdapter = new MusicInfosAdapter(this, musicNames);

        button_confirm = (Button) findViewById(R.id.button_confirm);
        button_shakeControl = (Button) findViewById(R.id.button_shakeControl);
        button_shakeControl.setOnClickListener(this);

        musicInfosAdapter = new MusicInfosAdapter(this, musicInfos,button_confirm,this);
        listView.setAdapter(musicInfosAdapter);
    }


    private String getJson(String stringUrl) {

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e1) {
            Toast.makeText(getApplicationContext(), "无法创建URL", Toast.LENGTH_SHORT).show();
        }
        String resultData = "";
        if (url != null) {
            HttpURLConnection conn;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);        //设置连接超时时间
                conn.setRequestMethod("POST");     //Post方式
                conn.setDoOutput(true);  //设置能输出
                conn.setDoInput(true); //设置能输入
                conn.setUseCaches(false);  //不使用缓存
                conn.setRequestProperty("Charset", "UTF-8");   //设置uft-8字符集
                //接收服务器返回的信息
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String line = null;
//                Toast.makeText(getApplicationContext(), "Status codes: "+Integer.toString(conn.getResponseCode()), Toast.LENGTH_SHORT).show();
                //使用循环体来获得数据
                while ((line = reader.readLine()) != null) {
                    resultData += line + "\n";
                }
                reader.close();  //关闭
                if (!resultData.equals("")) {
                    ArrayList<MusicInfo> musicInfos = JsonObject2MusicInfo(resultData);
                    String size = Integer.toString(musicInfos.size());
                    System.out.println(musicInfos.size());
                    Toast.makeText(getApplicationContext(), size, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "读取的内容为null", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "出错了!", Toast.LENGTH_SHORT).show();
            }
        }
        return resultData;
    }
    private ArrayList<MusicInfo> JsonObject2MusicInfo(String json){
        String musicName;
        String artist;
        String albums;
        String thumbnail;
        String musicPath;
        ArrayList<MusicInfo> musicInfos = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = (JSONObject)jsonArray.opt(i);
                musicName = jsonObject.getString("musicName");
                artist = jsonObject.getString("artist");
                albums = jsonObject.getString("albums");
                thumbnail = jsonObject.getString("thumbnail");
                musicPath = jsonObject.getString("musicPath");
                MusicInfo musicInfo = new MusicInfo(musicName,artist,albums,thumbnail,musicPath);
                musicInfos.add(musicInfo);

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return musicInfos;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_shakeControl:
                System.out.println("aaaaaaaaaaaaa");
                int vibratorlevel = 150;
                if ("开启摇一摇".equals(button_shakeControl.getText())) {
                    button_shakeControl.setText("关闭摇一摇");
                    button_shakeControl.setBackgroundColor(Color.parseColor("#99cc00"));
                    Toast.makeText(getApplicationContext(), "开启摇一摇", Toast.LENGTH_SHORT).show();

                    vibratorlevel = 15;
                } else if ("关闭摇一摇".equals(button_shakeControl.getText())) {
                    button_shakeControl.setText("开启摇一摇");
                    button_shakeControl.setBackgroundColor(Color.parseColor("#33b5e5"));
                    Toast.makeText(getApplicationContext(), "关闭摇一摇", Toast.LENGTH_SHORT).show();
                }
               musicInfosAdapter.setVibratorlevel(vibratorlevel);
        }


    }
}
