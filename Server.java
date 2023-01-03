package Server;

import java.io.IOException;

public class Server {
    public static void main(String args[]) throws IOException {
        ServerRunner sv = new ServerRunner();
        sv.runServer();
    }
}