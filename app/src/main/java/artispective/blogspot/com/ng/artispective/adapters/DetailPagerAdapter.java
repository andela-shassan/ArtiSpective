package artispective.blogspot.com.ng.artispective.adapters;

import android.content.Context;
import android.os.Handler;
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
import artispective.blogspot.com.ng.artispective.utils.Helper;

public class DetailPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Event> events;
    private ImageView imageView;
    private int i = 0;

    public DetailPagerAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
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

        final Event event = events.get(position);
        TextView eventTitle, eventContent, eventDate, eventLocation;
        final ImageView eventImage;

        View convertView = layoutInflater.inflate(R.layout.content_detail, container, false);

        eventTitle = (TextView) convertView.findViewById(R.id.detail_event_name);
        eventContent = (TextView) convertView.findViewById(R.id.detail_event_detail);
        eventDate = (TextView) convertView.findViewById(R.id.detail_event_date);
        eventLocation = (TextView) convertView.findViewById(R.id.detail_event_location);
        eventImage = (ImageView) convertView.findViewById(R.id.detail_event_images);
        imageView = eventImage;

        String date = Helper.formatDateTime(event.getDate());

        eventTitle.setText(event.getTitle());
        eventContent.setText(event.getDetails());
        eventLocation.setText(event.getAddress());
        eventDate.setText(date);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                picasso(eventImage, scrollImage(event));
                handler.postDelayed(this, 10000);
            }
        };

        runnable.run();
        container.addView(convertView);

        return convertView;
    }

    private void picasso(ImageView eventImage, String url) {
        Picasso.with(context).load(url).placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image).fit().into(eventImage);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    private String scrollImage(Event event) {
        int imgNo = event.getImages().size();
        String next =  event.getImages().get(i%imgNo);
        i++;
        return next;
    }


}
