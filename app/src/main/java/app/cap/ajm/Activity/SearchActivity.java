package app.cap.ajm.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ListView;

import app.cap.ajm.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class SearchActivity extends AppCompatActivity{

    @BindView(R.id.etQuery) EditText query;
    @BindView(R.id.list_search) ListView listview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        ButterKnife.bind(this);


	}
    @OnTouch void onTouchEdit(){

    }
}
