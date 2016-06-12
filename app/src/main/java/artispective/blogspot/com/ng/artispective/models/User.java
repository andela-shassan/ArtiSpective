package artispective.blogspot.com.ng.artispective.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nobest on 13/05/2016.
 */
public class User implements Parcelable {
    @SerializedName("_id")
    @Expose
    private String _id;
    @SerializedName("emailAddress")
    @Expose
    private String emailAddress;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("craft")
    @Expose
    private String craft;
    @SerializedName("isAdmin")
    @Expose
    private boolean isAdmin;
    @SerializedName("interests")
    @Expose
    private List<Object> interests = new ArrayList<Object>();
    @SerializedName("favouriteEvents")
    @Expose
    private List<Object> favouriteEvents = new ArrayList<Object>();
    @SerializedName("postedEvents")
    @Expose
    private List<Object> postedEvents = new ArrayList<Object>();

    public User() {}

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCraft() {
        return craft;
    }

    public void setCraft(String craft) {
        this.craft = craft;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Object> getInterests() {
        return interests;
    }

    public void setInterests(List<Object> interests) {
        this.interests = interests;
    }

    public List<Object> getFavouriteEvents() {
        return favouriteEvents;
    }

    public void setFavouriteEvents(List<Object> favouriteEvents) {
        this.favouriteEvents = favouriteEvents;
    }

    public List<Object> getPostedEvents() {
        return postedEvents;
    }

    public void setPostedEvents(List<Object> postedEvents) {
        this.postedEvents = postedEvents;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.emailAddress);
        dest.writeString(this.password);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.craft);
        dest.writeByte(this.isAdmin ? (byte) 1 : (byte) 0);
        dest.writeList(this.interests);
        dest.writeList(this.favouriteEvents);
        dest.writeList(this.postedEvents);
    }

    protected User(Parcel in) {
        this._id = in.readString();
        this.emailAddress = in.readString();
        this.password = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.phoneNumber = in.readString();
        this.craft = in.readString();
        this.isAdmin = in.readByte() != 0;
        this.interests = new ArrayList<Object>();
        in.readList(this.interests, Object.class.getClassLoader());
        this.favouriteEvents = new ArrayList<Object>();
        in.readList(this.favouriteEvents, Object.class.getClassLoader());
        this.postedEvents = new ArrayList<Object>();
        in.readList(this.postedEvents, Object.class.getClassLoader());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
