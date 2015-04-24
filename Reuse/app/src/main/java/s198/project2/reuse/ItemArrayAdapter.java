package s198.project2.reuse;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rachelwang1994 on 4/23/15.
 */
public class ItemArrayAdapter extends ArrayAdapter<Item> {
    private Activity context;
    private List<Item> items;

    public ItemArrayAdapter(Activity context, List<Item> items) {
        super(context, R.layout.item_row_layout, items);
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
        // set image from url
        return convertView;
    }
}
