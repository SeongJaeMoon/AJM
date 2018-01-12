package app.cap.ajm.Helper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;

import app.cap.ajm.R;

public class TrackAdapter extends CursorAdapter{

    private Context mContext;
    private int startAddr;
    private int endAddr;
    private int startTime;
    private int endTime;
    private int avgSpeed;
    private int calorie;
    private int distance;
    private int temps;
    private int wets;

    public TrackAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
        mContext = context;
        startAddr = cursor.getColumnIndex("startAddr");
        endAddr = cursor.getColumnIndex("endAddr");
        startTime = cursor.getColumnIndex("startTime");
        endTime = cursor.getColumnIndex("endTime");
        avgSpeed = cursor.getColumnIndex("avgSpeed");
        calorie = cursor.getColumnIndex("calorie");
        distance = cursor.getColumnIndex("distance");
        temps = cursor.getColumnIndex("temp");
        wets = cursor.getColumnIndex("wet");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.custom_listview, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor){
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        final String sttime = cursor.getString(startTime);
        final String endtime = cursor.getString(endTime);
        final String stAddr = cursor.getString(startAddr);
        final String edAddr = cursor.getString(endAddr);
        double speed = cursor.getDouble(avgSpeed);
        double dis = cursor.getDouble(distance);
        double cal = cursor.getDouble(calorie);
        double temp = cursor.getDouble(temps);
        double wet = cursor.getDouble(wets);

        TextView stTime = (TextView) view.findViewById(R.id.startTime);
        TextView edTime = (TextView) view.findViewById(R.id.endTime);
        TextView startA = (TextView) view.findViewById(R.id.startAddr);
        TextView endA = (TextView) view.findViewById(R.id.endAddr);
        TextView avgsp = (TextView) view.findViewById(R.id.Avg);
        TextView distanceView = (TextView) view.findViewById(R.id.Distance);
        TextView calorieView = (TextView) view.findViewById(R.id.Calorie);
        TextView tempView = (TextView) view.findViewById(R.id.Temp);
        TextView wetView = (TextView) view.findViewById(R.id.Wet);

                stTime.setText(sttime);
                edTime.setText(endtime);
                startA.setText(stAddr);
                endA.setText(edAddr);
                avgsp.setText(String.valueOf(decimalFormat.format(speed))+"km/h");
            if (cal/1000f<1) {
                calorieView.setText(String.valueOf(decimalFormat.format(cal))+"cal");
            }else {
                calorieView.setText(String.valueOf(decimalFormat.format(cal/1000f))+"kcal");
            }
            if (dis/1000f<1) {
                distanceView.setText(String.valueOf(decimalFormat.format(dis))+"m");
            }else{
                distanceView.setText(String.valueOf(decimalFormat.format(dis/1000f))+"km");
            }
                tempView.setText(String.valueOf(decimalFormat.format(temp))+"°C");
                wetView.setText(String.valueOf(decimalFormat.format(wet))+"%");
    }
}
