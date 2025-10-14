package cal.info;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServiceVente {

    private static final List<Vente> ventes = new ArrayList<>();
    private static int seq = 1;

    public static List<Vente> listerVentes() {
        return new ArrayList<>(ventes);
    }

    // Créer une vente à partir d'une liste d'ids de chaussettes
    public static Vente creerVente(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return null;

        List<Chaussette> prises = new ArrayList<>();
        for (int id : ids) {
            Chaussette c = ServiceInventaire.retirerParId(id);
            if (c == null) {
                // rollback
                for (Chaussette r : prises) ServiceInventaire.remettre(r);
                return null;
            }
            prises.add(c);
        }

        Vente v = new Vente(seq++);
        for (Chaussette c : prises) v.ajouterChaussette(c);
        ventes.add(v);
        return v;
    }

    // Ajouter des chaussettes à une vente existante
    public static Vente ajouterAVente(int idVente, List<Integer> ids) {
        Vente v = trouverParId(idVente);
        if (v == null || ids == null || ids.isEmpty()) return null;

        List<Chaussette> prises = new ArrayList<>();
        for (int id : ids) {
            Chaussette c = ServiceInventaire.retirerParId(id);
            if (c == null) {
                // rollback local
                for (Chaussette r : prises) ServiceInventaire.remettre(r);
                return null;
            }
            prises.add(c);
        }
        for (Chaussette c : prises) v.ajouterChaussette(c);
        return v;
    }

    // Annuler une vente (remet les chaussettes dans l'inventaire)
    public static boolean annulerVente(int id) {
        Iterator<Vente> it = ventes.iterator();
        while (it.hasNext()) {
            Vente v = it.next();
            if (v.getIdentifiant() == id) {
                for (Chaussette c : v.getChaussettes()) ServiceInventaire.remettre(c);
                it.remove();
                return true;
            }
        }
        return false;
    }

    public static Vente trouverParId(int id) {
        for (Vente v : ventes) if (v.getIdentifiant() == id) return v;
        return null;
    }
}
