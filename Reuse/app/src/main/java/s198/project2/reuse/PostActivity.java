package s198.project2.reuse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PostActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private int pic = 1;
    private Uri fileUri;

    private Firebase firebase;

    private final String alphabet = "abcdefghijklmnopqrstuvwxyz";

    private Cloudinary cloudinary;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private BreakIterator mLatitudeText;
    private BreakIterator mLongitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        firebase = new Firebase(ReuseApplication.FIREBASE_URL).child("items");

        getActionBar().setDisplayHomeAsUpEnabled(true);

        ((Button) findViewById(R.id.button2)).setEnabled(true);
        Map config = new HashMap();
        config.put("cloud_name", "dqm3svvyq");
        config.put("api_key", "778421991389216");
        config.put("api_secret", "U0lUKYDAb0jzXKHiJfc4EGFqQRM");
        cloudinary = new Cloudinary(config);

        Spinner dropdown = (Spinner) findViewById(R.id.spinner);
        String[] categories = new String[]{"Miscellaneous", "Books", "Electronics", "Food", "Furniture", "Tickets & Coupons"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dropdown.setAdapter(adapter);
        buildGoogleApiClient();
        mGoogleApiClient.connect();

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

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
            case R.id.action_settings:
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
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), fileUri);
                    int h = bitmap.getHeight();
                    int w = bitmap.getWidth();
                    if (h > w){
                        double ratio = 400/(double)h;
                        h *= ratio;
                        w *= ratio;
                    }
                    else {
                        double ratio = 400/(double)w;
                        h *= ratio;
                        w *= ratio;
                    }
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, w, h, true);
                    resized = imageOrientationValidator(resized, fileUri.getPath());
                    imageView.setImageBitmap(resized);
                } catch (IOException e){ };

            }
            else if (resultCode == RESULT_CANCELED) {
                fileUri = null;
            } else {
                fileUri = null;
            }
        }
    }

    public void postItem (View v) {
        if (((EditText) findViewById(R.id.nameInput)).getText().toString().equals("")){
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter an item name.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (((EditText) findViewById(R.id.locationInput)).getText().toString().equals("")){
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter a location.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        ((Button) findViewById(R.id.button2)).setEnabled(false);
        new postTask().execute();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    class postTask extends AsyncTask<String,String,String> {

        protected String doInBackground(String... params) {
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
            try {
                location.add(mLastLocation.getLatitude());
                location.add(mLastLocation.getLongitude());
            }catch(Exception e){
                location.add(42.3598);
                location.add(-71.0921);
            }
            // get locationInput
            EditText etLocation = (EditText) findViewById(R.id.locationInput);
            String locationInput = etLocation.getText().toString();

            String pictureUrl = "";

            // generate random code
            String code = "";
            int rand;
            for (
                    int i = 0;
                    i < 4; i++)

            {
                rand = (int) Math.round(25 * Math.random());
                code += alphabet.substring(rand, rand + 1);
            }
            //upload picture to cloudinary
            if (fileUri != null) {
                final Map<String, String> options = new HashMap<>();
                options.put("public_id", code);
                try

                {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), fileUri);
                    int h = bitmap.getHeight();
                    int w = bitmap.getWidth();
                    if (h > w){
                        double ratio = 400/(double)h;
                        h *= ratio;
                        w *= ratio;
                    }
                    else {
                        double ratio = 400/(double)w;
                        h *= ratio;
                        w *= ratio;
                    }
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, w, h, true);
                    resized = imageOrientationValidator(resized, fileUri.getPath());
                    resized.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

                    Map<String, String> uploadResult = cloudinary.uploader().upload(bs, options);
                    pictureUrl = uploadResult.get("url");
                } catch (IOException e) {
                    Log.i("IOException", fileUri.toString());
                };
            }
            // get tags
            Spinner spCategory = (Spinner) findViewById(R.id.spinner);
            String category = spCategory.getSelectedItem().toString();

            Item item = new Item(
                    userId,
                    name,
                    description,
                    location,
                    locationInput,
                    pictureUrl,
                    code,
                    category,
                    false // unclaimed
            );

            // push item to firebase
            Firebase ref = firebase.push();
            item.setKey(ref.getKey());
            ref.setValue(item);

            Intent intent = new Intent(getApplicationContext(), PostSuccessActivity.class);
            intent.putExtra("code", code);

            startActivity(intent);
            finish();
            return "";
        }
    }

    private Bitmap imageOrientationValidator(Bitmap bitmap, String path) {

        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {

        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }
}
