
package artispective.blogspot.com.ng.artispective.models.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Event implements Parcelable {

    public Event() {}

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("images")
    @Expose
    private List<String> images = new ArrayList<>();
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("dateCreated")
    @Expose
    private String dateCreated;
    @SerializedName("userId")
    @Expose
    private String userId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.details);
        dest.writeString(this.date);
        dest.writeString(this.id);
        dest.writeStringList(this.images);
        dest.writeString(this.address);
        dest.writeString(this.dateCreated);
        dest.writeString(this.userId);
    }

    protected Event(Parcel in) {
        this.title = in.readString();
        this.details = in.readString();
        this.date = in.readString();
        this.id = in.readString();
        this.images = in.createStringArrayList();
        this.address = in.readString();
        this.dateCreated = in.readString();
        this.userId = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}

