import org.json.JSONObject;

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

    Keys(){
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

    private void loadConfig(){
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("keys.json").getFile());
    String str = "";
    try (Scanner scanner = new Scanner(file)) {

        while (scanner.hasNextLine()) {
            str += scanner.nextLine();
        }
        scanner.close();

    } catch (IOException e) {
        e.printStackTrace();
    }
        JSONObject obj = new JSONObject(str);
        slackAdminKey = obj.get(slackAdminKeyID).toString();
        slackKey = obj.get(slackKeyID).toString();
        databaseURL = obj.get(databaseURLID).toString();
        databaseUser = obj.get(databaseUserID).toString();
        databasePass = obj.get(databasePassID).toString();
        projectName = obj.get(projectNameID).toString();


    }





}
