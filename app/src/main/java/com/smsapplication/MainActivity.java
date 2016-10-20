package com.smsapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
//    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private SMSAdapter smsAdapter;
    private List<SMSData> smsDataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        smsDataList = new ArrayList<SMSData>();
   /*     fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NewMessageActivity.class);
                startActivity(intent);
            }
        });
*/
        final int REQUEST_CODE_ASK_PERMISSIONS = 123;
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            constructData();
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        smsAdapter = new SMSAdapter(smsDataList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(smsAdapter);

    }
    public void constructData() {
        try {
            Cursor cursor = null;
            SMSData smsData =null;
            Uri uriSms = Uri.parse("content://sms/inbox");
            cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String body = cursor.getString(3);
                    String address = cursor.getString(1);
                    String date = (cursor.getString(2));
                    smsData = new SMSData();
                    smsData.setContent(body);
                    smsData.setNumber(address);
                    smsDataList.add(smsData);
                }
                cursor.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
