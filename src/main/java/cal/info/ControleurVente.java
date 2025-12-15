package cal.info;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ControleurVente implements HttpHandler {

    private final ServiceVente service;
    private final ObjectMapper mapper = new ObjectMapper();

    public ControleurVente(ServiceVente service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange echange) throws IOException {
        String methode = echange.getRequestMethod();
        try {
            switch (methode) {
                case "GET":
                    String q = echange.getRequestURI().getQuery();
                    if (q != null && q.contains("minDate=") && q.contains("maxDate=")) {
                        String min = extraireParam(q, "minDate");
                        String max = extraireParam(q, "maxDate");
                        envoyerJson(echange, service.rechercherVentesParDate(min, max), 200);
                    } else {
                        envoyerJson(echange, service.listerVentes(), 200);
                    }
                    break;
                case "POST": handlePost(echange); break;
                case "DELETE": handleDelete(echange); break;
                default: echange.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            envoyerReponseTexte(echange, "Erreur: " + e.getMessage(), 400);
        }
    }

    @SuppressWarnings("unchecked")
    private void handlePost(HttpExchange echange) throws Exception {
        String path = echange.getRequestURI().getPath();
        try (InputStreamReader reader = new InputStreamReader(echange.getRequestBody(), StandardCharsets.UTF_8)) {
            Map<String, Object> body = mapper.readValue(reader, Map.class);
            List<Integer> ids = (List<Integer>) body.get("ids");

            Vente v;
            if (path.endsWith("/ajouter")) {
                String query = echange.getRequestURI().getQuery();
                if (query == null || !query.contains("identifiant=")) throw new IllegalArgumentException("ID manquant");
                int idVente = Integer.parseInt(query.split("identifiant=")[1].split("&")[0]);
                v = service.ajouterAVente(idVente, ids);
            } else {
                v = service.creerVente(ids);
            }
            envoyerJson(echange, v, 201);
        }
    }

    private void handleDelete(HttpExchange echange) throws IOException {
        String query = echange.getRequestURI().getQuery();
        if (query != null && query.contains("identifiant=")) {
            int id = Integer.parseInt(query.split("identifiant=")[1].split("&")[0]);
            if (service.annulerVente(id)) envoyerReponseTexte(echange, "Annulée", 204);
            else envoyerReponseTexte(echange, "Non trouvée", 404);
        } else {
            envoyerReponseTexte(echange, "ID manquant", 400);
        }
    }

    private void envoyerJson(HttpExchange ex, Object obj, int code) throws IOException {
        byte[] bytes = mapper.writeValueAsBytes(obj);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    private void envoyerReponseTexte(HttpExchange ex, String msg, int code) throws IOException {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    private String extraireParam(String query, String param) {
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2 && kv[0].equals(param)) return kv[1];
        }
        return null;
    }
}