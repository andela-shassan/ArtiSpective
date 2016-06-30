package artispective.blogspot.com.ng.artispective.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.models.model.Event;
import artispective.blogspot.com.ng.artispective.utils.Constants;
import artispective.blogspot.com.ng.artispective.utils.Helper;

public class EventListAdapter extends ArrayAdapter<Event> {

    public EventListAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.event_list_layout, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.event_list_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.exhibition_title.setText(event.getTitle());
        viewHolder.exhibition_content.setText(event.getDetails());
        String date = Helper.formatDateTime(event.getDate());
        viewHolder.exhibition_date.setText(date);
        viewHolder.exhibition_location.setText(event.getAddress());

        String img;
        img = (event.getImages().size() > 0) ? event.getImages().get(0) : Constants.DEFAULT_IMAGE;

        Picasso.with(getContext()).load(img)
                .resize(100, 100)
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .centerCrop()
                .into(viewHolder.exhibition_image);

        return convertView;

    }

    private class ViewHolder {
        public TextView exhibition_title, exhibition_content, exhibition_date, exhibition_location;
        public ImageView exhibition_image;

        public ViewHolder(View itemView) {
            exhibition_title = (TextView) itemView.findViewById(R.id.exhibition_title);
            exhibition_content = (TextView) itemView.findViewById(R.id.exhibition_content);
            exhibition_date = (TextView) itemView.findViewById(R.id.exhibition_date);
            exhibition_location = (TextView) itemView.findViewById(R.id.exhibition_location);
            exhibition_image = (ImageView) itemView.findViewById(R.id.exhibition_image);

        }
    }
}
