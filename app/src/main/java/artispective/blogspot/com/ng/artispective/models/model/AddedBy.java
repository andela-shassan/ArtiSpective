
package artispective.blogspot.com.ng.artispective.models.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class AddedBy implements Parcelable {

    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("emailAddress")
    @Expose
    private String emailAddress;
    @SerializedName("interests")
    @Expose
    private List<Object> interests = new ArrayList<Object>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<Object> getInterests() {
        return interests;
    }

    public void setInterests(List<Object> interests) {
        this.interests = interests;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.emailAddress);
        dest.writeList(this.interests);
    }

    public AddedBy() {
    }

    protected AddedBy(Parcel in) {
        this.id = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.emailAddress = in.readString();
        this.interests = new ArrayList<Object>();
        in.readList(this.interests, Object.class.getClassLoader());
    }

    public static final Parcelable.Creator<AddedBy> CREATOR = new Parcelable.Creator<AddedBy>() {
        @Override
        public AddedBy createFromParcel(Parcel source) {
            return new AddedBy(source);
        }

        @Override
        public AddedBy[] newArray(int size) {
            return new AddedBy[size];
        }
    };
}
