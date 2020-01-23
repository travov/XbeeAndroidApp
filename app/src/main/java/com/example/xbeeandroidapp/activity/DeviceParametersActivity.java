package com.example.xbeeandroidapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Dialog;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xbeeandroidapp.R;
import com.example.xbeeandroidapp.api.XbeeWebApi;
import com.example.xbeeandroidapp.controller.Controller;
import com.example.xbeeandroidapp.model.ResponseObject;
import com.example.xbeeandroidapp.util.LinesDatabaseHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceParametersActivity extends AppCompatActivity {

    private static final String TAG = DeviceParametersActivity.class.getSimpleName();
    private static XbeeWebApi api;
    private String mac64bit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_parameters);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        api = Controller.getApi();
        EditText editNodeId = findViewById(R.id.edit_node_id);
        TextView mac = findViewById(R.id.mac);
        TextView mac16bit = findViewById(R.id.mac16bit);
        editNodeId.setText((String) getIntent().getExtras().get("nodeId"));
        String mac64bit = (String) getIntent().getExtras().get("xbee64bitAddress");
        this.mac64bit = mac64bit;
        mac.setText(mac64bit);
        mac16bit.setText((String) getIntent().getExtras().get("mac16bit"));
        TextView firmwareVersion = findViewById(R.id.firmware_version);
        TextView hardwareVersion = findViewById(R.id.hardware_version);
        EditText editSampling = findViewById(R.id.sampling);
        ImageButton refreshNiButton = findViewById(R.id.refresh_ni_button);
        Spinner d0Spinner = findViewById(R.id.D0);
        Spinner d1Spinner = findViewById(R.id.D1);
        Spinner d2Spinner = findViewById(R.id.D2);
        Spinner d3Spinner = findViewById(R.id.D3);
        Spinner d4Spinner = findViewById(R.id.D4);
        Spinner d5Spinner = findViewById(R.id.D5);
        Spinner p0Spinner = findViewById(R.id.P0);
        Spinner p1Spinner = findViewById(R.id.P1);
        Spinner p2Spinner = findViewById(R.id.P2);

        Resources r = getResources();
        String ni = r.getString(R.string.NI);
        String d0 = r.getString(R.string.D0);
        String d1 = r.getString(R.string.D1);
        String d2 = r.getString(R.string.D2);
        String d3 = r.getString(R.string.D3);
        String d4 = r.getString(R.string.D4);
        String d5 = r.getString(R.string.D5);
        String p0 = r.getString(R.string.P0);
        String p1 = r.getString(R.string.P1);
        String p2 = r.getString(R.string.P2);
        String vr = r.getString(R.string.VR);
        String hv = r.getString(R.string.HV);
        String ir = r.getString(R.string.IR);

        refreshNiButton.setOnClickListener(v -> api.getParameter(mac64bit, "NI").enqueue(new ParamCallback("NI", editNodeId)));
        ImageButton refreshSamplingButton = findViewById(R.id.refresh_sampling_button);
        refreshSamplingButton.setOnClickListener(v -> api.getParameter(mac64bit, "IR").enqueue(new ParamCallback("IR", editSampling))
        );
        api.getParameter(mac64bit, ni).enqueue(new ParamCallback(ni, editNodeId));
        api.getParameter(mac64bit, vr).enqueue(new ParamCallback(vr, firmwareVersion));
        api.getParameter(mac64bit, hv).enqueue(new ParamCallback(hv, hardwareVersion));
        api.getParameter(mac64bit, ir).enqueue(new ParamCallback(ir, editSampling));
        api.getParameter(mac64bit, d0).enqueue(new ParamCallback(d0, d0Spinner));
        api.getParameter(mac64bit, d1).enqueue(new ParamCallback(d1, d1Spinner));
        api.getParameter(mac64bit, d2).enqueue(new ParamCallback(d2, d2Spinner));
        api.getParameter(mac64bit, d3).enqueue(new ParamCallback(d3, d3Spinner));
        api.getParameter(mac64bit, d4).enqueue(new ParamCallback(d4, d4Spinner));
        api.getParameter(mac64bit, d5).enqueue(new ParamCallback(d5, d5Spinner));
        api.getParameter(mac64bit, p0).enqueue(new ParamCallback(p0, p0Spinner));
        api.getParameter(mac64bit, p1).enqueue(new ParamCallback(p1, p1Spinner));
        api.getParameter(mac64bit, p2).enqueue(new ParamCallback(p2, p2Spinner));

        d0Spinner.setOnItemSelectedListener(new ParamOnItemSelectedListener(api, d0, d0Spinner));
        d1Spinner.setOnItemSelectedListener(new ParamOnItemSelectedListener(api, d1, d1Spinner));
        d2Spinner.setOnItemSelectedListener(new ParamOnItemSelectedListener(api, d2, d2Spinner));
        d3Spinner.setOnItemSelectedListener(new ParamOnItemSelectedListener(api, d3, d3Spinner));
        d4Spinner.setOnItemSelectedListener(new ParamOnItemSelectedListener(api, d4, d4Spinner));
        d5Spinner.setOnItemSelectedListener(new ParamOnItemSelectedListener(api, d5, d5Spinner));
        p0Spinner.setOnItemSelectedListener(new ParamOnItemSelectedListener(api, p0, p0Spinner));
        p1Spinner.setOnItemSelectedListener(new ParamOnItemSelectedListener(api, p1, p1Spinner));
        p2Spinner.setOnItemSelectedListener(new ParamOnItemSelectedListener(api, p2, p2Spinner));
        editNodeId.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                api.setNodeIdentifier(mac64bit, editNodeId.getText().toString()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful())
                            Toast.makeText(DeviceParametersActivity.this, getResources().getString(R.string.written), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(DeviceParametersActivity.this, getResources().getString(R.string.not_change_ni), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(DeviceParametersActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, String.format("Error: %s", t.getMessage()));
                    }
                });
            }
            return false;
        });
        editSampling.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                api.setSampling(mac64bit, editSampling.getText().toString()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful())
                            Toast.makeText(DeviceParametersActivity.this, getResources().getString(R.string.written), Toast.LENGTH_LONG).show();
                        else {
                            Toast.makeText(DeviceParametersActivity.this, getResources().getString(R.string.not_change_ir), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, String.format("Error response: %d %s", response.code(), response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(DeviceParametersActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, String.format("Error: %s", t.getMessage()));
                    }
                });
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_parameters, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.write_changes_button: {
                api.write(mac64bit).enqueue(new Callback<ResponseObject>() {
                    @Override
                    public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                        if (response.isSuccessful())
                            Toast.makeText(DeviceParametersActivity.this, getResources().getString(R.string.written), Toast.LENGTH_LONG).show();
                        else Toast.makeText(DeviceParametersActivity.this, getResources().getString(R.string.not_written), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseObject> call, Throwable t) {
                        Toast.makeText(DeviceParametersActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, String.format("Error: %s", t.getMessage()));
                    }
                });
            }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.refresh_all:
                Resources r = getResources();
                String ni = r.getString(R.string.NI);
                String d0 = r.getString(R.string.D0);
                String d1 = r.getString(R.string.D1);
                String d2 = r.getString(R.string.D2);
                String d3 = r.getString(R.string.D3);
                String d4 = r.getString(R.string.D4);
                String d5 = r.getString(R.string.D5);
                String p0 = r.getString(R.string.P0);
                String p1 = r.getString(R.string.P1);
                String p2 = r.getString(R.string.P2);
                String ir = r.getString(R.string.IR);
                api.getParameter(mac64bit, ni).enqueue(new ParamCallback(ni, findViewById(R.id.edit_node_id)));
                api.getParameter(mac64bit, ir).enqueue(new ParamCallback(ir, findViewById(R.id.sampling)));
                api.getParameter(mac64bit, d0).enqueue(new ParamCallback(d0, findViewById(R.id.D0)));
                api.getParameter(mac64bit, d1).enqueue(new ParamCallback(d1, findViewById(R.id.D1)));
                api.getParameter(mac64bit, d2).enqueue(new ParamCallback(d2, findViewById(R.id.D2)));
                api.getParameter(mac64bit, d3).enqueue(new ParamCallback(d3, findViewById(R.id.D3)));
                api.getParameter(mac64bit, d4).enqueue(new ParamCallback(d4, findViewById(R.id.D4)));
                api.getParameter(mac64bit, d5).enqueue(new ParamCallback(d5, findViewById(R.id.D5)));
                api.getParameter(mac64bit, p0).enqueue(new ParamCallback(p0, findViewById(R.id.P0)));
                api.getParameter(mac64bit, p1).enqueue(new ParamCallback(p1, findViewById(R.id.P1)));
                api.getParameter(mac64bit, p2).enqueue(new ParamCallback(p2, findViewById(R.id.P2)));
                return true;
        }
        return true;
    }

    public void onClick(View view) {
        Set<Integer> checkedLines = new HashSet<>();
        View checkBoxesView = getLayoutInflater().inflate(R.layout.change_detection_layout, null);
        List<View> checkboxes = getAllViewChildren(checkBoxesView);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeviceParametersActivity.this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle(R.string.change_detection);
        AlertDialog dialog = alertDialogBuilder.create();

        Map<String, Integer> linesMap = new HashMap<>();
        SQLiteOpenHelper helper = new LinesDatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("LINES", new String[]{"AT", "AT_INDEX"}, null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast();cursor.moveToNext())
            linesMap.put(cursor.getString(0), cursor.getInt(1));
        cursor.close();
        db.close();
        api.getChangeDetection(mac64bit).enqueue(new Callback<ResponseObject<String, Set<Integer>>>() {
            @Override
            public void onResponse(Call<ResponseObject<String, Set<Integer>>> call, Response<ResponseObject<String, Set<Integer>>> response) {
                if (response.isSuccessful()) {
                    Map<String, Set<Integer>> mapWithLines = response.body().getParameters();
                    if (mapWithLines != null) {
                        Set<Integer> linesSet = mapWithLines.get("IC");
                        for (View c: checkboxes) {
                            CheckBox checkBox = (CheckBox) c;
                            String at = checkBox.getText().toString();
                            Integer index = linesMap.get(at);
                            if (linesSet.contains(index))
                                checkBox.setChecked(true);
                        }
                    }
                    else Toast.makeText(DeviceParametersActivity.this, getResources().getString(R.string.null_response), Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(DeviceParametersActivity.this, "Cannot get IC parameter!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseObject<String, Set<Integer>>> call, Throwable t) {
                Toast.makeText(DeviceParametersActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, String.format("Error: %s", t.getMessage()));
            }
        });
        dialog.setButton(Dialog.BUTTON_POSITIVE, getResources().getString(R.string.apply), (dialog1, which) -> {
            for (View c: checkboxes) {
                CheckBox checkBox = (CheckBox) c;
                Integer index = linesMap.get(checkBox.getText().toString());
                if (checkBox.isChecked()) checkedLines.add(index);
            }
            api.setChangeDetection(mac64bit, checkedLines).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(DeviceParametersActivity.this, "Cannot change parameters!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(DeviceParametersActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
        dialog.setButton(Dialog.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), (dialog12, which) -> dialog.dismiss());
        dialog.setView(checkBoxesView);
        dialog.show();
        dialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        dialog.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private List<View> getAllViewChildren(View v) {
        if (!(v instanceof ViewGroup)) {
            List<View> viewArrayList = new ArrayList<>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        List<View> result = new ArrayList<>();
        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            List<View> viewArrayList = new ArrayList<>();
            //viewArrayList.add(v);
            viewArrayList.addAll(getAllViewChildren(child));
            result.addAll(viewArrayList);
        }
        return result;
    }


    private class ParamCallback implements Callback<ResponseObject> {

        private String param;
        private View view;

        public ParamCallback(String param, View view) {
            this.param = param;
            this.view = view;
        }

        @Override
        public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
            if (response.isSuccessful()) {
                Map<String, String> parameters = response.body().getParameters();
                if (view instanceof Spinner) {
                    Spinner spinner = (Spinner) view;
                    if (parameters != null && parameters.containsKey(param))
                        spinner.setSelection(Integer.valueOf(parameters.get(param).substring(1)));
                }
                else if (view instanceof EditText || view instanceof TextView) {
                    TextView tw = (TextView) view;
                    String value = param.equals("IR") ? String.valueOf(Integer.parseInt(parameters.get(param), 16)): parameters.get(param);
                    tw.setText(value);

                }
            } else {
                Log.e(TAG, String.format("Error response: %d %s", response.code(), response.message()));
                Toast.makeText(DeviceParametersActivity.this, "Cannot get " + param + " parameter!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ResponseObject> call, Throwable t) {
            Toast.makeText(DeviceParametersActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, String.format("Error: %s", t.getMessage()));
        }
    }

    private class ParamOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        private XbeeWebApi api;
        private String param;
        private Spinner spinner;
        private int oldPosition;

        public ParamOnItemSelectedListener(XbeeWebApi api, String param, Spinner spinner) {
            this.api = api;
            this.param = param;
            this.spinner = spinner;
            this.oldPosition = spinner.getSelectedItemPosition();
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            api.setParameter(mac64bit, param, String.valueOf(position)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, String.format("Error response: %d %s", response.code(), response.message()));
                        Toast.makeText(DeviceParametersActivity.this, "Cannot set " + param + " parameter!", Toast.LENGTH_SHORT).show();
                        spinner.setSelection(oldPosition);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(DeviceParametersActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, String.format("Error: %s", t.getMessage()));

                }
            });
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
