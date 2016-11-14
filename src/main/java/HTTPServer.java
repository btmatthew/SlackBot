import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;      // used to get the POST values from the Header body
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

        // https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/Headers.html
        String postResponse = t.getRequestHeaders();
        Set<Map.Entry<String, List<String>>> postResponseMapped = headers.entrySet();
        // it gets the reader values and save them in a Map variable above
        // now you read directly from the original URL (/user.php) and use port 443
         
        
        // -- can't track the URL because it won't show anything there
        // String response = t.getRequestURI().getPath();
        // String[] userDetails = response.split("/");
        // System.out.println(response);
        ParsedSlackReply reply = session.inviteUser(userDetails[1],userDetails[2],true).getReply();

        t.sendResponseHeaders(202, response.length());

        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();


    }
}
