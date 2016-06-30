package artispective.blogspot.com.ng.artispective.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.models.ShareItem;

public class ShareAdapter extends BaseAdapter {
    private ArrayList<ShareItem> shares;
    private LayoutInflater inflater;

    public ShareAdapter(Context context, ArrayList<ShareItem> shares) {
        this.shares = shares;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return shares.size();
    }

    @Override
    public Object getItem(int position) {
        return shares.get(position);
    }

    @Override
    public long getItemId(int position) {
        ShareItem item = shares.get(position);
        return item.getImage();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShareItem item = shares.get(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.share_items_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(item.getText());
        viewHolder.imageView.setImageResource(item.getImage());

        return convertView;
    }

    class ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.share_image);
            textView = (TextView) view.findViewById(R.id.share_text);
        }
    }
}
