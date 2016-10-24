package com.smsapplication.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.smsapplication.R;
import com.smsapplication.utils.RecyclerViewItemClickListener;
import com.smsapplication.adapters.SMSAdapter;
import com.smsapplication.models.SMSData;
import com.smsapplication.database.UploadData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by harshita30 on 20/10/16.
 */

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private SMSAdapter smsAdapter;
    private ArrayList<SMSData> smsDataList;
    private Cursor c;
    private ExportTask exportTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.messages_list);
        smsDataList = new ArrayList<SMSData>();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SendSmsActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onResume(){
        super.onResume();
        final ArrayList<SMSData> smslist, smsgrouplist;
        smslist = new ArrayList<>();
        smsgrouplist = new ArrayList<>();

        int REQUEST_CODE_ASK_PERMISSIONS = 123;
        try{
            if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {}
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);}

            Uri uriSms = Uri.parse("content://sms/inbox");
            c = getContentResolver().query(uriSms,new String[]{"_id", "address", "date", "body"},null,null,null);
            StringBuilder sb = new StringBuilder();

            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    String address = c.getString(c.getColumnIndexOrThrow("address"));
                    String date = c.getString(c.getColumnIndexOrThrow("date"));
                    String body = c.getString(c.getColumnIndexOrThrow("body"));
                    sb.append(address).append("\n");
                    sb.append(body).append("\n");
                    sb.append(date).append("\n");
                    sb.append("\n");
                    smslist.add(new SMSData(address, date, body));
                }
                c.close();
            }

            smsDataList= smslist;
            Map<String, SMSData> map = new LinkedHashMap<>();

            for (SMSData message : smslist) {

                SMSData existingValue = map.get(message.address);
                if(existingValue == null){
                    map.put(message.address, message);
                }
            }

            smsgrouplist.clear();
            smsgrouplist.addAll(map.values());

            smsAdapter= new SMSAdapter(MainActivity.this);
            smsAdapter.updateList(smsgrouplist);
            recyclerView.setAdapter(smsAdapter);

            recyclerView.addOnItemTouchListener(
                    new RecyclerViewItemClickListener(getApplicationContext(), new RecyclerViewItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            // TODO Handle item click
                            ArrayList<SMSData> smsinsidegroup = new ArrayList<SMSData>();

                            String n = smsgrouplist.get(position).address;

                            for (int i = 0; i < smslist.size(); i++) {
                                if(smslist.get(i).address.equals(n))
                                    smsinsidegroup.add(smslist.get(i));
                            }

                            Intent i = new Intent(MainActivity.this, ReadAllSmsActivity.class);
                            i.putParcelableArrayListExtra("messages", smsinsidegroup);
                            startActivity(i);
                        }
                    })
            );


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    class ExportTask extends AsyncTask<Void, Integer, Uri> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Exporting to file ...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Uri doInBackground(Void... params) {

            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                FileOutputStream fos = null;
                try {
                    String file_name ="SmsList.txt";
                    File file = new File(getFilesDir(),file_name);
                    if (!file.exists())
                        file.createNewFile();
                    fos = openFileOutput(file_name, Context.MODE_PRIVATE);

                    Uri uriSms = Uri.parse("content://sms/inbox");
                    c = getContentResolver().query(uriSms,new String[]{"_id", "address", "date", "body"},null,null,null);
                    int count = c.getCount(), i = 0;

                    StringBuilder sb = new StringBuilder();
                    if (c.moveToFirst()) {
                        do {
                            sb.append(c.getString(c.getColumnIndex("address")))
                                    .append("\n");
                            sb.append(c.getString(c.getColumnIndex("body")))
                                    .append("\n");
                            sb.append(c.getString(c.getColumnIndex("date")))
                                    .append("\n");
                            sb.append("\n");
                            publishProgress(++i*100/count);
                        } while (!isCancelled() && c.moveToNext());
                    }
                    fos.write(sb.toString().getBytes());
                    return Uri.fromFile(file);

                } catch (Exception e) {
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {}
                    }
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Uri result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            if (result == null) {
                Toast.makeText(MainActivity.this, "Export task failed!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Intent i = new Intent(MainActivity.this,UploadData.class);
            startActivity(i);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_search) {
            Intent i = new Intent(MainActivity.this,SearchActivity.class);
            i.putParcelableArrayListExtra("search", smsDataList);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_share) {
            exportTask = new ExportTask();
            exportTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        if (exportTask != null) {
            exportTask.cancel(false);
            exportTask.pDialog.dismiss();
        }
        super.onPause();
    }

}
