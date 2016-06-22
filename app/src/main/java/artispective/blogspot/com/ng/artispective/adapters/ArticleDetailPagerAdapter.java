package artispective.blogspot.com.ng.artispective.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.interfaces.CommentClickListener;
import artispective.blogspot.com.ng.artispective.models.article.Post;
import artispective.blogspot.com.ng.artispective.models.article.PostComment;
import artispective.blogspot.com.ng.artispective.utils.Helper;

public class ArticleDetailPagerAdapter extends PagerAdapter implements View.OnClickListener,
        ViewPager.OnPageChangeListener {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Post> posts;
    private ImageView imageView;
    private CommentClickListener commentClickListener;
    private int currentPosition;
    private CommentAdapter commentAdapter;

    public ArticleDetailPagerAdapter(Context context, CommentClickListener commentClickListener,
                                     ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
        layoutInflater = LayoutInflater.from(context);
        this.commentClickListener = commentClickListener;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Post post = posts.get(position);
        TextView articleTitle, articleDetails, articleDate, articleComment, addComment;
        ImageView articleImage, articleCommentImage;
        ListView listView;
        android.support.v4.widget.NestedScrollView view;

        View convertView = layoutInflater.inflate(R.layout.content_detail_article, container, false);

        articleTitle = (TextView) convertView.findViewById(R.id.post_title);
        articleDetails = (TextView) convertView.findViewById(R.id.post_details);
        articleComment = (TextView) convertView.findViewById(R.id.article_comment_counter);
        articleDate = (TextView) convertView.findViewById(R.id.article_posted_date);
        articleImage = (ImageView) convertView.findViewById(R.id.post_image);
        addComment = (TextView) convertView.findViewById(R.id.add_comment_article);
        articleCommentImage = (ImageView) convertView.findViewById(R.id.article_comment_image);
        listView = (ListView) convertView.findViewById(R.id.comment_list_view);
//        view = (NestedScrollView) convertView.findViewById(R.id.header_view);
//        listView.addHeaderView(view);
        articleCommentImage.setOnClickListener(this);
        addComment.setOnClickListener(this);
        articleComment.setOnClickListener(this);
        imageView = articleImage;
        commentAdapter = new CommentAdapter(context, (ArrayList<PostComment>) post.getComments());
        listView.setAdapter(commentAdapter);
        listView.setDividerHeight(5);
        updateListViewHeight(listView);
        commentAdapter.notifyDataSetChanged();
        String date = Helper.formatDate(post.getDatePosted());

        articleTitle.setText(post.getHeading());
        articleDetails.setText(post.getBody());
        articleComment.setText(post.getComments().size()+"");
        articleDate.setText(date);

        Picasso.with(context).load(post.getImage()).placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image).fit().into(articleImage);

        container.addView(convertView);
        currentPosition = position - 1;

        return convertView;
    }

//    @NonNull
//    private String formatDate(String dated) {
//        String[] dateArray = dated.split("-");
//        return dateArray[2].substring(0, 2) + "/" + dateArray[1] + "/" + dateArray[0];
//    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.article_comment_image:
            case R.id.article_comment_counter:
            case R.id.add_comment_article:
                commentClickListener.onCommentClick(currentPosition);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentPosition = position - 1;
    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position -1;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static void updateListViewHeight(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            return;
        }
        //get listview height
        int totalHeight = 0;
        int adapterCount = myListAdapter.getCount();
        for (int size = 0; size < adapterCount ; size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
//            listItem.getHeight();
            totalHeight += (listItem.getMeasuredHeight() * 5);
        }
        //Change Height of ListView
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (adapterCount - 1));
        myListView.setLayoutParams(params);
    }


}
