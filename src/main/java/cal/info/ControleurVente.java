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

    @Override
    public void handle(HttpExchange echange) throws IOException {
        String methode = echange.getRequestMethod();

        switch (methode) {
            case "GET": {
                // lister toutes les ventes
                var mapper = new ObjectMapper();
                byte[] out = mapper.writeValueAsBytes(ServiceVente.listerVentes());
                echange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                echange.sendResponseHeaders(200, out.length);
                try (OutputStream os = echange.getResponseBody()) { os.write(out); }
                break;
            }

            case "POST": {
                String path = echange.getRequestURI().getPath(); // /api/ventes  ou  /api/ventes/ajouter
                if (path.endsWith("/ajouter")) {
                    // POST /api/ventes/ajouter?identifiant=ID
                    String params = echange.getRequestURI().getQuery();
                    if (params == null || !params.contains("identifiant=")) {
                        envoyerReponseTexte(echange, "Le paramètre 'identifiant' est manquant.", 400);
                        break;
                    }
                    int idVente = parseId(params);
                    if (idVente < 0) { envoyerReponseTexte(echange, "Identifiant de vente invalide.", 400); break; }

                    var lecteur = new InputStreamReader(echange.getRequestBody(), StandardCharsets.UTF_8);
                    var mapper = new ObjectMapper();
                    @SuppressWarnings("unchecked")
                    Map<String,Object> body = mapper.readValue(lecteur, Map.class);
                    @SuppressWarnings("unchecked")
                    List<Integer> ids = (List<Integer>) body.get("ids");

                    Vente v = ServiceVente.ajouterAVente(idVente, ids);
                    if (v == null) { envoyerReponseTexte(echange, "Opération impossible.", 400); }
                    else {
                        byte[] out = mapper.writeValueAsBytes(v);
                        echange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                        echange.sendResponseHeaders(200, out.length);
                        try (OutputStream os = echange.getResponseBody()) { os.write(out); }
                    }
                } else {
                    // POST /api/ventes  (créer une vente)  body: {"ids":[1,2]}
                    var lecteur = new InputStreamReader(echange.getRequestBody(), StandardCharsets.UTF_8);
                    var mapper = new ObjectMapper();
                    @SuppressWarnings("unchecked")
                    Map<String,Object> body = mapper.readValue(lecteur, Map.class);
                    @SuppressWarnings("unchecked")
                    List<Integer> ids = (List<Integer>) body.get("ids");

                    Vente v = ServiceVente.creerVente(ids);
                    if (v == null) { envoyerReponseTexte(echange, "IDs invalides.", 400); }
                    else {
                        byte[] out = mapper.writeValueAsBytes(v);
                        echange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                        echange.sendResponseHeaders(201, out.length);
                        try (OutputStream os = echange.getResponseBody()) { os.write(out); }
                    }
                }
                break;
            }

            case "DELETE": {
                // DELETE /api/ventes?identifiant=ID  (annuler)
                String params = echange.getRequestURI().getQuery();
                if (params == null || !params.contains("identifiant=")) {
                    envoyerReponseTexte(echange, "Le paramètre 'identifiant' est manquant.", 400);
                    break;
                }
                int idVente = parseId(params);
                if (idVente < 0) { envoyerReponseTexte(echange, "Identifiant invalide.", 400); break; }

                boolean ok = ServiceVente.annulerVente(idVente);
                if (ok) echange.sendResponseHeaders(204, -1);
                else    envoyerReponseTexte(echange, "Vente introuvable.", 404);
                break;
            }

            default:
                echange.sendResponseHeaders(405, -1);
        }
    }

    private void envoyerReponseTexte(HttpExchange echange, String message, int code) throws IOException {
        byte[] out = message.getBytes(StandardCharsets.UTF_8);
        echange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        echange.sendResponseHeaders(code, out.length);
        try (OutputStream os = echange.getResponseBody()) { os.write(out); }
    }

    private int parseId(String params) {
        try {
            String v = params.split("identifiant=")[1];
            int i = v.indexOf('&');
            if (i >= 0) v = v.substring(0, i);
            return Integer.parseInt(v.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return -1;
        }
    }
}
