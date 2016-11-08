import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Mateusz on 05/10/2016.
 */
public class SlackMessage {

    private String userName;
    private String message;
    private Timestamp timestamp;
    private String channelName;
    private String status;
    private String channelID;
    private String email;
    private String userID;
    private String slackSizeID;
    private ArrayList<String> replaces = new ArrayList<>();

    public SlackMessage() {

    }

    public ArrayList<String> getReplaces() {
        return replaces;
    }

    public void setReplaces(ArrayList<String> replaces) {
        this.replaces = replaces;
    }


    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSlackSizeID() {
        return slackSizeID;
    }

    public void setSlackSizeID(String slackSizeID) {
        this.slackSizeID = slackSizeID;
    }
}
