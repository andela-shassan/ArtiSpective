
package artispective.blogspot.com.ng.artispective.models.model; import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

;

@Generated("org.jsonschema2pojo")
public class BigEvent {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("event")
    @Expose
    private Event event;
    @SerializedName("events")
    @Expose
    private List<Event> events = new ArrayList<Event>();


    /**
     * 
     * @return
     *     The status
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * 
     * @param message
     *     The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 
     * @return
     *     The event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * 
     * @param event
     *     The event
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     *
     * @return
     *     The events
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     *
     * @param events
     *     The events
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

}
