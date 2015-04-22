package s198.project2.reuse;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String FIREBASE_URL = "https://rswang.firebaseio.com";
    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebase = new Firebase(FIREBASE_URL).child("items");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void postScreen(View v) {
        Intent intent = new Intent(this, PostActivity.class);
        startActivity(intent);
    }

    public void viewItems(View v) {
        Intent intent = new Intent(this, ItemListActivity.class);
        startActivity(intent);
    }

    public void createItem(View v) {
        // create dummy item
        List<Double> location = new ArrayList<>();
        location.add(42.3598);
        location.add(71.0921);

        Item item = new Item(
                "myUsername",
                "Laptop",
                "This is an old Macbook air.",
                location,
                "http://zapp0.staticworld.net/reviews/graphics/products/uploaded/118242_g3.jpg",
                "A8dZ",
                new ArrayList<String>()
        );

        // push item to firebase
        firebase.push().setValue(item);
    }
}
