import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Mateusz on 05/10/2016.
 */
public class DatabaseManager{
    //  Database credentials
    Keys keys = new Keys();
    private final String DB_URL = keys.databaseURL;
    private final String USER = keys.databaseUser;
    private final String PASS = keys.databasePass;

    /**
     * Used for purpose of saving users message in a database,
     * it also checks if there is a user mentioned in a message and calls savMentionedInDatabase method
     * @param message contains data used for purpose of saving a message
     */
    public void saveMessageInDatabase(SlackMessage message){
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

                String query = "INSERT INTO channelMessages ( slackChannelId,writerSlackUserId,content,timestamp) " +
                        "VALUES (?,?,?,?)";
                PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, message.getChannelID());
                preparedStatement.setString(2, message.getUserID());
                preparedStatement.setString(3,message.getMessage());
                preparedStatement.setTimestamp(4,message.getTimestamp(),Calendar.getInstance(TimeZone.getTimeZone("Europe/London")));
                preparedStatement.executeUpdate();

                ResultSet rs = preparedStatement.getGeneratedKeys();
                rs.next();
                if(message.getReplaces().size()>0){
                    int rowID = rs.getInt(1);
                    saveMentionedInDatabase(rowID,message.getReplaces(),conn);
                }
                rs.close();
                preparedStatement.close();
                stmt.close();
            }
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used for purpose of saving mentioned users in a message in a database
     * @param rowID rowID used a primaryKey for the table
     * @param replaces list of users mentioned in a message
     * @param conn database connection object
     * @throws SQLException
     */
    public void saveMentionedInDatabase(int rowID,ArrayList<String> replaces,Connection conn) throws SQLException {
        for(String userName : replaces){
            String query1 = "INSERT INTO messageMentions (channelMessageId, mentionedUserId) " +
                    "VALUES (?,?)";
            PreparedStatement preparedStatement1 = conn.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
            preparedStatement1.setInt(1, rowID);
            preparedStatement1.setString(2, userName);
            preparedStatement1.executeUpdate();
            preparedStatement1.close();
            conn.close();
        }
    }

    /**
     * used for purpose of saving user's status in a database
     * @param message object containing all the required variables for saving user's status in a table
     */
    public void saveStatusInDatabase(SlackMessage message){
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                String query = "INSERT INTO slackUserStatusLog (slackUserId,email,status,time) " +
                        "VALUES (?,?,?,?)";
                PreparedStatement preparedStatement = conn.prepareStatement(query);

                preparedStatement.setString(1, message.getUserID());
                preparedStatement.setString(2, message.getEmail());
                preparedStatement.setString(3, message.getStatus());
                preparedStatement.setTimestamp(4, message.getTimestamp(), Calendar.getInstance(TimeZone.getTimeZone("Europe/London")));
                preparedStatement.executeUpdate();
                preparedStatement.close();
                stmt.close();
                conn.close();
            }

            } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used for purpose of adding newly created channel into a database
     * @param message containing all the required variables for purpose of saving new channel to the database
     */
    public void newChannelCreated(SlackMessage message) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                String query = "SELECT * FROM slackChannel WHERE slackChannelId = " + "\"" +message.getChannelID()+ "\"";
                ResultSet rs = stmt.executeQuery(query);

                if (!rs.isBeforeFirst()) {
                    rs.close();

                    query = "INSERT INTO slackChannel (slackChannelId,slackSiteId,name) " +
                            "VALUES (?,?,?)";
                    PreparedStatement preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, message.getChannelID());
                    preparedStatement.setString(2, message.getSlackSizeID());
                    preparedStatement.setString(3,message.getChannelName());
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                    stmt.close();
                    conn.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> selectAllSlackUsersFromDatabase(){
        ArrayList<String> slackMessages = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                String query = "SELECT * FROM slackUser";
                ResultSet rs = stmt.executeQuery(query);
                while(rs.next()) {
                    String slackUserID = rs.getString("slackUserId");
                    slackMessages.add(slackUserID);
                }
                stmt.close();
                conn.close();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return slackMessages;
    }

    public void findUserID(String email,String slackID,String slackNickName){
        String userID=null;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                String query = "SELECT * FROM `user` WHERE `email` =" + "\"" +email+ "\"";
                ResultSet rs = stmt.executeQuery(query);
                while(rs.next()) {
                    userID = rs.getString("userId");
                }
                stmt.close();
                query = "INSERT INTO slackUser (slackUserId,userId,userAlias) " +
                        "VALUES (?,?,?)";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, slackID);
                preparedStatement.setString(2, userID);
                preparedStatement.setString(3, slackNickName);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                conn.close();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<SlackMessage> getUserGroup(){
        ArrayList<SlackMessage> slackMessageArrayList = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                String query = "SELECT " +
                        "su.slackUserId,"+
                        "sc.slackChannelId,"+
                        "g.name as 'group name'," +
                        "p.projectId FROM `user` AS u " +
                        "INNER JOIN `slackUser` AS su On `su`.`userId` = `u`.`userId`"+
                        "INNER JOIN `userGroup` AS ug ON `ug`.`userId` = `u`.`userId`" +
                        "INNER JOIN `group` AS g ON `g`.`groupId` = `ug`.`groupId`" +
                        "INNER JOIN `slackChannel` AS sc ON `sc`.`groupId` = `g`.`groupId`"+
                        "INNER JOIN `project` AS p ON `p`.`projectId` = `g`.`projectId`" +
                        "WHERE p.projectId = "+"\"bestFood2016\"";
                ResultSet rs = stmt.executeQuery(query);
                while(rs.next()) {
                    SlackMessage slackMessage = new SlackMessage();
                    slackMessage.setUserID(rs.getString("slackUserId"));
                    slackMessage.setChannelID(rs.getString("slackChannelId"));
                    slackMessageArrayList.add(slackMessage);
                }
                rs.close();
                stmt.close();
                conn.close();

            }

            }catch (SQLException e) {
            e.printStackTrace();
        }


        return slackMessageArrayList;
    }
    public ArrayList<SlackMessage> getUsersToInvite() {
        ArrayList<SlackMessage> slackMessageArrayList = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

                String query = "SELECT `user`.`userId`,`user`.`firstName`,`user`.`email`,`project`.`projectId`" +
                        "FROM `user`" +
                        "INNER JOIN `userGroup` ON `user`.`userId` = `userGroup`.`userId`" +
                        "INNER JOIN `group` ON `userGroup`.`groupId` = `group`.`groupId`" +
                        "INNER JOIN `project` ON `group`.`projectId` = `project`.`projectId`"+
                        "WHERE project.projectId = "+"\"bestFood2016\"";
                ResultSet rs = stmt.executeQuery(query);
                while(rs.next()) {
                    SlackMessage slackMessage = new SlackMessage();
                    slackMessage.setEmail(rs.getString("email"));
                    slackMessage.setFirstName(rs.getString("firstName"));
                    slackMessageArrayList.add(slackMessage);
                }
                rs.close();
                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return slackMessageArrayList;
    }

}
