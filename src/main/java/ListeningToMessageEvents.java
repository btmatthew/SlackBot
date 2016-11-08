import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.PresenceChange;
import com.ullink.slack.simpleslackapi.events.SlackChannelJoined;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.listeners.PresenceChangeListener;
import com.ullink.slack.simpleslackapi.listeners.SlackChannelJoinedListener;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Samples showing how to listen to message events
 */
public class ListeningToMessageEvents {
    /**
     * This method shows how to register a listener on a SlackSession
     */

    /**
     * Used for purpose registering a listener which will be called when
     * user posts a message on the channel.
     *
     * It aso be used for purpose finding out who was mentioned in the message,
     * and making an array list out of it.
     *
     * @param session from Slack
     */
    public void registeringAListener(SlackSession session) {

        SlackMessagePostedListener messagePostedListener = new SlackMessagePostedListener() {
            @Override
            public void onEvent(SlackMessagePosted event, SlackSession session) {
                if(!event.getSender().isBot()){
                    SlackMessage slackMessage = new SlackMessage();
                    Date date = new Date(System.currentTimeMillis());
                    Timestamp timestamp = new Timestamp(date.getTime());
                    slackMessage.setTimestamp(timestamp);
                    slackMessage.setChannelID(event.getChannel().getId());
                    slackMessage.setMessage(event.getMessageContent());
                    slackMessage.setUserID(event.getSender().getId());
                    // detect both patterns: <@U12345678> and <@U12345678|username>
                    Pattern mentionsPattern = Pattern.compile("<@([^<>\\|]{9})(\\|)?([^>]*)>");
                    Matcher mentionsMatcher = mentionsPattern.matcher(slackMessage.getMessage());
                    // these lists are used to replace mentions



                    ArrayList<Integer> startIndexes = new ArrayList<>();
                    ArrayList<Integer> endIndexes = new ArrayList<>();
                    ArrayList<String> replaces = new ArrayList<>();
                    while (mentionsMatcher.find()) {
                        startIndexes.add(mentionsMatcher.start());
                        endIndexes.add(mentionsMatcher.end());
                        String slackUsername = mentionsMatcher.group(1);
                        replaces.add(slackUsername);
                    }
                    slackMessage.setReplaces(replaces);

                    DatabaseManager databaseManager = new DatabaseManager();
                    databaseManager.saveMessageInDatabase(slackMessage);
                }

            }

        };
        session.addMessagePostedListener(messagePostedListener);
    }

    /**
     * Used for purpose registering a listener which will be called when
     * user's status changes i.e. AWAY or ONLINE
     * It will also check if the user is already added in the slackUser table,
     * if not the user will be added to that table by collecting his userID from user table,
     * using the email
     *
     * @param session from Slack
     */
    public void registeringLoginListener(final SlackSession session) {
        final DatabaseManager databaseManager = new DatabaseManager();
        final ArrayList<String> databaseSlackUsers = databaseManager.selectAllSlackUsersFromDatabase();
        for(String user : databaseSlackUsers){
            System.out.println(user);
        }

        PresenceChangeListener slackUserChangeListener = new PresenceChangeListener() {
            @Override
            public void onEvent(PresenceChange presenceChange, SlackSession slackSession) {
                session.refetchUsers();
                SlackUser slackUser = session.findUserById(presenceChange.getUserId());
                if (!slackUser.isBot()) {
                    SlackMessage slackMessage = new SlackMessage();
                    slackMessage.setUserID(slackUser.getId());
                    if(!databaseSlackUsers.contains(slackMessage.getUserID())){
                        String userEmail = slackUser.getUserMail();
                        String userNickName = slackUser.getUserName();
                        databaseManager.findUserID(userEmail,slackMessage.getUserID(),userNickName);
                        databaseSlackUsers.add(slackMessage.getUserID());
                    }
                    slackMessage.setEmail(slackUser.getUserMail());
                    Date date = new Date(System.currentTimeMillis());
                    Timestamp timestamp = new Timestamp(date.getTime());
                    slackMessage.setTimestamp(timestamp);
                    slackMessage.setStatus(presenceChange.getPresence().toString());


                    databaseManager.saveStatusInDatabase(slackMessage);
                }

            }
        };
        session.addPresenceChangeListener(slackUserChangeListener);
    }
    /**
     * Used for purpose registering a listener which will be called when
     * bot is added to a channel that it hasn't seen before.
     *
     * @param session from Slack
     */
    public void registeringChannelCreatedListener(final SlackSession session) {

        SlackChannelJoinedListener slackChannelJoinedListener = new SlackChannelJoinedListener() {
            @Override
            public void onEvent(SlackChannelJoined slackChannelJoined, SlackSession slackSession) {
                System.out.println(slackSession.getTeam().getId());
                SlackMessage slackMessage = new SlackMessage();
                slackMessage.setSlackSizeID(slackSession.getTeam().getId());
                slackMessage.setChannelID(slackChannelJoined.getSlackChannel().getId());
                slackMessage.setChannelName(slackChannelJoined.getSlackChannel().getName());

                DatabaseManager databaseManager = new DatabaseManager();
                databaseManager.newChannelCreated(slackMessage);
            }
        };
        session.addChannelJoinedListener(slackChannelJoinedListener);
    }

}