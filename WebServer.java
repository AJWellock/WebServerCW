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
import java.util.Date;

public class WebServer {

    private int port;
    private String rootDir;
    private boolean logging;
    Date date = new Date();

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
            String pathname = rootDir + URI;
            Path Fullpath = Paths.get(pathname);
            Path AllPath = Fullpath.toAbsolutePath().normalize();
            if ("GET".equals(methName)){
                ResponseMessage msg = new ResponseMessage(200); 
            String uri =  URLDecoder.decode(URI,"ASCII");
            Path path = Paths.get(rootDir).resolve(uri).normalize(); 
            if (!path.startsWith(Paths.get(rootDir))){
                try{
            ResponseMessage msgBroken = new ResponseMessage(400); 
            msgBroken.write(os);   
            byte[] b = Files.readAllBytes(path);
            os.write(" Request is bad ".getBytes());
            os.write(b);
            }
            catch
            (IOException x) {
            System.err.format("IOException: %s%n", x);
            }
            }
               
            if (path.startsWith(rootDir)&& uri.equals(URI)){
            InputStream thefile = Files.newInputStream(AllPath); 
                    while (true) {
                        int a = thefile.read();
                        if (a == -1) {
                            break;
                        }
                        ResponseMessage FileObtained = new ResponseMessage(400); 
                        FileObtained.write(os);   
                         byte[] b = Files.readAllBytes(path);
                         os.write(" File Obtained:".getBytes());
                         os.write(b);
                         os.write(a);              
            }
            }
            }
            
            if ("HEAD".equals(methName)){
                ResponseMessage head = new ResponseMessage(200);
                head.getHeaderFieldValue("Connection");
                head.addHeaderField("Date",date.toString());
                head.getHeaderFieldValue("Status");
                head.getHeaderFieldValue("Server");
               
                head.write(os);
                //os.write(" Weclome to Group 42 server ".getBytes());;
                

                conn.close();
            }
            if ("PUT".equals(methName)){
                  OutputStream putfile = Files.newOutputStream(AllPath);
                int count = 0;
                while (true) {
                    int c = is.read();
                    if (c == -1) {
                        break;
                    }
                    String uri =  URLDecoder.decode(URI,"ASCII");
                    Path path = Paths.get(rootDir).resolve(uri).normalize(); 
                    byte[] d = Files.readAllBytes(path);
                     os.write(" File Created:".getBytes());
                    putfile.write(d);
                    ++count;
                }
                putfile.close();
                ResponseMessage putresp = new ResponseMessage(201);
                putresp.write(os);
                //os.write(" Weclome to Group 42 server ".getBytes());

                
                conn.close();
            }
            
            else{
               ResponseMessage msg = new ResponseMessage(500);  
            }
            // get the output stream for sending data to the client 
            ResponseMessage msg = new ResponseMessage(200); 
            msg.write(os); 
            os.write("Team 23: ".getBytes()); 
 
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
