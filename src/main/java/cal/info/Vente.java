package cal.info;

import java.util.ArrayList;
import java.util.List;

public class Vente {
    private int identifiant;
    private String dateVente = java.time.LocalDate.now().toString(); // string simple (évite modules Jackson)
    private double total;
    private List<Chaussette> chaussettes = new ArrayList<>();

    public Vente() {}
    public Vente(int id) { this.identifiant = id; }

    public int getIdentifiant() { return identifiant; }
    public void setIdentifiant(int identifiant) { this.identifiant = identifiant; }

    public String getDateVente() { return dateVente; }
    public void setDateVente(String dateVente) { this.dateVente = dateVente; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public List<Chaussette> getChaussettes() { return chaussettes; }
    public void setChaussettes(List<Chaussette> chaussettes) { this.chaussettes = chaussettes; }

    public void ajouterChaussette(Chaussette c) {
        if (c != null) {
            chaussettes.add(c);
            recalculerTotal();
        }
    }

    public void recalculerTotal() {
        double t = 0.0;
        for (Chaussette c : chaussettes) {
            t += c.getPrix();
        }
        this.total = t;
    }
}
