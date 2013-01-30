package ru.disc.sounds3d;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int currentPosition = -1;
    private boolean isPaused = false;
    private boolean isLoopingPlaylist = false;

    private float leftVol = 0f;
    private float rightVol = 0f;
    private Runnable increaseVolume, decreaseVolume;
    MyArrayAdapter adapter;

    /**
     * Список элементов вида ключ-значение, где ключ - название трека, значение - путь к файлу
     */
    final public Map<Integer,Integer> elements = new HashMap<Integer,Integer>() {{
        put(R.string.snd_rain_in_tropics, R.raw.rain_in_tropics);
        put(R.string.snd_nature, R.raw.nature);
        put(R.string.snd_cat_purring, R.raw.cat_purring);
        put(R.string.snd_wind, R.raw.wind);
        put(R.string.snd_cuckoo, R.raw.cuckoo);
        put(R.string.snd_storm2, R.raw.storm2);
        put(R.string.snd_nightingale, R.raw.nightingale);
        put(R.string.snd_forest, R.raw.forest);
        put(R.string.snd_morning_in_the_forest, R.raw.morning_in_the_forest);
    }};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // работа со списком
        final ListView listView = (ListView)findViewById(R.id.listView);

        // заполняю список элементами из массива
        adapter = new MyArrayAdapter(getApplicationContext(), elements.keySet().toArray());
        listView.setAdapter(adapter);

          // обработчик нажатия по элементу списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // если уже играется трек или он стоит на паузе, ставлю его на паузу
                if ((mediaPlayer.isPlaying() || isPaused) && position == currentPosition) {
                    playButtonClick(findViewById(R.id.mainView));
                } else {
                    currentPosition = position;
                    adapter.setSelectedPosition(position);
                    playSong(position);
                }
            }
        });

        final ImageView repeatButton = (ImageView)findViewById(R.id.repeatButton);
        repeatButton.setOnClickListener(new AdapterView.OnClickListener() {
            public void onClick(View button) {
                if (button.isSelected()){
                    button.setSelected(false);
                    isLoopingPlaylist = false;
                } else {
                    button.setSelected(true);
                    isLoopingPlaylist = true;
                }
            }
        });
    }

    /**
     * Обработчик кнопки "Next"
     */
    public void nextTrackButtonClick(View v){
        nextSong();
        final ListView listView = (ListView)findViewById(R.id.listView);
        listView.smoothScrollToPosition(currentPosition);
        adapter.setSelectedPosition(currentPosition);
    }

    /**
     * Обработчик кнопки "Prev"
     */
    public void prevTrackButtonClick(View v){
        currentPosition = currentPosition > 0 ? --currentPosition : adapter.getCount() - 1;
        playSong(currentPosition);
        final ListView listView = (ListView)findViewById(R.id.listView);
        listView.smoothScrollToPosition(currentPosition);
        adapter.setSelectedPosition(currentPosition);
    }

    /**
     * Обработчик кнопки "Play"
     */
    public void playButtonClick(View v){
        ImageView playButton = (ImageView)v.findViewById(R.id.playDownButton);
        if (mediaPlayer.isPlaying()) {
            isPaused = true;
            mediaPlayer.pause();
            playButton.setImageResource(R.drawable.play_button_dynamic);
            adapter.changeState(MyArrayAdapter.STATE_PAUSE);
        } else if (isPaused) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
            // fade эффект
            leftVol = rightVol = 0.20f;
            mediaPlayer.setVolume(leftVol, rightVol);
            final Handler h = new Handler();
            increaseVolume = new Runnable(){
                public void run(){
                    mediaPlayer.setVolume(leftVol, rightVol);
                    if(leftVol < 1.0f){
                        leftVol += .05f;
                        rightVol += .05f;
                        Log.d("Test","volume is up after pause");
                        h.postDelayed(increaseVolume, 150);
                    }
                }
            };
            h.post(increaseVolume);
            mediaPlayer.start();
            playButton.setImageResource(R.drawable.pause_button_dynamic);
            adapter.changeState(MyArrayAdapter.STATE_PLAY);
        } else {
            currentPosition = currentPosition == -1 ? 0 : currentPosition;
            playSong(currentPosition);
            adapter.setSelectedPosition(currentPosition);
        }
    }

    /**
     * Обработчик кнопки "Повторять Playlyst"
     */
    public void playlistLoopButtonClick(View v){
        isLoopingPlaylist = !isLoopingPlaylist;
    }

    private void playSong(Integer trackNumber) {
        try {
            mediaPlayer.reset();
            Integer fileResource = (Integer)elements.values().toArray()[trackNumber];
            AssetFileDescriptor afd = getResources().openRawResourceFd(fileResource);
            if (afd == null) {
                Toast.makeText(getApplicationContext(), "Could not load sound.", Toast.LENGTH_LONG).show();
                return;
            }
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();

            // fade эффект
            leftVol = rightVol = 0;
            mediaPlayer.setVolume(leftVol, rightVol);
            final Handler h = new Handler();
            increaseVolume = new Runnable(){
                public void run(){
                    mediaPlayer.setVolume(leftVol, rightVol);
                    if(leftVol < 1.0f){
                        leftVol += .05f;
                        rightVol += .05f;
                        Log.d("Test","volume is up");
                        h.postDelayed(increaseVolume, 150);
                    }
                }
            };

            decreaseVolume = new Runnable(){
                public void run(){
                    // уменьшаю громкость за 3 секунды до конца
                    if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration() - 3000) {
                        mediaPlayer.setVolume(leftVol, rightVol);
                        if(leftVol > 0f){
                            leftVol -= .033f;
                            rightVol -= .033f;
                            Log.d("Test","volume is down");
                        }
                    }
                    h.postDelayed(decreaseVolume, 100);
                }
            };

            //увеличиваю звук при нача
            h.post(increaseVolume);
            h.post(decreaseVolume);
            //
            mediaPlayer.start();

            final ImageView playButton = (ImageView)findViewById(R.id.playDownButton);
            playButton.setImageResource(R.drawable.pause_button_dynamic);

            // событие, происходящее после того как медиа-плеер закончил играть трек
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    Log.d("Test", "And the next song!");
                    playButton.setImageResource(R.drawable.play_button_dynamic);
                    nextTrackButtonClick(playButton.getRootView());
                }
            });
        } catch (IOException e) {
            Log.v(getString(R.string.app_name), e.getMessage());
        }
    }

    private void nextSong() {
        if (++currentPosition >= elements.size()) {
            // если конец списка начинаю сначала
            currentPosition = isLoopingPlaylist ? 0 : -1;
        }
        if (currentPosition != -1) {
            playSong(currentPosition);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
    }
}