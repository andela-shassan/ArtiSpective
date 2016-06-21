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
import artispective.blogspot.com.ng.artispective.models.article.Post;

/**
 * Created by Nobest on 20/06/2016.
 */
public class ArticleListAdapter extends ArrayAdapter<Post> {
    private ArrayList<Post> posts;

    public ArticleListAdapter(Context context, ArrayList<Post> posts) {
        super(context, R.layout.article_list_layout, posts);
        this.posts = posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post post = posts.get(position);
        String[] dateArray = post.getDatePosted().split("-");
        String date = dateArray[2].substring(0, 2) + "/" + dateArray[1] + "/" + dateArray[0];
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.article_list_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.article_title.setText(post.getHeading());
        viewHolder.article_body.setText(post.getBody());
        viewHolder.article_date.setText(date);
        viewHolder.article_comment.setText(post.getComments().size()+"");

        Picasso.with(getContext()).load(post.getImage())
                .resize(100, 100)
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .centerCrop()
                .into(viewHolder.article_image);

        return convertView;
    }

    private class ViewHolder {
        public TextView article_title, article_body, article_date, article_comment;
        public ImageView article_image;

        public ViewHolder(View itemView) {
            article_title = (TextView) itemView.findViewById(R.id.article_title);
            article_body = (TextView) itemView.findViewById(R.id.article_details);
            article_date = (TextView) itemView.findViewById(R.id.article_posted_date);
            article_comment = (TextView) itemView.findViewById(R.id.article_comment_counter);
            article_image = (ImageView) itemView.findViewById(R.id.article_image);

        }
    }
}
