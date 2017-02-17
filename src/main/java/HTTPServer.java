import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Mateusz on 14/11/2016.
 */
public class HTTPServer implements HttpHandler {


    /***
     * Method used for purpose of receiving HTTP requests to add users to Slack and to add users to Slack groups
     * @param t used to handle http requests
     * @throws IOException
     */
    public void handle(HttpExchange t) throws IOException {
        Keys keys = null;
        try {
            keys = new Keys();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Creating admin Session");
        SlackSession session = SlackSessionFactory.createWebSocketSlackSession(keys.getSlackAdminKey());
        session.connect();

        String response = t.getRequestURI().getPath();
        t.sendResponseHeaders(200, 0);
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
        DatabaseManager db = null;
        db = new DatabaseManager();
        if(response.contains("group")){
            addUsersToGroup(session, db );
        }else if(response.contains("invite")){
            inviteUsersToSlack(session,db);
        }

    }

    /***
     * Method used for purpose of inviting users to Slack
     * @param session used for communicating with slack API
     * @param db used to query database
     */
    private void inviteUsersToSlack(SlackSession session, DatabaseManager db) throws IOException {
        ArrayList<SlackMessage> slackMessageArrayList = db.getUsersToInvite();
        ArrayList<SlackUser> slackUserArrayList = new ArrayList<>(session.getUsers());
        for(SlackMessage slackMessage : slackMessageArrayList){


            boolean userFound = false;
            for(SlackUser slackUser : slackUserArrayList){
                System.out.println("user to invite "+slackMessage.getEmail());
                System.out.println(slackUser.getUserMail());
                if(slackMessage.getEmail().equals(slackUser.getUserMail())){
                    System.out.println("user found " + slackMessage.getEmail());
                    userFound=true;
                    break;
                }
            }
            if(!userFound){
                session.inviteUser(slackMessage.getEmail(),slackMessage.getFirstName(),true);
                System.out.println("Following user was invited : " + slackMessage.getFirstName() + " with email address "+slackMessage.getEmail());
            }
        }
        session.disconnect();
    }

    /***
     * Method used for purpose of adding users to groups of Slack
     * @param session used for communicating with slack API
     * @param db used to query database
     */
    private void addUsersToGroup(SlackSession session, DatabaseManager db) throws IOException {
        ArrayList<SlackMessage> slackMessageArrayList = db.getUserGroup();
        session.refetchUsers();
        ArrayList<SlackChannel> slackChannelArrayList = new ArrayList<>(session.getChannels());
        ArrayList<SlackUser> slackUserArrayList = new ArrayList<>(session.getUsers());

        for(SlackMessage slackMessage : slackMessageArrayList){
            SlackChannel slackChannelTemp=null;
            SlackUser slackUserTemp = null;
            for(SlackChannel slackChannel : slackChannelArrayList){
                if(slackChannel.getId().equals(slackMessage.getChannelID())){
                    slackChannelTemp=slackChannel;
                    break;
                }
            }
            for(SlackUser slackUser : slackUserArrayList){
                if(slackUser.getId().equals(slackMessage.getUserID())){
                    slackUserTemp = slackUser;
                }
            }
            if(slackChannelTemp!=null && slackUserTemp!=null){
                session.inviteToChannel(slackChannelTemp,slackUserTemp);
            }
        }
        session.disconnect();
    }


}
