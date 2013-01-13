package ru.disc.sounds3d;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    MediaPlayer mediaPlayer;

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

        ListView listView = (ListView)findViewById(R.id.listView);

        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), elements.keySet().toArray());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //убираю иконку паузы у всех элементов
                setPlayIconForAllElementsInList(parent);

                try {
                    // получаю нажатый элемент и меняю иконку
                    ImageView imageView = (ImageView)view.findViewById(R.id.icon);
                    imageView.setImageResource(R.drawable.ic_media_pause);
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Could not find element.", Toast.LENGTH_LONG).show();
                }

                // проигрываю звук
                mediaPlayer.reset();
                try {
                    Integer fileResource = (Integer)elements.values().toArray()[position];
                    AssetFileDescriptor afd = getResources().openRawResourceFd(fileResource);
                    if (afd == null) {
                        Toast.makeText(getApplicationContext(), "Could not load sound.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    afd.close();
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Could not load sound.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // событие, происходящее после того как медиа-плеер закончил играть трек
        MediaPlayer.OnCompletionListener completionListener  = new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer arg0) {
                ListView listView = (ListView)findViewById(R.id.listView);
                //TODO: убрать этот вызов и сделать смену иконки только для текущего трека который играет
                setPlayIconForAllElementsInList(listView);
            }
        };

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(completionListener);
    }

    private void setPlayIconForAllElementsInList(AdapterView<?> parent) {
        ListView lv = (ListView)parent.findViewById(R.id.listView);
        ArrayList<View> arrayListView = lv.getTouchables();
        for(View v: arrayListView){
            ImageView imageView = (ImageView)v.findViewById(R.id.icon);
            imageView.setImageResource(R.drawable.ic_media_play);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    public void showAboutActivity(View v) {
        Intent myIntent = new Intent(getApplicationContext(), HelpActivity.class);
        startActivityForResult(myIntent, 0);
    }
}
