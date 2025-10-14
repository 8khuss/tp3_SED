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

    @Override
    public void handle(HttpExchange echange) throws IOException {
        String methode = echange.getRequestMethod();

        switch (methode) {
            case "GET": {
                String parametres = echange.getRequestURI().getQuery();

                if (parametres != null && parametres.contains("identifiant=")) {
                    int id = parseId(parametres);
                    if (id < 0) { envoyerReponseTexte(echange, "Identifiant invalide.", 400); break; }
                    afficherChaussette(echange, id);
                } else {
                    afficherToutesLesChaussettes(echange);
                }
                break;
            }

            case "POST": {
                try {
                    ajouterNouvelleChaussette(echange);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    echange.sendResponseHeaders(500, -1);
                }
                break;
            }

            case "PUT": {
                String parametres = echange.getRequestURI().getQuery();
                if (parametres == null || !parametres.contains("identifiant=")) {
                    envoyerReponseTexte(echange, "Le paramètre 'identifiant' est manquant.", 400);
                    break;
                }
                int id = parseId(parametres);
                if (id < 0) { envoyerReponseTexte(echange, "Identifiant invalide.", 400); break; }

                try {
                    modifierChaussette(echange, id);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    echange.sendResponseHeaders(500, -1);
                }
                break;
            }

            case "DELETE": {
                String parametres = echange.getRequestURI().getQuery();

                if (parametres != null && parametres.contains("identifiant=")) {
                    int id = parseId(parametres);
                    if (id < 0) { envoyerReponseTexte(echange, "Identifiant fourni invalide.", 400); break; }
                    retirerChaussette(echange, id);
                } else {
                    envoyerReponseTexte(echange, "Le paramètre 'identifiant' est manquant.", 400);
                }
                break;
            }

            default:
                echange.sendResponseHeaders(405, -1); // Méthode non autorisée
                break;
        }
    }

    // POST: ajouter
    public void ajouterNouvelleChaussette(HttpExchange echange) throws IOException {
        var lecteur = new InputStreamReader(echange.getRequestBody(), StandardCharsets.UTF_8);
        var mapper = new ObjectMapper();

        Chaussette chaussette = mapper.readValue(lecteur, Chaussette.class);

        // validation minimale
        if (chaussette == null ||
                chaussette.getCouleur() == null || chaussette.getCouleur().trim().isEmpty() ||
                chaussette.getTaille()  == null || chaussette.getTaille().trim().isEmpty()  ||
                chaussette.getTypeTissu()== null || chaussette.getTypeTissu().trim().isEmpty()||
                chaussette.getPrix() < 0) {
            envoyerReponseTexte(echange, "Données invalides.", 400);
            return;
        }

        ServiceInventaire.ajouterChaussette(chaussette);
        envoyerReponseTexte(echange, "Nouvelle chaussette ajoutée avec succès.", 201);
    }

    // PUT: modifier
    public void modifierChaussette(HttpExchange echange, int identifiant) throws IOException {
        var lecteur = new InputStreamReader(echange.getRequestBody(), StandardCharsets.UTF_8);
        var mapper = new ObjectMapper();

        Chaussette nouvelle = mapper.readValue(lecteur, Chaussette.class);

        if (nouvelle == null ||
                nouvelle.getCouleur() == null || nouvelle.getCouleur().trim().isEmpty() ||
                nouvelle.getTaille()  == null || nouvelle.getTaille().trim().isEmpty()  ||
                nouvelle.getTypeTissu()== null || nouvelle.getTypeTissu().trim().isEmpty()||
                nouvelle.getPrix() < 0) {
            envoyerReponseTexte(echange, "Données invalides.", 400);
            return;
        }

        boolean ok = ServiceInventaire.modifierChaussette(identifiant, nouvelle);
        if (ok) envoyerReponseTexte(echange, "La chaussette a été modifiée avec succès.", 200);
        else    envoyerReponseTexte(echange, "Aucune chaussette trouvée pour l'identifiant " + identifiant, 404);
    }

    // DELETE: supprimer
    public void retirerChaussette(HttpExchange echange, int identifiant) throws IOException {
        boolean ok = ServiceInventaire.supprimerChaussette(identifiant);
        if (ok) {
            envoyerReponseTexte(echange, "La chaussette #" + identifiant + " a été supprimée.", 200);
        } else {
            envoyerReponseTexte(echange, "Aucune chaussette trouvée avec l'identifiant " + identifiant, 404);
        }
    }

    // GET: lister
    public void afficherToutesLesChaussettes(HttpExchange echange) {
        List<Chaussette> liste = ServiceInventaire.listerChaussettes();
        var mapper = new ObjectMapper();

        try {
            String json = mapper.writeValueAsString(liste);
            byte[] contenu = json.getBytes(StandardCharsets.UTF_8);

            echange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            echange.sendResponseHeaders(200, contenu.length);
            echange.getResponseBody().write(contenu);
            echange.getResponseBody().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // GET: par id (le service renvoie une liste)
    public void afficherChaussette(HttpExchange echange, int identifiant) throws IOException {
        List<Chaussette> trouvees = ServiceInventaire.rechercherChaussette(identifiant);
        var mapper = new ObjectMapper();

        if (trouvees.isEmpty()) {
            envoyerReponseTexte(echange, "Aucune chaussette trouvée pour l'identifiant : " + identifiant, 404);
            return;
        }

        String reponseJson = mapper.writeValueAsString(trouvees);
        byte[] contenu = reponseJson.getBytes(StandardCharsets.UTF_8);

        echange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        echange.sendResponseHeaders(200, contenu.length);
        echange.getResponseBody().write(contenu);
        echange.getResponseBody().close();
    }

    // utilitaire texte
    private void envoyerReponseTexte(HttpExchange echange, String message, int code) throws IOException {
        byte[] donnees = message.getBytes(StandardCharsets.UTF_8);
        echange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        echange.sendResponseHeaders(code, donnees.length);
        echange.getResponseBody().write(donnees);
        echange.getResponseBody().close();
    }

    // parsing simple: identifiant=123[&...]
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
