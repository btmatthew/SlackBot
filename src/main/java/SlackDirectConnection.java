import com.sun.net.httpserver.HttpServer;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * This sample code is creating a Slack session and is connecting to slack. To get some more details on
 * how to get a token, please have a look here : https://api.slack.com/bot-users
 */
public class SlackDirectConnection
{
    public static void main(String[] args) throws IOException{
        Keys keys = new Keys();
        SlackSession session = SlackSessionFactory.createWebSocketSlackSession(keys.slackKey);
        session.connect();


        ListeningToMessageEvents slackMessanger = new ListeningToMessageEvents();
        slackMessanger.registeringAListener(session);
        slackMessanger.registeringLoginListener(session);
        slackMessanger.registeringChannelCreatedListener(session);

        HttpServer server = HttpServer.create(new InetSocketAddress(7000), 0);
        server.createContext("/addusers", new HTTPServer());
        server.setExecutor(null); // creates a default executor
        server.start();


    }
}