package artispective.blogspot.com.ng.artispective.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.models.article.PostComment;
import artispective.blogspot.com.ng.artispective.utils.Helper;

public class CommentAdapter extends ArrayAdapter<PostComment> {
    private ArrayList<PostComment> comments;


    public CommentAdapter(Context context, ArrayList<PostComment> comments) {
        super(context, R.layout.comment_list_item_view, comments);
        this.comments = comments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PostComment postComment = comments.get(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.comment_list_item_view, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String userName = postComment.getUser().getFirstName() +"  "
                + postComment.getUser().getLastName();
        viewHolder.comment_owner.setText(userName);
        viewHolder.comment_body.setText(postComment.getComment());
        viewHolder.comment_time.setText(Helper.formatDate(postComment.getDate()));

        return convertView;
    }

    private class ViewHolder {
        public TextView comment_owner, comment_body, comment_time;

        public ViewHolder(View itemView) {
            comment_owner = (TextView) itemView.findViewById(R.id.comment_owner);
            comment_body = (TextView) itemView.findViewById(R.id.comment_body);
            comment_time = (TextView) itemView.findViewById(R.id.comment_time);

        }
    }

    public void addPostComment(PostComment postComment) {
        comments.add(postComment);
    }

    public void addPostCommentList(List<PostComment> postComments) {
        comments.addAll(postComments);
    }
}
