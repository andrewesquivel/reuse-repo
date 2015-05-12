package s198.project2.reuse;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.cloudinary.Cloudinary;
import com.firebase.client.Firebase;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EditActivity extends Activity {

    private Item item;
    private EditText editName;
    private EditText editDescription;
    private EditText editLocation;
    private Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Bundle data = getIntent().getExtras();
        item = (Item) data.getParcelable("item");

        editName = (EditText) findViewById(R.id.nameInput);
        editDescription = (EditText) findViewById(R.id.descriptionInput);
        editLocation = (EditText) findViewById(R.id.locationInput);

        editName.setText(item.getName());
        editDescription.setText(item.getDescription());
        editLocation.setText(item.getLocationInput());

        dropdown = (Spinner) findViewById(R.id.spinner);
        String[] categories = new String[]{"Miscellaneous", "Books", "Electronics", "Food", "Furniture", "Tickets & Coupons"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dropdown.setAdapter(adapter);
        List<String> categoriesList = Arrays.asList(categories);
        dropdown.setSelection(categoriesList.indexOf(item.getCategory()));

        ImageView editImage = (ImageView) findViewById(R.id.imageView);
        UrlImageViewHelper.setUrlDrawable(editImage, item.getPictureUrl());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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

    public void finishEdit(View view) {
        item.setName(editName.getText().toString());
        item.setDescription(editDescription.getText().toString());
        item.setLocationInput(editLocation.getText().toString());
        item.setCategory(dropdown.getSelectedItem().toString());

        Firebase ref = new Firebase(ReuseApplication.FIREBASE_URL + "/items/" + item.getKey());
        ref.setValue(item);

        finish();
    }

}
