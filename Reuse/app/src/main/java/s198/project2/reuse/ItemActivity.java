package s198.project2.reuse;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;


public class ItemActivity extends Activity {

    private String code;
    private String key = "";
    public static final String FIREBASE_URL = "https://reuse-app.firebaseio.com";
    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);
        Bundle data = getIntent().getExtras();
        Item item = (Item) data.getParcelable("item");
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

    public void claim(View v) {
        EditText claimCode = (EditText) findViewById(R.id.claimCode);
        String codeInput = claimCode.getText().toString();
        if (codeInput.equals(code)) {
            Firebase ref = new Firebase(FIREBASE_URL + "/items/" + key);
            //Toast toast = Toast.makeText(getApplicationContext(), key, Toast.LENGTH_LONG);
            //toast.show();
            //ref.removeValue();
            
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "Incorrect Claim Code!", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
