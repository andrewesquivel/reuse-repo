package s198.project2.reuse;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rachelwang1994 on 4/23/15.
 */
public class ItemList extends ListActivity {
    public List<Item> items;
    public Activity activity;

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
        ArrayAdapter<Item> adapter = new ItemArrayAdapter(this, items, null, userId);
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
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_item, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


}
