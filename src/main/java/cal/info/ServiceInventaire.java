package cal.info;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServiceInventaire {

    private static final List<Chaussette> stock = new ArrayList<>();

    public static void ajouterChaussette(Chaussette c) {
        if (c == null) return;
        if (c.getIdentifiant() == 0) {
            int next = 0;
            for (Chaussette x : stock) {
                if (x.getIdentifiant() > next) next = x.getIdentifiant();
            }
            c.setIdentifiant(next + 1);
        }
        stock.add(c);
    }

    public static boolean modifierChaussette(int id, Chaussette nouvelle) {
        if (nouvelle == null) return false;
        for (int i = 0; i < stock.size(); i++) {
            if (stock.get(i).getIdentifiant() == id) {
                nouvelle.setIdentifiant(id);
                stock.set(i, nouvelle);
                return true;
            }
        }
        return false;
    }

    public static boolean supprimerChaussette(int id) {
        Iterator<Chaussette> it = stock.iterator();
        while (it.hasNext()) {
            if (it.next().getIdentifiant() == id) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    // Lister tout
    public static List<Chaussette> listerChaussettes() {
        return new ArrayList<>(stock);
    }

    public static List<Chaussette> rechercherChaussette(int idRecherche) {
        List<Chaussette> res = new ArrayList<>();
        for (Chaussette c : stock) {
            if (c.getIdentifiant() == idRecherche) res.add(c);
        }
        return res;
    }


    public static Chaussette retirerParId(int id) {
        for (int i = 0; i < stock.size(); i++) {
            if (stock.get(i).getIdentifiant() == id) {
                return stock.remove(i);
            }
        }
        return null;
    }

    public static void remettre(Chaussette c) {
        if (c != null) stock.add(c);
    }
}
