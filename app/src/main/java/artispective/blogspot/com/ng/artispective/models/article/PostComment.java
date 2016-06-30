package artispective.blogspot.com.ng.artispective.models.article;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import artispective.blogspot.com.ng.artispective.models.User;

public class PostComment implements Parcelable {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("date")
    @Expose
    private String date;

    public PostComment() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.comment);
        dest.writeParcelable(this.user, 0);
        dest.writeString(this.date);
    }

    protected PostComment(Parcel in) {
        this.id = in.readString();
        this.comment = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.date = in.readString();
    }

    public static final Parcelable.Creator<PostComment> CREATOR =
            new Parcelable.Creator<PostComment>() {

        public PostComment createFromParcel(Parcel source) {
            return new PostComment(source);
        }

        public PostComment[] newArray(int size) {
            return new PostComment[size];
        }
    };
}
