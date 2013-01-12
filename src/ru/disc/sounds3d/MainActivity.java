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

                Object o = parent.getAdapter().getItem(position);
                String keyword = o.toString();
                Toast.makeText(getApplicationContext(), "You selected: " + keyword, Toast.LENGTH_SHORT).show();
                //убираю иконку паузы у всех элементов

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
//                    mediaPlayer.start();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Could not load sound.", Toast.LENGTH_LONG).show();
                }
            }
        });

        mediaPlayer = new MediaPlayer();
    }


    public void showAboutActivity(View v) {
        Intent myIntent = new Intent(getApplicationContext(), HelpActivity.class);
        startActivityForResult(myIntent, 0);
    }
}
