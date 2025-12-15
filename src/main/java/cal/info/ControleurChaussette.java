package cal.info;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ControleurChaussette implements HttpHandler {

    private final ServiceInventaire service;
    private final ObjectMapper mapper = new ObjectMapper();

    public ControleurChaussette(ServiceInventaire service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange echange) throws IOException {
        String methode = echange.getRequestMethod();
        try {
            switch (methode) {
                case "GET": handleGet(echange); break;
                case "POST": handlePost(echange); break;
                case "PUT": handlePut(echange); break;
                case "DELETE": handleDelete(echange); break;
                default: echange.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            envoyerReponseTexte(echange, "Erreur interne: " + e.getMessage(), 500);
        }
    }

    private void handleGet(HttpExchange echange) throws IOException {
        String query = echange.getRequestURI().getQuery();

        if (query != null) {
            if (query.contains("identifiant=")) {
                int id = parseId(query);
                List<Chaussette> res = service.rechercherChaussette(id);
                if (res.isEmpty()) envoyerReponseTexte(echange, "Non trouvé", 404);
                else envoyerJson(echange, res, 200);
            } else {
                String couleur = extraireParam(query, "couleur");
                String taille = extraireParam(query, "taille");
                if (couleur == null && taille == null) {
                    envoyerJson(echange, service.listerChaussettes(), 200);
                } else {
                    envoyerJson(echange, service.rechercherParCriteres(couleur, taille), 200);
                }
            }
        } else {
            envoyerJson(echange, service.listerChaussettes(), 200);
        }
    }

    private void handlePost(HttpExchange echange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(echange.getRequestBody(), StandardCharsets.UTF_8)) {
            Chaussette c = mapper.readValue(reader, Chaussette.class);
            if (invalide(c)) {
                envoyerReponseTexte(echange, "Données invalides", 400);
                return;
            }
            service.ajouterChaussette(c);
            envoyerJson(echange, c, 201);
        }
    }

    private void handlePut(HttpExchange echange) throws IOException {
        String query = echange.getRequestURI().getQuery();
        if (query == null || !query.contains("identifiant=")) {
            envoyerReponseTexte(echange, "ID manquant", 400);
            return;
        }
        int id = parseId(query);

        try (InputStreamReader reader = new InputStreamReader(echange.getRequestBody(), StandardCharsets.UTF_8)) {
            Chaussette c = mapper.readValue(reader, Chaussette.class);
            if (invalide(c)) {
                envoyerReponseTexte(echange, "Données invalides", 400);
                return;
            }
            boolean ok = service.modifierChaussette(id, c);
            if (ok) envoyerJson(echange, c, 200);
            else envoyerReponseTexte(echange, "Introuvable", 404);
        }
    }

    private void handleDelete(HttpExchange echange) throws IOException {
        String query = echange.getRequestURI().getQuery();
        if (query != null && query.contains("identifiant=")) {
            int id = parseId(query);
            boolean ok = service.supprimerChaussette(id);
            if (ok) envoyerReponseTexte(echange, "Supprimé", 200);
            else envoyerReponseTexte(echange, "Introuvable", 404);
        } else {
            envoyerReponseTexte(echange, "ID manquant", 400);
        }
    }

    private boolean invalide(Chaussette c) {
        return c == null || c.getPrix() < 0 || c.getCouleur() == null;
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

    private int parseId(String query) {
        try {
            String v = query.split("identifiant=")[1].split("&")[0];
            return Integer.parseInt(v);
        } catch (Exception e) { return -1; }
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