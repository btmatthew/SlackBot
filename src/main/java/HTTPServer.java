import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Mateusz on 14/11/2016.
 */
public class HTTPServer implements HttpHandler {


    public void handle(HttpExchange t) throws IOException {
        Keys keys = new Keys();
        SlackSession session = SlackSessionFactory.createWebSocketSlackSession(keys.slackAdminKey);
        session.connect();
        String response = t.getRequestURI().getPath();
        String[] userDetails = response.split("/");

        session.inviteUser(userDetails[2],userDetails[3],true);
        t.sendResponseHeaders(202, response.length());

        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
        session.disconnect();

    }
}
