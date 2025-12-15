package cal.info;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws Exception {
        int port = 8080;

        DataBaseManager dbManager = new DataBaseManager();

        ChaussetteDAO chaussetteDAO = new ChaussetteDAO();
        VenteDAO venteDAO = new VenteDAO();

        ServiceInventaire serviceInv = new ServiceInventaire(dbManager, chaussetteDAO);
        ServiceVente serviceVente = new ServiceVente(dbManager, chaussetteDAO, venteDAO);

        ControleurChaussette ctrlChaussette = new ControleurChaussette(serviceInv);
        ControleurVente ctrlVente = new ControleurVente(serviceVente);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/chaussettes", ctrlChaussette);
        server.createContext("/api/ventes", ctrlVente);

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());

        System.out.println("HautLesBas (DB H2 + DAO + Filtres) lancé sur http://localhost:" + port + "/");
        server.start();
    }
}