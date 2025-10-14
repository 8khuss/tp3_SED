package cal.info;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws Exception {
        int port = 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Contrôleurs (style cours, services statiques)
        server.createContext("/api/chaussettes", new ControleurChaussette());
        server.createContext("/api/ventes",       new ControleurVente());

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        System.out.println("HautLesBas lancé sur http://localhost:" + port + "/");
        server.start();
    }
}
