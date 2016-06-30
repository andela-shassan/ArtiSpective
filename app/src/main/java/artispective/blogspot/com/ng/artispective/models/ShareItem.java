package artispective.blogspot.com.ng.artispective.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ShareItem implements Parcelable {
    private int image;
    private String text;

    public ShareItem(int image, String text) {
        this.image = image;
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.image);
        dest.writeString(this.text);
    }

    protected ShareItem(Parcel in) {
        this.image = in.readInt();
        this.text = in.readString();
    }

    public static final Parcelable.Creator<ShareItem> CREATOR = new Parcelable.Creator<ShareItem>() {
        @Override
        public ShareItem createFromParcel(Parcel source) {
            return new ShareItem(source);
        }

        @Override
        public ShareItem[] newArray(int size) {
            return new ShareItem[size];
        }
    };
}
