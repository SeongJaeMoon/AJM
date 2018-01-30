package app.cap.ajm.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.cap.ajm.Helper.SMSDBhelper;
import app.cap.ajm.Helper.SearchDBhelper;
import app.cap.ajm.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class SearchActivity extends AppCompatActivity{

    @BindView(R.id.etQuery) EditText query;
    @BindView(R.id.list_search) ListView lv;
    @BindView(R.id.btnSearch) Button btnSearch;
    private List<String> list = new ArrayList<>();
    private SearchDBhelper searchDBhelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        searchDBhelper = new SearchDBhelper(this);
        searchDBhelper.open();
        final Cursor cursor = searchDBhelper.getAllSearch();
        searchDBhelper.close();

        if (cursor.moveToFirst()) {
            do {
                String text = cursor.getString(cursor.getColumnIndex(SearchDBhelper.KEY_SEARCH));
                list.add(text);
            } while (cursor.moveToNext());
        }cursor.close();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.simplerow);
        arrayAdapter.addAll(list);
        lv.setAdapter(arrayAdapter);
    }
    @OnTouch(R.id.etQuery) boolean onTouchEdit(View view, MotionEvent motionEvent){

        return false;
    }
    @OnClick(R.id.btnSearch) void onClickSearch() {
        if(query.getText().toString().length() == 0 ) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_route), Toast.LENGTH_SHORT).show();
        }else{
            hideSoftKeyboard();
            Intent intent = new Intent(SearchActivity.this, RouteActivity.class);
            intent.putExtra("search", query.getText().toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void hideSoftKeyboard() throws NullPointerException {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(query.getWindowToken(), 0);
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(searchDBhelper!=null){
            searchDBhelper.close();
        }
    }
}
