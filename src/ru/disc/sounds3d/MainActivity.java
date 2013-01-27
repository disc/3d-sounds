package ru.disc.sounds3d;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int currentPosition = 0;
    private boolean isPaused = false;
    MySimpleArrayAdapter adapter;

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
        adapter = new MySimpleArrayAdapter(getApplicationContext(), elements.keySet().toArray());
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
        } else if (isPaused) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
            mediaPlayer.start();
            playButton.setImageResource(R.drawable.pause_button_dynamic);
        } else {
            playSong(currentPosition);
            adapter.setSelectedPosition(currentPosition);
        }
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
            mediaPlayer.start();

            final ImageView playButton = (ImageView)findViewById(R.id.playDownButton);
            playButton.setImageResource(R.drawable.pause_button_dynamic);

            // событие, происходящее после того как медиа-плеер закончил играть трек
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    Log.d("Test", "And the next song!");
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
            //TODO: добавить проверку что включен repeat
            currentPosition = 0;
        }
        playSong(currentPosition);
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