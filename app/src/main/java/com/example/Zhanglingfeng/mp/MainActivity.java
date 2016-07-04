package com.example.hongyu.remote_music_player5;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hongyu.remote_music_player5.VO.MusicInfo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,SensorEventListener {
    /*

    已知Bug：
        1.未联网或者联网失败不会报错，程序会无反应
        2.过一定时间自动播放。－2016.6.11
    展望：
        1.通过输入URL，连接到服务器，获取json，加入到当前歌单
        2.歌单存入sqllite，对歌单进行增删改查（2个表，
            1.（歌曲表）主键，歌曲名，歌曲对应url
            2.（歌单表）主键，歌单名，（外键）歌曲表主键
         ）
        3.服务端友好的，可定制的歌曲列表并转换成json。
        4.扫二维码代替输入URl
        5.分享歌单，分享单首歌曲

     2016.6.10
        加入底部导航栏
        通过json获取底部歌曲名，歌曲url，歌手，专辑，专辑图片
        滑动专辑图片切歌。


     */

    private Button button_playStyle;
    private Button button_updateList;
    private Button button_play;
    private Button button_next;
    private Button button_pre;
    private TextView textView_currentTIme;
    private TextView textView_remainTime;
    private TextView textView_musicName;
    private TextView textView_musicListTitle;
    private SeekBar seekBar;
    private String currentMusicName;
    MediaPlayer mPlayer;
    private SimpleDateFormat sDateFormat = new SimpleDateFormat("mm:ss");
    private double currentProgress = 0;
    private String currentMusic = "";
    private int currentMusicIndex = 0;
    private boolean isIdle = true;
    private boolean isMusicListChange = true;
    SensorManager sensorManager = null;
    Vibrator vibrator = null;
    private int vibratorlevel = 17;
    private boolean isSingleLoop = false;
    private ListView lv;
    private static final ArrayList<String> musicList = new ArrayList<String>();
    private static long lastShakeTime = 0;
    private SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper();
    ArrayList<MusicInfo> musicInfos = new ArrayList<MusicInfo>();
    private int flag_play = 1;
    private int flag_playStyle = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlayer = new MediaPlayer();
        button_playStyle = (Button) findViewById(R.id.button_playStyle);
        button_updateList = (Button) findViewById(R.id.button_updateList);
        button_play = (Button) findViewById(R.id.button_play);
        button_next = (Button) findViewById(R.id.button_next);
        button_pre = (Button) findViewById(R.id.button_pre);
        textView_currentTIme = (TextView) findViewById(R.id.textView_currentTime);
        textView_remainTime = (TextView) findViewById(R.id.textView_remainTime);
        textView_musicName = (TextView) findViewById(R.id.textView_musicName);
        textView_musicListTitle = (TextView) findViewById(R.id.textView_musicListTitle);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(200);
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        button_play.setOnClickListener(this);
        button_next.setOnClickListener(this);
        button_pre.setOnClickListener(this);
        button_playStyle.setOnClickListener(this);
        button_updateList.setOnClickListener(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        lv = (ListView) findViewById(R.id.listView_musicList);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, musicList));
        textView_musicListTitle.setText("Music List\n");
        musicList.clear();
        for (MusicInfo musicInfo0 : musicInfos) {
            musicList.add(musicInfo0.getMusicName());
        }
        vibratorlevel = getIntent().getIntExtra("vibratorlevel",17);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeMusic(position);
                playMusic();
            }
        });
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, musicList));
        isMusicListChange = false;

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public boolean isFastDoubleShake() {
        boolean flag = false;
        long time = System.currentTimeMillis();
        long timeD = time - lastShakeTime;
        if (0 < timeD && timeD < 5000) {
            flag = true;
            Toast.makeText(getApplicationContext(), "摇一摇频率过快!", Toast.LENGTH_SHORT).show();
        } else {
            lastShakeTime = time;
        }
        return flag;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float[] values = event.values;

        if ( sensorType == Sensor.TYPE_ACCELEROMETER)
        {
            if ((Math.abs(values[0]) > vibratorlevel || Math.abs(values[1]) > vibratorlevel || Math.abs(values[2]) > vibratorlevel))
            {
                if (!isFastDoubleShake()){

                    vibrator.vibrate(500);

                    MediaPlayer soundPlayer = MediaPlayer.create(this,R.raw.sound01);

                    for (int j = 0; j < 2; j++) {
                        soundPlayer.start();
                        while (soundPlayer.isPlaying()){
                        }
                    }
                    soundPlayer.stop();
                    soundPlayer.release();
                    randomMusic();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            double msec = seekBar.getProgress() / 200.0 * mPlayer.getDuration();
            mPlayer.seekTo(Integer.parseInt(new java.text.DecimalFormat("0").format(msec)));
            startMusic();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if(!pauseMusic()){
                playMusic();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_play:
                if (flag_play == 1) {
                    flag_play = 2;
                    button_play.setBackgroundResource(R.drawable.play);
                    playMusic();
                    Toast.makeText(getApplicationContext(), "开始播放", Toast.LENGTH_SHORT).show();
                } else if (flag_play == 2) {
                    flag_play = 1;
                    button_play.setBackgroundResource(R.drawable.pause);
                    pauseMusic();
                    Toast.makeText(getApplicationContext(), "暂停播放", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_next:
                nextMusic();
                break;
            case R.id.button_pre:
                preMusic();
                break;
            case R.id.button_playStyle:
                if (flag_playStyle == 1) {
                    flag_playStyle = 2;
                    button_playStyle.setBackgroundResource(R.drawable.order1);
                    Toast.makeText(getApplicationContext(), "顺序播放", Toast.LENGTH_SHORT).show();
                    isSingleLoop = true;
                } else if (flag_playStyle == 2) {
                    flag_playStyle = 3;
                    button_playStyle.setBackgroundResource(R.drawable.random);
                    Toast.makeText(getApplicationContext(), "随机播放", Toast.LENGTH_SHORT).show();
                    isSingleLoop = false;
                } else if (flag_playStyle == 3) {
                    flag_playStyle = 1;
                    button_playStyle.setBackgroundResource(R.drawable.play);
                    Toast.makeText(getApplicationContext(), "单曲循环", Toast.LENGTH_SHORT).show();
                    Collections.shuffle(musicInfos);
                    isMusicListChange = true;
                }
                break;
            case R.id.button_updateList:
//                Intent intent2=new Intent();
//                intent2.setComponent(new ComponentName(this, Main2Activity.class));
//                startActivity(intent2);
                finish();
                break;
        }
        if (isMusicListChange){
            lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, musicList));
            isMusicListChange = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("renew musicDict");
        musicList.clear();
        musicInfos = sharedPreferencesHelper.read(this);
        System.out.println(musicInfos.size());
        for (MusicInfo musicInfo : musicInfos) {
            musicList.add(musicInfo.getMusicName());
        }
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, musicList));
    }

    private void startMusic() {
        mPlayer.start();   //开始播放
        flag_play = 2;
        button_play.setBackgroundResource(R.drawable.play);
        final String duration =  sDateFormat.format(new Date(mPlayer.getDuration() - mPlayer.getCurrentPosition() + 0));
        textView_remainTime.setText(duration);
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {

            };

        };
        new Thread(){
            public void run() {
                try {
                    while (mPlayer.isPlaying()){
                        Thread.sleep(1000);
                        handler.post(new Runnable(){
                            public void run() {
                                String currentPosition =  sDateFormat.format(new Date(mPlayer.getCurrentPosition() + 0));
                                currentProgress = (mPlayer.getCurrentPosition() + 0.0)/(mPlayer.getDuration() + 0.0);
                                String duration =  sDateFormat.format(new Date(mPlayer.getDuration() - mPlayer.getCurrentPosition() + 0));
                                textView_currentTIme.setText(currentPosition);
                                textView_remainTime.setText(duration);
                                seekBar.setProgress(Integer.parseInt(new java.text.DecimalFormat("0").format(currentProgress*200)));
                                if("00:00".equals(textView_remainTime.getText().toString())){
                                    if (isSingleLoop){
                                        playMusic();
                                    } else {
                                        nextMusic();
                                    }

                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void initPlayer() {
        if (isIdle){
            if (!"".equals(currentMusic)){

            } else {
                currentMusic = musicInfos.get(0).getMusicName();
            }
            try {
                String doNotEncode = currentMusic.substring(0,currentMusic.lastIndexOf('/'));
                String needEncode = currentMusic.substring(currentMusic.lastIndexOf('/'));
                System.out.println(doNotEncode +needEncode);
                System.out.println(doNotEncode + Uri.encode(needEncode));
                mPlayer.setDataSource(doNotEncode + Uri.encode(needEncode));


                int i = 0;
                for (MusicInfo musicInfo : musicInfos){
                    if (currentMusic.equals(musicInfo.getMusicPath())) {
                        currentMusicIndex = i;
                        currentMusicName = musicInfo.getMusicName();
                        break;
                    } else  if (i==musicInfos.size()-1){
                        currentMusicIndex = 0;
                        currentMusicName = musicInfo.getMusicName();
                    }
                    i++;
                }
                Toast.makeText(getApplicationContext(), currentMusicName, Toast.LENGTH_SHORT).show();

                textView_musicName.setText(currentMusicName);

            } catch (IOException e) {
                System.err.print("setDataSource Wrong!!!");
                e.printStackTrace();
            }
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mPlayer.prepare();
            } catch (IOException e) {
                System.err.print("prepare Wrong!!!");
                e.printStackTrace();
            }
            isIdle = false;
        }

    }

    private void playMusic() {
        initPlayer();
        startMusic();

    }
    private int getCurrentMusicIndex() {
        int i = 0;
        for (MusicInfo musicInfo : musicInfos){
            if (currentMusic.equals(musicInfo.getMusicPath())) {
                currentMusicIndex = i;
                break;
            } else  if (i==musicInfos.size()-1){
                currentMusicIndex = 0;
            }
            i++;
        }
        return currentMusicIndex;
    }
    private void preMusic() {
        currentMusicIndex = getCurrentMusicIndex();
        if(0 == currentMusicIndex) {
            currentMusicIndex = musicInfos.size()-1;
        } else {
            currentMusicIndex -= 1;
        }
        changeMusic(currentMusicIndex);
        playMusic();
    }

    private void nextMusic() {
        currentMusicIndex = getCurrentMusicIndex();
        if(musicInfos.size()-1 == currentMusicIndex) {
            currentMusicIndex = 0;
        } else {
            currentMusicIndex += 1;
        }
        changeMusic(currentMusicIndex);
        playMusic();
    }

    private void changeMusic(String newMusic) {
        mPlayer.reset();
        isIdle = true;
        currentMusic = newMusic;
    }

    public void changeMusic(int newMusicIndex) {
        if (newMusicIndex < musicInfos.size() && newMusicIndex >= 0) {
            mPlayer.reset();
            isIdle = true;
            currentMusic = musicInfos.get(newMusicIndex).getMusicPath();
        } else {
            System.err.println("changeMusic Wrong!!!");
        }
    }

    private boolean pauseMusic() {
        boolean flag = false;
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            flag_play = 2;
            button_play.setBackgroundResource(R.drawable.play);
            flag = true;
        }
        return flag;
    }

    private int randomMusicWithRandNum() {
        Random rand = new Random();
        currentMusicIndex = getCurrentMusicIndex();
        int randNum = currentMusicIndex;
        while (currentMusicIndex==randNum) {
            randNum = rand.nextInt(musicInfos.size());
        }
        changeMusic(randNum);
        playMusic();
        return randNum;
    }

    private void randomMusic() {
        Random rand = new Random();
        currentMusicIndex = getCurrentMusicIndex();
        int randNum = currentMusicIndex;
        while (currentMusicIndex==randNum) {
            randNum = rand.nextInt(musicInfos.size());
        }
        changeMusic(randNum);
        playMusic();
    }

}