package cal.info;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceVente {
    private final DataBaseManager dbManager;
    private final ChaussetteDAO chaussetteDAO;
    private final VenteDAO venteDAO;

    public ServiceVente(DataBaseManager dbManager, ChaussetteDAO chaussetteDAO, VenteDAO venteDAO) {
        this.dbManager = dbManager;
        this.chaussetteDAO = chaussetteDAO;
        this.venteDAO = venteDAO;
    }

    public List<Vente> listerVentes() {
        try (Connection conn = dbManager.getConnection()) {
            List<Vente> ventes = venteDAO.findAll(conn);
            for (Vente v : ventes) {
                v.setChaussettes(chaussetteDAO.findByVenteId(conn, v.getIdentifiant()));
            }
            return ventes;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Vente> rechercherVentesParDate(String min, String max) {
        try (Connection conn = dbManager.getConnection()) {
            List<Vente> ventes = venteDAO.findByDate(conn, min, max);
            for (Vente v : ventes) {
                v.setChaussettes(chaussetteDAO.findByVenteId(conn, v.getIdentifiant()));
            }
            return ventes;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Vente creerVente(List<Integer> ids) throws Exception {
        if (ids == null || ids.isEmpty()) throw new IllegalArgumentException("Liste vide");

        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                double total = 0;
                List<Chaussette> aVendre = new ArrayList<>();
                for (Integer id : ids) {
                    Chaussette c = chaussetteDAO.findById(conn, id);
                    if (c == null || c.getVenteId() != null) {
                        throw new Exception("Chaussette " + id + " indisponible.");
                    }
                    total += c.getPrix();
                    aVendre.add(c);
                }

                Vente v = new Vente();
                v.setTotal(total);
                venteDAO.save(conn, v);

                for (Chaussette c : aVendre) {
                    chaussetteDAO.linkToVente(conn, c.getIdentifiant(), v.getIdentifiant());
                }

                v.setChaussettes(aVendre);
                conn.commit();
                return v;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public Vente ajouterAVente(int idVente, List<Integer> ids) throws Exception {
        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Vente v = venteDAO.findById(conn, idVente);
                if (v == null) throw new Exception("Vente introuvable");

                double totalAjout = 0;
                List<Chaussette> ajout = new ArrayList<>();

                for (Integer id : ids) {
                    Chaussette c = chaussetteDAO.findById(conn, id);
                    if (c == null || c.getVenteId() != null) throw new Exception("Chaussette " + id + " indisponible");
                    totalAjout += c.getPrix();
                    ajout.add(c);
                    chaussetteDAO.linkToVente(conn, id, idVente);
                }

                v.setTotal(v.getTotal() + totalAjout);
                venteDAO.update(conn, v);

                conn.commit();

                v.setChaussettes(chaussetteDAO.findByVenteId(conn, idVente));
                return v;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean annulerVente(int idVente) {
        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                chaussetteDAO.unlinkFromVente(conn, idVente);
                boolean ok = venteDAO.delete(conn, idVente);

                if(ok) conn.commit();
                else conn.rollback();

                return ok;
            } catch (Exception e) {
                conn.rollback();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}