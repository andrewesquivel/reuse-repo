package s198.project2.reuse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.firebase.client.Firebase;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.util.Arrays;
import java.util.List;


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

        getActionBar().setDisplayHomeAsUpEnabled(true);


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
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
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

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("item", item);
        setResult(RESULT_OK, data);
        super.finish();
    }

}
