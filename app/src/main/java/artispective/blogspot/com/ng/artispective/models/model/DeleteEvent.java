package artispective.blogspot.com.ng.artispective.models.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteEvent {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;


    public boolean getStatus() {
        return status;
    }


    public String getMessage() {
        return message;
    }

}