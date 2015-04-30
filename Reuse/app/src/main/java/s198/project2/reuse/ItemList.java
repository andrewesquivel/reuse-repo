package s198.project2.reuse;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rachelwang1994 on 4/23/15.
 */
public class ItemList extends ListActivity {
    public List<Item> items;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.item_list_view);

        items = new ArrayList<>();
        ArrayAdapter<Item> adapter = new ItemArrayAdapter(this,
                items);
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Item item = (Item) parent.getItemAtPosition(position);
                Intent i = new Intent(ItemList.this, ItemActivity.class);
                i.putExtra("item", item);
                startActivity(i);
            }

        });
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
        } else if (id == R.id.action_map){
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("type", "multi");
            intent.putExtra("items", (java.io.Serializable) items);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


}
