package artispective.blogspot.com.ng.artispective.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.models.model.Event;
import artispective.blogspot.com.ng.artispective.utils.Constants;

public class DetailPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Event> events;
    private int currentPosition;

    public DetailPagerAdapter(Context context, ArrayList<Event> events, int currentPosition) {
        this.context = context;
        this.events = events;
        this.currentPosition = currentPosition;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Event event = events.get(position);
        TextView eventTitle, eventContent, eventDate, eventLocation;
        ImageView eventImage;

        View convertView = layoutInflater.inflate(R.layout.content_detail, container, false);

        eventTitle = (TextView) convertView.findViewById(R.id.detail_event_name);
        eventContent = (TextView) convertView.findViewById(R.id.detail_event_detail);
        eventDate = (TextView) convertView.findViewById(R.id.detail_event_date);
        eventLocation = (TextView) convertView.findViewById(R.id.detail_event_location);
        eventImage = (ImageView) convertView.findViewById(R.id.detail_event_images);


        String[] dateArray = event.getDate().split("-");
        String date = dateArray[2].substring(0, 2) + "/" + dateArray[1] + "/" + dateArray[0];

        eventTitle.setText(event.getTitle());
        eventContent.setText(event.getDetails());
        eventLocation.setText(event.getAddress());
        eventDate.setText(date);

        String image = "";
        if (event.getImages().size() > 0) {
            image += event.getImages().get(0);
        } else {
            image += Constants.DEFAULT_IMAGE;
        }

        Picasso.with(context).load(image)
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .fit()
                .into(eventImage);

        container.addView(convertView);

        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


}
