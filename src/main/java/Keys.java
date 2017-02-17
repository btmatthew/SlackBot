


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Mateusz on 07/11/2016.
 */
public class Keys {

    private static final String slackKeyID = "slackKey";
    private static final String slackAdminKeyID = "slackAdminKey";
    private static final String databaseURLID = "databaseURL";
    private static final String databaseUserID = "databaseUser";
    private static final String databasePassID = "databasePass";
    private static final String projectNameID = "projectName";

    private String slackKey = "";
    private String slackAdminKey = "";
    private String databaseURL = "";
    private String databaseUser = "";
    private String databasePass = "";
    private String projectName = "";

    Keys() throws ParseException {
        loadConfig();
    }

    String getSlackKey() {
        return slackKey;
    }

    String getSlackAdminKey() {
        return slackAdminKey;
    }

    String getDatabaseURL() {
        return databaseURL;
    }

    String getDatabaseUser() {
        return databaseUser;
    }

    String getDatabasePass() {
        return databasePass;
    }

    String getProjectName() {
        return projectName;
    }

    private void loadConfig() throws ParseException {
    File file = new File("keys.json");
    String str = "";
    try (Scanner scanner = new Scanner(file)) {

        while (scanner.hasNextLine()) {
            str += scanner.nextLine();
        }
        scanner.close();

    } catch (IOException e) {
        e.printStackTrace();
    }
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(str);
        JSONObject jsonObject = (JSONObject) obj;
        slackAdminKey = jsonObject.get(slackAdminKeyID).toString();
        slackKey = jsonObject.get(slackKeyID).toString();
        databaseURL = jsonObject.get(databaseURLID).toString();
        databaseUser = jsonObject.get(databaseUserID).toString();
        databasePass = jsonObject.get(databasePassID).toString();
        projectName = jsonObject.get(projectNameID).toString();


    }





}
