package s198.project2.reuse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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


public class ItemActivity extends Activity {
    private Item item;

    private String code;
    private String key = "";
    private Firebase firebase;

    private TextView tvName;
    private TextView tvDescription;
    private TextView tvLocation;

    private final int EDIT_ACTIVITY_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);
        Bundle data = getIntent().getExtras();
        item = (Item) data.getParcelable("item");
        tvName = (TextView) findViewById(R.id.name);
        tvName.setText(item.getName());
        tvDescription = (TextView) findViewById(R.id.description);
        tvDescription.setText(item.getDescription());
        code = item.getCode();
        key = item.getKey();
        Log.i("key", "here " + item.getKey());

        tvLocation = (TextView) findViewById(R.id.textView);
        tvLocation.setText(item.getLocationInput());

        getActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView ivItem = (ImageView) findViewById(R.id.itemImage);
        UrlImageViewHelper.setUrlDrawable(ivItem, item.getPictureUrl());

        firebase = new Firebase(ReuseApplication.FIREBASE_URL).child("items");
        // add a firebase listener to update after having edited

        String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (userId.equals(item.getUser())){
            Button claimButton = (Button) findViewById(R.id.claimButton);
            TextView claimLabel = (TextView) findViewById(R.id.textView7);
            Button deleteButton = (Button) findViewById(R.id.deleteButton);
            Button editButton = (Button) findViewById(R.id.editButton);

            Log.i("user", "this is user's own item");
            EditText claim = (EditText) findViewById(R.id.claimCode);
            claim.setVisibility(View.GONE);
            claimButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
            claimLabel.setText("Claim Code: " + code);
            claimLabel.setVisibility(View.VISIBLE);
            claimLabel.setVisibility(View.VISIBLE);

            if(item.isClaimed()){
                claimButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
                TextView claimed = (TextView) findViewById(R.id.claimed);
                claimed.setVisibility(View.VISIBLE);
                claim.setVisibility(View.GONE);
                claimLabel.setVisibility(View.GONE);
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
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
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
            Firebase ref = new Firebase(ReuseApplication.FIREBASE_URL + "/items/" + key);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                item = data.getParcelableExtra("item");
                tvName.setText(item.getName());
                tvDescription.setText(item.getDescription());
                tvLocation.setText(item.getLocationInput());
            }
        }
    }

    public void edit(View view) {
        Intent i = new Intent(this, EditActivity.class);
        i.putExtra("item", item);
        startActivityForResult(i, EDIT_ACTIVITY_CODE);
    }

}
