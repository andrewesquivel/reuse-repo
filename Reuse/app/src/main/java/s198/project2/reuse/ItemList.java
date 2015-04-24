package s198.project2.reuse;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by rachelwang1994 on 4/23/15.
 */
public class ItemList extends ListActivity {

    public static final String FIREBASE_URL = "https://rswang.firebaseio.com";
    private Firebase firebase;
    private List<Item> items;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.item_list_view);
        firebase = new Firebase(FIREBASE_URL).child("items");

        List<Item> items = getItems();
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

    public List<Item> getItems() {
        final List<Item> items = new ArrayList<Item>();
        List<Double> location = new ArrayList<>();
        location.add(42.3598);
        location.add(71.0921);

        items.add(new Item(
                "myUsername",
                "Laptop",
                "This is an old Macbook air.",
                location,
                "http://zapp0.staticworld.net/reviews/graphics/products/uploaded/118242_g3.jpg",
                "A8dZ",
                new ArrayList<String>(),
                false
        ));

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot snapshotItem: snapshot.getChildren()) {
                    Map<String, Object> itemMap = (Map<String, Object>) snapshotItem.getValue();
                    Item item = new Item(itemMap);
                    items.add(item);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        return items;
    }
}
