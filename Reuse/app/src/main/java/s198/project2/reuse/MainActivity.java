package s198.project2.reuse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void postScreen(View v) {
        Intent intent = new Intent(this, PostActivity.class);
        startActivity(intent);
    }

    public void viewItems(View v) {
        Intent intent = new Intent(this, ItemList.class);
        startActivity(intent);
    }
}
