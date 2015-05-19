package com.practice.ankur.contacts.activites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.practice.ankur.contacts.R;
import com.practice.ankur.contacts.Utils.DataHandler;

public class ContactDetails extends ActionBarActivity {

    private static final int CAMERA_REQUEST = 1888;
    TextView contactName, contactNumber;
    ImageView img;
    String name, number;
    Uri imageUri;
    DataHandler dataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        contactName = (TextView) findViewById(R.id.contactName);
        contactNumber = (TextView) findViewById(R.id.contactNumber);
        img = (ImageView) findViewById(R.id.imageView);
        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        number = intent.getStringExtra("number");

        contactName.setText(name);
        contactNumber.setText(number);

        dataHandler = new DataHandler(getApplicationContext());
        dataHandler.open();
        String timageUri = dataHandler.getImageUriFromNumber(number);
        Log.i(ContactDetails.class.getName(), "Got imageUri : " + timageUri);
        if (timageUri != null){
            imageUri = Uri.parse(timageUri);
            img.setImageURI(imageUri);
        }else{
            img.setImageResource(R.mipmap.ic_launcher);
        }
        dataHandler.close();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_details2, menu);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            img.setImageBitmap(photo);
            imageUri = data.getData();
            img.setImageURI(imageUri);
            dataHandler = new DataHandler(getApplicationContext());
            dataHandler.open();
            dataHandler.setImageUriFromNumber(number, imageUri);
            dataHandler.close();
        }
    }
}
