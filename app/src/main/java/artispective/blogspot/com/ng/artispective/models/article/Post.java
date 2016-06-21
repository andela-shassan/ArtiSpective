
package artispective.blogspot.com.ng.artispective.models.article;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Post implements Parcelable {

    @SerializedName("imageId")
    @Expose
    private String imageId;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("heading")
    @Expose
    private String heading;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("comments")
    @Expose
    private List<PostComment> comments = new ArrayList<>();
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("datePosted")
    @Expose
    private String datePosted;

    public Post() {}

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PostComment> getComments() {
        return comments;
    }

    public void setComments(List<PostComment> comments) {
        this.comments = comments;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageId);
        dest.writeString(this.image);
        dest.writeString(this.heading);
        dest.writeString(this.body);
        dest.writeString(this.id);
        dest.writeList(this.comments);
        dest.writeString(this.source);
        dest.writeString(this.datePosted);
    }

    protected Post(Parcel in) {
        this.imageId = in.readString();
        this.image = in.readString();
        this.heading = in.readString();
        this.body = in.readString();
        this.id = in.readString();
        this.comments = new ArrayList<PostComment>();
        in.readList(this.comments, List.class.getClassLoader());
        this.source = in.readString();
        this.datePosted = in.readString();
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
