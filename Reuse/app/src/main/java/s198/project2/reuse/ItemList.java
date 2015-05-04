package s198.project2.reuse;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rachelwang1994 on 4/23/15.
 */
public class ItemList extends ListActivity {
    public List<Item> items;
    public Activity activity;
    private ArrayAdapter<Item> adapter;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.item_list_view);
        activity = this;

        Spinner dropdown = (Spinner) findViewById(R.id.spinner2);
        String[] categories = new String[]{"All", "Books", "Electronics", "Food", "Furniture", "Tickets & Coupons", "Miscellaneous"};
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dropdown.setAdapter(spinAdapter);


        items = new ArrayList<>();
        final String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        adapter = new ItemArrayAdapter(this, items, null, userId);
        final ListView listView = (ListView) findViewById(android.R.id.list);
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

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                String category = (String) parentView.getItemAtPosition(position);
                if (!category.equals("All")){
                    items = new ArrayList<Item>();

                    listView.setAdapter(new ItemArrayAdapter(activity, items, category, userId));
                }
                else {
                    listView.setAdapter(new ItemArrayAdapter(activity, items, null, userId));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

    }
    public void viewItems(View view){
        if(items.size() > 0) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("type", "multi");
            intent.putExtra("items", (java.io.Serializable) items);
            Log.i("EXTRAS", intent.getExtras().toString());
            startActivity(intent);
        }else{
            Context context = getApplicationContext();
            CharSequence text = "No Items To Display";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }



}
