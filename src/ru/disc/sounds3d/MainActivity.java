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

public class MainActivity extends Activity {
    String[] values = new String[] {
        "Звук 1", "Звук 2", "Звук 3", "Звук 4", "Звук 5"
    };
    Integer[] sounds = new Integer[] {
       R.raw.modem_sound,
       R.raw.hiphopopotamus,
       R.raw.hiphopopotamus,
       R.raw.hiphopopotamus,
       R.raw.hiphopopotamus
    };
    MediaPlayer mediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ListView listView = (ListView)findViewById(R.id.listView);

        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //убираю иконку паузы у всех элементов
                setPlayIconForAllElementsInList(parent);

                // получаю нажатый элемент и меняю иконку
                ImageView imageView = (ImageView)view.findViewById(R.id.icon);
                imageView.setImageResource(true ? R.drawable.ic_media_pause : R.drawable.ic_media_play);

                // проигрываю звук
                mediaPlayer.reset();
                try {
                    AssetFileDescriptor afd = getResources().openRawResourceFd(sounds[position]);
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

        MediaPlayer.OnCompletionListener completionListener  = new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer arg0) {
                Toast.makeText(getApplicationContext(), "Media playing is complete", Toast.LENGTH_LONG).show();
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


    public void showAboutActivity(View v) {
        Intent myIntent = new Intent(getApplicationContext(), HelpActivity.class);
        startActivityForResult(myIntent, 0);
    }
}
