package s198.project2.reuse;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;


public class PostActivity extends Activity {

    public static final String FIREBASE_URL = "https://reuse-app.firebaseio.com";
    private Firebase firebase;

    private String alphabet = "abcdefghijklmnopqrstuvwxyz";

    private final String FILEPICKER_API_KEY = "AKzhkK4dxSBSbK9eNfY3vz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        firebase = new Firebase(FIREBASE_URL).child("items");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
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

    private static int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1;

    public void takePicture (View v) {
        Intent camera_intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera_intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(thumbnail);
            }
        }
    }

    public void postItem (View v) {
        // get username
        String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // get name
        EditText etName = (EditText) findViewById(R.id.nameInput);
        String name = etName.getText().toString();

        // get description
        EditText etDescription = (EditText) findViewById(R.id.descriptionInput);
        String description = etDescription.getText().toString();

        // get location
        List<Double> location = new ArrayList<>();
        location.add(42.3598);
        location.add(71.0921);

        // get filepicker url
        String pictureUrl = "http://zapp0.staticworld.net/reviews/graphics/products/uploaded/118242_g3.jpg";

        // generate random code
        String code = "";
        int rand;
        for (int i = 0; i < 4; i++) {
            rand = (int) Math.round(25*Math.random());
            code += alphabet.substring(rand, rand+1);
        }

        // get tags
        List<String> tags = new ArrayList<String>();

        Item item = new Item(
                userId,
                name,
                description,
                location,
                pictureUrl,
                "A8dZ",
                tags,
                false // unclaimed
        );

        // push item to firebase
        firebase.push().setValue(item);
        Intent intent = new Intent(this, PostSuccessActivity.class);
        intent.putExtra("code", code);
        startActivity(intent);
    }
}
