package com.example.xbeeandroidapp.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.xbeeandroidapp.R;
import com.example.xbeeandroidapp.api.XbeeWebApi;
import com.example.xbeeandroidapp.controller.Controller;
import com.example.xbeeandroidapp.model.XbeeDevice;
import com.example.xbeeandroidapp.util.MinMaxTextWatcher;
import com.example.xbeeandroidapp.util.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DevicesActivity extends AppCompatActivity {

    private static final String TAG = DevicesActivity.class.getSimpleName();
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static XbeeWebApi api;
    private static RecyclerView recyclerView;
    private static Integer timeout = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        TextView noActiveDevicesView = findViewById(R.id.no_active_devices);
        try {
            api = Controller.getApi();
            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(new ArrayList<>());
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(DevicesActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (savedInstanceState != null) {
            RecyclerViewAdapter adapter = (RecyclerViewAdapter) recyclerView.getAdapter();
            adapter.setDevices(savedInstanceState.getParcelableArrayList(KEY_RECYCLER_STATE));
            noActiveDevicesView.setVisibility(View.GONE);
        }
        else noActiveDevicesView.setVisibility(View.VISIBLE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnLongClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DevicesActivity.this);
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.setTitle(R.string.timeout);
            dialog.setView(getLayoutInflater().inflate(R.layout.timeout_layout, null));
            dialog.setButton(Dialog.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), (dialog1, which) -> dialog.dismiss());
            dialog.setButton(Dialog.BUTTON_POSITIVE, getResources().getString(R.string.apply), (dialog12, which) -> {
                EditText editTimeout = dialog.findViewById(R.id.edit_timeout);
                timeout = Integer.parseInt(editTimeout.getText().toString());
                dialog.dismiss();
            });
            dialog.show();
            EditText editTimeout = dialog.findViewById(R.id.edit_timeout);
            editTimeout.setText(String.valueOf(timeout));
            editTimeout.addTextChangedListener(new MinMaxTextWatcher(getApplicationContext(), dialog.getButton(AlertDialog.BUTTON_POSITIVE), dialog.findViewById(R.id.range_hint), 3200, 25500));
            dialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            dialog.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            return true;
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        RecyclerViewAdapter adapter = (RecyclerViewAdapter) recyclerView.getAdapter();
        if (recyclerView != null) {
            outState.putParcelableArrayList(KEY_RECYCLER_STATE, (ArrayList<? extends Parcelable>) adapter.getDevices());
        }
    }

    public void onClick(View v) {
        RecyclerViewAdapter adapter = (RecyclerViewAdapter) recyclerView.getAdapter();
        List<XbeeDevice> devices = adapter.getDevices();
        TextView noActiveDevicesView = findViewById(R.id.no_active_devices);
        if (devices != null && !devices.isEmpty()) {
            devices.clear();
            noActiveDevicesView.setVisibility(View.VISIBLE);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        api = Controller.getApi();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DevicesActivity.this);
        alertDialogBuilder.setCancelable(false);
        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.setTitle(R.string.discovering);
        dialog.setView(getLayoutInflater().inflate(R.layout.progress_bar_layout, null));
        dialog.show();
        api.startDiscoveryProcess(timeout).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                //sleep 5000
                if (response.isSuccessful()) {
                    new Handler().postDelayed(() -> api.getDevices(true).enqueue(new Callback<List<XbeeDevice>>() {
                        @Override
                        public void onResponse(Call<List<XbeeDevice>> call1, Response<List<XbeeDevice>> response1) {
                            if (response1.isSuccessful()) {
                                devices.addAll(response1.body());
                                recyclerView.getAdapter().notifyDataSetChanged();
                                if (!devices.isEmpty())
                                    noActiveDevicesView.setVisibility(View.GONE);
                                dialog.dismiss();
                            }
                            else {
                                dialog.dismiss();
                                Log.e(TAG, String.format("Error response: %d %s", response1.code(), response1.message()));
                                Toast.makeText(DevicesActivity.this, getResources().getString(R.string.not_get_devices), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<XbeeDevice>> call1, Throwable t) {
                            Toast.makeText(DevicesActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, String.format("Error: %s", t.getMessage()));
                            dialog.dismiss();
                        }
                    }), timeout + 500);
                }
                else {
                    dialog.dismiss();
                    Log.e(TAG, String.format("Error response: %d %s", response.code(), response.message()));
                    Toast.makeText(DevicesActivity.this, getResources().getString(R.string.not_start_discovery), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DevicesActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, String.format("Error: %s", t.getMessage()));
                dialog.dismiss();
            }
        });
    }
}
