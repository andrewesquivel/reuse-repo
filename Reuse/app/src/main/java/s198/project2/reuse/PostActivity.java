package s198.project2.reuse;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.cloudinary.Cloudinary;
import com.firebase.client.Firebase;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.filepicker.Filepicker;


public class PostActivity extends Activity {
    private int pic = 1;
    private Uri fileUri;

    public static final String FIREBASE_URL = "https://reuse-app.firebaseio.com";
    private Firebase firebase;

    private final String alphabet = "abcdefghijklmnopqrstuvwxyz";

    private final String FILEPICKER_API_KEY = "AKzhkK4dxSBSbK9eNfY3vz";
    private Cloudinary cloudinary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        firebase = new Firebase(FIREBASE_URL).child("items");

        Map config = new HashMap();
        config.put("cloud_name", "dqm3svvyq");
        config.put("api_key", "778421991389216");
        config.put("api_secret", "U0lUKYDAb0jzXKHiJfc4EGFqQRM");
        cloudinary = new Cloudinary(config);
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
        new postTask().execute();
    }

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
            location.add(42.3598);
            location.add(71.0921);


            // get filepicker url
            String pictureUrl = "http://zapp0.staticworld.net/reviews/graphics/products/uploaded/118242_g3.jpg";

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
            final Map<String, String> options = new HashMap<>();
            options.put("public_id", code);
            try

            {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), fileUri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
                cloudinary.uploader().upload(bs, options);
            } catch (
                    IOException e
                    )

            {
                Log.i("IOException", fileUri.toString());
            }

            ;

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
            Intent intent = new Intent(getApplicationContext(), PostSuccessActivity.class);
            intent.putExtra("code", code);

            startActivity(intent);
            return "";
        }
    }


//    private static final String DEV_KEY = "f52a9e5d40b9930";
//
//    public void post(String path) {
//
//
//        List<NameValuePair> postContent = new ArrayList<NameValuePair>(2);
//        postContent.add(new BasicNameValuePair("key", DEV_KEY));
//        postContent.add(new BasicNameValuePair("image", path));
//
//
//        String url = "http://api.imgur.com/2/upload";
//        HttpClient httpClient = new DefaultHttpClient();
//        HttpContext localContext = new BasicHttpContext();
//        HttpPost httpPost = new HttpPost(url);
//
//        try {
//            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//            for(int index=0; index < postContent.size(); index++) {
//                if(postContent.get(index).getName().equalsIgnoreCase("image")) {
//                    // If the key equals to "image", we use FileBody to transfer the data
//                    entity.addPart(postContent.get(index).getName(), new FileBody(new File (postContent.get(index).getValue())));
//                } else {
//                    // Normal string data
//                    entity.addPart(postContent.get(index).getName(), new StringBody(postContent.get(index).getValue()));
//                }
//            }
//
//            httpPost.setEntity(entity);
//
//            HttpResponse response = httpClient.execute(httpPost, localContext);
//            mImgurResponse = parseResponse (response);
//
//
//            Iterator it = mImgurResponse.entrySet().iterator();
//            while(it.hasNext()){
//                HashMap.Entry pairs = (HashMap.Entry)it.next();
//
//                Log.i("INFO",pairs.getKey().toString());
//                if(pairs.getValue()!=null){
//                    reviewEdit.setText(pairs.getValue().toString());
//
//                    Log.i("INFO",pairs.getValue().toString());
//                }
//            }
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Map<String,String> parseResponse(HttpResponse response) {
//        String xmlResponse = null;
//
//        try {
//            xmlResponse = EntityUtils.toString(response.getEntity());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (xmlResponse == null) return null;
//
//        HashMap<String, String> ret = new HashMap<String, String>();
//        ret.put("error", getXMLElementValue(xmlResponse, "error_msg"));
//        ret.put("delete", getXMLElementValue(xmlResponse, "delete_page"));
//        ret.put("original", getXMLElementValue(xmlResponse, "original_image"));
//
//        return ret;
//    }
//
//    private String getXMLElementValue(String xml, String elementName) {
//        if (xml.indexOf(elementName) >= 0)
//            return xml.substring(xml.indexOf(elementName) + elementName.length() + 1,
//                    xml.lastIndexOf(elementName) - 2);
//        else
//            return null;
//    }
}
