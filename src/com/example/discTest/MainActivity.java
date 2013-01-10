package com.example.discTest;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void firstSound(View v) {
        final MediaPlayer mp1 = MediaPlayer.create(getApplicationContext(), R.raw.modem_sound);
        mp1.start();

        CharSequence text = getApplicationContext().getResources().getText(R.string.first_sound_toast);

        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        toast.show();
    }

    public void showAboutActivity(View v) {
        Intent myIntent = new Intent(getApplicationContext(), HelpActivity.class);
        startActivityForResult(myIntent, 0);
    }
}
