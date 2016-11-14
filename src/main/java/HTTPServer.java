import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.replies.ParsedSlackReply;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Mateusz on 14/11/2016.
 */
public class HTTPServer implements HttpHandler {

    private SlackSession session;
    public HTTPServer(SlackSession session) {
    }

    public void handle(HttpExchange t) throws IOException {

        String response = t.getRequestURI().getPath();
        String[] userDetails = response.split("/");
        System.out.println(response);
        ParsedSlackReply reply = session.inviteUser(userDetails[1],userDetails[2],true).getReply();

        t.sendResponseHeaders(202, response.length());

        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();


    }
}
