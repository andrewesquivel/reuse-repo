package s198.project2.reuse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ItemActivity extends Activity {
    private Item item;

    private String code;
    private String key = "";
    public static final String FIREBASE_URL = "https://reuse-app.firebaseio.com";
    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);
        Bundle data = getIntent().getExtras();
        item = (Item) data.getParcelable("item");
        TextView tvName = (TextView) findViewById(R.id.name);
        tvName.setText(item.getName());
        TextView tvDescription = (TextView) findViewById(R.id.description);
        tvDescription.setText(item.getDescription());
        code = item.getCode();
        key = item.getKey();
        Log.i("key", "here " + item.getKey());

        ImageView ivItem = (ImageView) findViewById(R.id.itemImage);
        UrlImageViewHelper.setUrlDrawable(ivItem, item.getPictureUrl());

        firebase = new Firebase(FIREBASE_URL).child("items");

        String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (userId.equals(item.getUser())){
            Log.i("user", "this is user's own item");
            EditText claim = (EditText) findViewById(R.id.claimCode);
            claim.setText(code);
            if(item.isClaimed()){
                Button claimButton = (Button) findViewById(R.id.claimButton);
                claimButton.setVisibility(View.GONE);
                TextView claimed = (TextView) findViewById(R.id.claimed);
                claimed.setVisibility(View.VISIBLE);
            }
        }

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewItem(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("type", "single");
        intent.putExtra("item", item);
        Log.i("EXTRAS", intent.getExtras().toString());
        startActivity(intent);
    }

    public void claim(View v) {
        EditText claimCode = (EditText) findViewById(R.id.claimCode);
        String codeInput = claimCode.getText().toString();
        if (codeInput.equals(code)) {
            Firebase ref = new Firebase(FIREBASE_URL + "/items/" + key);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            alertDialogBuilder.setTitle("Claim Item");

            alertDialogBuilder
                    .setMessage("You have successfully claimed the item!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            ref.child("claimed").setValue(true);

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Incorrect Claim Code!", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
