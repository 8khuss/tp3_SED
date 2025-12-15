package cal.info;

import java.util.ArrayList;
import java.util.List;

public class Vente {
    private int identifiant;
    private String dateVente;
    private double total;
    private List<Chaussette> chaussettes = new ArrayList<>();

    public Vente() {}

    public int getIdentifiant() { return identifiant; }
    public void setIdentifiant(int identifiant) { this.identifiant = identifiant; }

    public String getDateVente() { return dateVente; }
    public void setDateVente(String dateVente) { this.dateVente = dateVente; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public List<Chaussette> getChaussettes() { return chaussettes; }
    public void setChaussettes(List<Chaussette> chaussettes) { this.chaussettes = chaussettes; }
}