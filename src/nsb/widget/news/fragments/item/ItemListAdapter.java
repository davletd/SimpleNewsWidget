package nsb.widget.news.fragments.item;

import java.util.List;

import nsb.widget.news.data.SortableAdapter;
import nsb.widget.news.model.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import nsb.widget.news.R;

public class ItemListAdapter extends SortableAdapter<Item>
{
    private int viewResourceId;
    
    private ItemBinder binder;
    
    
    public ItemListAdapter(Context context, List<Item> items)
    {
        super(context, R.layout.item_layout_1, items);
       
        sort();
        
        binder = new ItemBinder();
            
        viewResourceId = R.layout.item_layout_1;
        
        setNotifyOnChange(true);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        
        final Item item = getItem(position);
        if (item != null)
        {
            binder.bind(v, item);
        }
        
        return v;
    }
    
    
    
}
