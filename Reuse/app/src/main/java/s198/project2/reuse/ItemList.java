package s198.project2.reuse;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
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

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.item_list_view);

        List<Item> items = new ArrayList<>();
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


}
