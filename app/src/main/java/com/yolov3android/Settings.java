package com.yolov3android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Switch;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Settings extends AppCompatActivity {
    int temp_spinner_num = 1;
    String where_name;

    Switch toggle;
    private Button button;
    private TestOpenHelper helper;
    private SQLiteDatabase db;
    List<String> cocoNames ;
    String temp_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.actibity_settings);

        button = findViewById(R.id.backbutton);
        Resources res = getResources();
        helper = new TestOpenHelper(getApplicationContext());
        db = helper.getWritableDatabase();
        cocoNames = Arrays.asList("jagaimo","beniharuka","shadowqueen","satoimo","ninjin","daikon","koushindaikon","mixcarrot","tamanegi","mushroom","fruitkabu","stickbroccoli","negi","haninniku","serori","hourensou","dill","wasabina","nabana","babyleaf","radish","pakuchi","akakarashina","leaflettuce_red","leaflettuce_green","komatsuma","bekana","shungiku","saladmizuna","akasaladhourensou","lemon","rukkora","oaklettuce_red","oaklettuce_green","ringo","ourin","kikuimo","saladset","gorugo","shitakke","kokabu","hakusai","ninniku","akadaikon","renkon");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        for(int temp=0;temp<cocoNames.size();temp++){
            String spinner_name = "spinner" + temp;
            String switch_name = "toggle" + temp;
            temp_name = cocoNames.get(temp);
            final String name[] = {temp_name};
            int spinner_ID = res.getIdentifier(spinner_name,"id",getPackageName());
            int switch_ID = res.getIdentifier(switch_name, "id", getPackageName());
            toggle = (Switch) findViewById(switch_ID);
            Spinner spinner = findViewById(spinner_ID);
            where_name = "name = ?";

            Cursor cursor = db.query(
                    "testdb",
                    new String[] { "name", "quantity","alarm" },
                    where_name,
                    name,
                    null,
                    null,
                    null
            );
            cursor.moveToFirst();
            int setting_num = cursor.getInt(1);
            int alarm = cursor.getInt(2);

            spinner.setSelection(setting_num);

            if(alarm == 1){
                toggle.setChecked(true);
            }
            if(alarm == 0){
                toggle.setChecked(false);
            }

            cursor.close();

            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                //　アイテムが選択された時
                @Override
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int position, long id) {
                    Spinner spinner = (Spinner)parent;
                    String item = (String)spinner.getSelectedItem();
                    int num = Integer.parseInt(item);
                    ContentValues value = new ContentValues();
                    value.put("quantity",num);
                    db.update("testdb",value,where_name,name);
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    //
                }
            });

            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // 状態が変更された
                    if (isChecked == true) {
                        ContentValues value = new ContentValues();
                        value.put("alarm",1);
                        db.update("testdb",value,where_name,name);
                    }
                    if (isChecked == false){
                        ContentValues value = new ContentValues();
                        value.put("alarm",0);
                        db.update("testdb",value,where_name,name);
                    }
                }
            });
        }
    }
}