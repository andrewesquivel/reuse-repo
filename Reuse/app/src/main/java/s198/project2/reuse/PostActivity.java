package s198.project2.reuse;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.client.Firebase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.filepicker.Filepicker;


public class PostActivity extends Activity {
    private int pic = 1;
    private Uri fileUri;

    public static final String FIREBASE_URL = "https://reuse-app.firebaseio.com";
    private Firebase firebase;

    private final String alphabet = "abcdefghijklmnopqrstuvwxyz";

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

    private static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;

    public void takePicture (View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String path= Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
        File file = new File(path,"ReuseImage" + pic + ".jpg");
        pic += 1;
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageURI(fileUri);
            }
            else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture
            } else {
            // Image capture failed, advise user
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

        //upload picture to filepicker
        Filepicker.uploadLocalFile(fileUri, getApplicationContext());

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
                code,
                tags,
                false // unclaimed
        );

        // push item to firebase
        Firebase ref = firebase.push();
        item.setKey(ref.getKey());
        ref.setValue(item);

        Log.i("post item ", ref.getKey());
        System.out.println(code);
        Intent intent = new Intent(this, PostSuccessActivity.class);
        intent.putExtra("code", code);
        startActivity(intent);
    }
}
