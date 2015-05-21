package com.practice.ankur.contacts.activites;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.practice.ankur.contacts.R;
import com.practice.ankur.contacts.Utils.DataHandler;
import com.practice.ankur.contacts.services.GetForeGroundProcessInfo;


public class ListContacts extends ListActivity {

    @Override
    public int getSelectedItemPosition() {
        return super.getSelectedItemPosition();
    }

    @Override
    public long getSelectedItemId() {
        return super.getSelectedItemId();
    }

    ListView lv;
    Cursor cursor;
    DataHandler dataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);
        //Start service to check if power source is connected. Ignore.
        startService(new Intent(this, GetForeGroundProcessInfo.class));
        //Check if first load
            //Load all the contacts to sqlite
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            startManagingCursor(cursor);
            dataHandler = new DataHandler(getBaseContext());
            dataHandler.open();
            while (cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID));
                dataHandler.insert(Long.parseLong(contactId), name, number, image);
                Log.d(ListContacts.class.getName(), "Name : " + name + " Number : " + number + " Image_uri : " + image + " Id : " + contactId);
            }
        Cursor cursor = dataHandler.getAll();
        String[] from = {DataHandler.NAME, DataHandler.NUMBER, DataHandler.CONTENTID};
        int[] to = {android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);
        setListAdapter(cursorAdapter);
        lv = getListView();
        dataHandler.close();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tname = (TextView) view.findViewById(android.R.id.text1);
                TextView tnumber = (TextView) view.findViewById(android.R.id.text2);
                String name = (String) tname.getText();
                String number = (String) tnumber.getText();

                Intent intent = new Intent(getApplicationContext(), ContactDetails.class);
                intent.putExtra("name", name);
                intent.putExtra("number", number);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
