package ru.disc.sounds3d;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
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
        ListView listView = (ListView)findViewById(R.id.listView);

        // заполняю список элементами из массива
        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), elements.keySet().toArray());
        listView.setAdapter(adapter);

        // обработчик нажатия по элементу списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                Log.d("Test", "Position: " + position);
                playSong(position);
            }
        });
    }

    private void changeBackgroundForListElement(int position, int direction) {
        try {
            // получаю нажатый элемент и меняю иконку
            ListView listView = (ListView)findViewById(R.id.listView);
            // получаю позицию следующего элемента c учетом начального элемента в списке на экране
            // например, если виден не весь список, а только нижняя часть то надо учитывать getFirstVisiblePosition
            int localPosition = position > 0 ? position - listView.getFirstVisiblePosition() : position;
            listView.smoothScrollToPosition(position);

            // меняю фон и иконку для текущего проигрываемого элемента
            setBackgroundToListElement(listView, localPosition, 1);

            if (localPosition > 0) {
                int prevElementPos = direction == 1 ? localPosition  - 1 : localPosition  + 1;
                // меняю фон и иконку для предыдущего

                setBackgroundToListElement(listView, prevElementPos, 0);
            }
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Could not find element.", Toast.LENGTH_LONG).show();
        }
    }

    private void setBackgroundToListElement(ListView listView, int position, int active) {
        boolean status = (active == 1);
        View view = listView.getChildAt(position);
        TextView elemTitle = (TextView)view.findViewById(R.id.elTitle);
        elemTitle.setTextColor(status ? Color.BLACK : Color.WHITE);
        elemTitle.setBackgroundResource(status ? R.drawable.list_item_act : R.drawable.list_item);
        ImageView imageView = (ImageView)view.findViewById(R.id.icon);
        imageView.setImageResource(status ? R.drawable.ic_media_pause : R.drawable.ic_media_play);
    }

    /**
     * Обработчик кнопки "Next"
     * @param v
     */
    public void nextTrackButtonClick(View v){
        nextSong();
        changeBackgroundForListElement(currentPosition, 1);
    }

    /**
     * Обработчик кнопки "Prev"
     * @param v
     */
    public void prevTrackButtonClick(View v){
        currentPosition = currentPosition > 0 ? --currentPosition : currentPosition;
        playSong(currentPosition);
        changeBackgroundForListElement(currentPosition, 0);
    }

    /**
     * Обработчик кнопки "Play"
     * @param v
     */
    public void playButtonClick(View v){
        if (mediaPlayer.isPlaying()) {
            isPaused = true;
            mediaPlayer.pause();
        } else if (isPaused) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
            mediaPlayer.start();
        } else {
            playSong(currentPosition);
            changeBackgroundForListElement(currentPosition, 1);
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

            // событие, происходящее после того как медиа-плеер закончил играть трек
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    nextSong();
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

//    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                Log.d("Test", "Back button pressed!");
//                mediaPlayer.pause();
//                break;
//            case KeyEvent.KEYCODE_HOME:
//                Log.d("Test", "Home button pressed!");
//                finish();
//                break;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    protected void onStop() {
//        Log.d("Test", "Home button pressed!");
//        super.onStop();
//    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

//    public void showAboutActivity(View v) {
//        Intent myIntent = new Intent(getApplicationContext(), HelpActivity.class);
//        startActivityForResult(myIntent, 0);
//    }
}
