package ru.disc.sounds3d;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<Object> {
    private final Context context;
    private final Object[] values;
    private int selectedPos = -1;
    private int currentState = STATE_STOP;
    public static final int STATE_STOP = -1;
    public static final int STATE_PAUSE = 0;
    public static final int STATE_PLAY = 1;

    public MyArrayAdapter(Context context, Object[] values) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.values = values;
    }

    public void setSelectedPosition(int pos){
        selectedPos = pos;
        changeState(STATE_PLAY);
        // inform the view of this change
    }

    public int getSelectedPosition(){
        return selectedPos;
    }

    public void changeState(int state) {
        currentState = state;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        // only inflate the view if it's null
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.rowlayout, null);
        }
        TextView textView = (TextView) v.findViewById(R.id.elTitle);
        ImageView imageView = (ImageView) v.findViewById(R.id.icon);
        textView.setText(textView.getContext().getResources().getText((Integer)values[position]));

        int titleDrawableResId, iconDrawableResId;
        if (selectedPos == position) {
            titleDrawableResId = R.drawable.list_item_act;
            iconDrawableResId = R.drawable.ic_media_pause;

            if (currentState == STATE_PAUSE) {
                iconDrawableResId = R.drawable.ic_media_play;
            }
        } else {
            titleDrawableResId = R.drawable.list_item;
            iconDrawableResId = R.drawable.ic_media_play;
        }

        textView.setBackgroundDrawable(getContext().getResources().getDrawable(titleDrawableResId));
        imageView.setBackgroundDrawable(getContext().getResources().getDrawable(iconDrawableResId));

        return v;
    }
} 