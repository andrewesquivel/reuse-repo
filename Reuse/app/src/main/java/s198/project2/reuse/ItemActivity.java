package s198.project2.reuse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;


public class ItemActivity extends Activity {
    private Item item;

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

        ImageView ivItem = (ImageView) findViewById(R.id.itemImage);
        UrlImageViewHelper.setUrlDrawable(ivItem, item.getPictureUrl());

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
        startActivity(intent);
    }
}
