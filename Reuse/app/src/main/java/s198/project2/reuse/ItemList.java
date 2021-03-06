package s198.project2.reuse;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private String category = null;
    private ArrayAdapter<Item> adapter;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.item_list_view);
        activity = this;

        final Spinner dropdown = (Spinner) findViewById(R.id.spinner2);
        String[] categories = new String[]{"All", "Books", "Electronics", "Food", "Furniture", "Tickets & Coupons", "Miscellaneous"};

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dropdown.setAdapter(spinAdapter);


        items = new ArrayList<>();
        final String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        //adapter = new ItemArrayAdapter(this, items, null, null);

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
                category = (String) parentView.getItemAtPosition(position);
                if (!category.equals("All")){
                    items = new ArrayList<Item>();
                    listView.setAdapter(new ItemArrayAdapter(activity, items, category, null));

                }
                else {
                    listView.setAdapter(new ItemArrayAdapter(activity, items, null, null));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        final Button button4 = (Button) findViewById(R.id.button4);
        final Button button5 = (Button) findViewById(R.id.button5);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                items = new ArrayList<Item>();
                listView.setAdapter(new ItemArrayAdapter(activity, items, null, null));
                dropdown.setVisibility(View.VISIBLE);
                button4.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                button5.setBackgroundColor(getResources().getColor(R.color.green));
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                items = new ArrayList<Item>();
                listView.setAdapter(new ItemArrayAdapter(activity, items, null, userId));
                Spinner spin = (Spinner) findViewById(R.id.spinner2);
                spin.setVisibility(View.GONE);
                button5.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                button4.setBackgroundColor(getResources().getColor(R.color.green));
            }
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                  finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
