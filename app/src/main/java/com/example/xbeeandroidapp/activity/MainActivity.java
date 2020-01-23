package com.example.xbeeandroidapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import com.example.xbeeandroidapp.R;
import com.example.xbeeandroidapp.api.XbeeWebApi;
import com.example.xbeeandroidapp.controller.Controller;
import com.example.xbeeandroidapp.util.HistoryDatabaseHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static XbeeWebApi api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        SQLiteOpenHelper helper = new HistoryDatabaseHelper(this);

        final AutoCompleteTextView serverIp = findViewById(R.id.server_ip);
        try {
            serverIp.setOnKeyListener((v, keyCode, event) -> {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Intent intent = new Intent(MainActivity.this, DevicesActivity.class);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    String baseUrl = serverIp.getText().toString();
                    if (!baseUrl.startsWith("http://"))
                        baseUrl = "http://" + baseUrl;
                    baseUrl += "/io/";
                    values.put("URL", baseUrl);
                    db.insert("HISTORY", null, values);
                    db.close();

                    Controller.updateBaseUrl(baseUrl);
                    api = Controller.getApi();
                    api.checkIfReachable().enqueue(new CheckCallback(intent));
                }
                return false;
            });
            SQLiteDatabase db = helper.getWritableDatabase();
            List<String> history = new ArrayList<>();
            Cursor cursor = db.query("HISTORY", new String[]{"URL"},null, null, null, null, "_id DESC", "5");
            for (cursor.moveToFirst(); !cursor.isAfterLast();cursor.moveToNext())
                history.add(cursor.getString(0));
            serverIp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, history));
            cursor.close();
            db.close();

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onClick(View v) {
        try {
            EditText serverIp = findViewById(R.id.server_ip);
            Intent intent = new Intent(MainActivity.this, DevicesActivity.class);
            SQLiteOpenHelper helper = new HistoryDatabaseHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            String baseUrl = serverIp.getText().toString();
            if (!baseUrl.startsWith("http://"))
                baseUrl = "http://" + baseUrl;
            baseUrl += "/io/";
            values.put("URL", baseUrl);
            db.insert("HISTORY", null, values);
            Controller.updateBaseUrl(baseUrl);
            api = Controller.getApi();
            api.checkIfReachable().enqueue(new CheckCallback(intent));
        }
        catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class CheckCallback implements Callback<Void> {
        private Intent intent;

        public CheckCallback(Intent intent) {
            this.intent = intent;
        }

        @Override
        public void onResponse(Call call, Response response) {
            if (response.isSuccessful()) {
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Cannot reach remote host", Toast.LENGTH_SHORT).show();
                Log.e(TAG, String.format("Error response: %d %s", response.code(), response.message()));
                ResponseBody errorBody = response.errorBody();
                if (errorBody != null) {
                    try {
                        Log.e(TAG, "Error body:\n" + errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        }
    }
}
