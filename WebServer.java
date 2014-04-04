package webserver;

import in2011.http.RequestMessage;
import in2011.http.ResponseMessage;
import in2011.http.MessageFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.*;

public class WebServer {

    private int port;
    private String rootDir;
    private boolean logging;

    public WebServer(int port, String rootDir, boolean logging) {
        this.port = port;
        this.rootDir = rootDir;
        this.logging = logging;
    }

    public void start() throws IOException, MessageFormatException {
        // create a server socket 
        ServerSocket serverSock = new ServerSocket(port); 
            while (true) { 
            // listen for a new connection on the server socket 
            Socket conn = serverSock.accept(); 
             //examine byte stream sent by client
            InputStream is = conn.getInputStream();
            //extract HTTP message from stream
            RequestMessage req = RequestMessage.parse(is);
            OutputStream os = conn.getOutputStream(); 

            String methName = req.getMethod();
            String URI = req.getURI();
            if ("GET".equals(methName)){
                ResponseMessage msg = new ResponseMessage(200); 
            String uri =  URLDecoder.decode(URI,"ASCII");
            Path path = Paths.get(rootDir).resolve(uri).normalize(); 
            if (!path.startsWith(Paths.get(rootDir))){//bad path}
            byte[] b = Files.readAllBytes(path);
            os.write(b);
            }
            }
            if ("HEAD".equals(methName)){
                ResponseMessage msg = new ResponseMessage(200); 
            }
            if ("PUT".equals(methName)){
                ResponseMessage msg = new ResponseMessage(201);
            }
            else{
               ResponseMessage msg = new ResponseMessage(500);  
            }
            // get the output stream for sending data to the client 
            ResponseMessage msg = new ResponseMessage(200); 
            msg.write(os); 
            os.write(" a message of your choosing ".getBytes()); 
 
        conn.close();
            }
    }

    public static void main (String[] args) throws IOException, MessageFormatException {
        String usage = "Usage: java webserver.WebServer <port-number> <root-dir> (\"0\" | \"1\")";
        if (args.length != 3) {
            throw new Error(usage);
        }
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new Error(usage + "\n" + "<port-number> must be an integer");
        }
        String rootDir = args[1];
        boolean logging;
        if (args[2].equals("0")) {
            logging = false;
        } else if (args[2].equals("1")) {
            logging = true;
        } else {
            throw new Error(usage);
        }
        WebServer server = new WebServer(port, rootDir, logging);
        server.start();
    }
}
