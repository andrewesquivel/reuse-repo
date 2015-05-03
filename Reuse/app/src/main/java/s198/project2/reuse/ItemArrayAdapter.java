package s198.project2.reuse;

import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rachelwang1994 on 4/23/15.
 */
public class ItemArrayAdapter extends ArrayAdapter<Item> {
    private Activity context;
    private List<Item> items;
    public static final String FIREBASE_URL = "https://reuse-app.firebaseio.com";
    private Firebase firebase;
    private int layout;
    private LayoutInflater mInflater;
    private Map<String, Item> itemKeys;
    private ChildEventListener firebaseListener;

    public ItemArrayAdapter(Activity context, final List<Item> items, final String category, final String userId) {
        super(context, R.layout.item_row_layout, items);
        firebase = new Firebase(FIREBASE_URL).child("items");

        this.items = new ArrayList<>();
        this.itemKeys = new HashMap<>();

        firebaseListener = this.firebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Map<String, Object> itemMap = (Map<String, Object>) dataSnapshot.getValue();
                Item item = new Item(itemMap);
                if ((item.getCategory().equals(category) || category == null) && (!item.isClaimed() || item.getUser().equals(userId))) {
                    itemKeys.put(dataSnapshot.getKey(), item);

                    // Insert into the correct location, based on previousChildName
                    if (previousChildName == null) {
                        items.add(0, item);
                    } else {
                        Item previousItem = itemKeys.get(previousChildName);
                        int previousIndex = items.indexOf(previousItem);
                        int nextIndex = previousIndex + 1;
                        if (nextIndex == items.size()) {
                            items.add(item);
                        } else {
                            items.add(nextIndex, item);
                        }
                    }

                    notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                // One of the mModels changed. Replace it in our list and name mapping
                String modelName = dataSnapshot.getKey();
                Item oldItem = itemKeys.get(modelName);
                Map<String, Object> itemMap = (Map<String, Object>) dataSnapshot.getValue();
                Item newItem = new Item(itemMap);
                int index = items.indexOf(oldItem);

                items.set(index, newItem);
                itemKeys.put(modelName, newItem);

                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // A model was removed from the list. Remove it from our list and the name mapping
                String modelName = dataSnapshot.getKey();
                if (itemKeys.containsKey(modelName)) {
                    Item oldItem = itemKeys.get(modelName);
                    items.remove(oldItem);
                    itemKeys.remove(modelName);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                // A model changed position in the list. Update our list accordingly
                String modelName = dataSnapshot.getKey();
                Item oldItem = itemKeys.get(modelName);

                Map<String, Object> itemMap = (Map<String, Object>) dataSnapshot.getValue();
                Item newItem = new Item(itemMap);
                int index = items.indexOf(oldItem);
                items.remove(index);
                if (previousChildName == null) {
                    items.add(0, newItem);
                } else {
                    Item previousModel = itemKeys.get(previousChildName);
                    int previousIndex = items.indexOf(previousModel);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == items.size()) {
                        items.add(newItem);
                    } else {
                        items.add(nextIndex, newItem);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }

        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Item item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_row_layout, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.description);
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.icon);

        tvName.setText(item.getName());
        tvDescription.setText(item.getDescription());
        UrlImageViewHelper.setUrlDrawable(ivIcon, item.getPictureUrl());

        return convertView;

    }

    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the mModels
        firebase.removeEventListener(firebaseListener);
        items.clear();
        itemKeys.clear();
    }
    public List<Item> getItems(){
        return items;
    }

}
