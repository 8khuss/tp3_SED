package cal.info;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

public class ServiceInventaire {
    private final DataBaseManager dbManager;
    private final ChaussetteDAO dao;

    public ServiceInventaire(DataBaseManager dbManager, ChaussetteDAO dao) {
        this.dbManager = dbManager;
        this.dao = dao;
    }

    public void ajouterChaussette(Chaussette c) {
        if (c == null) return;
        try (Connection conn = dbManager.getConnection()) {
            dao.save(conn, c);
        } catch (Exception e) {
            throw new RuntimeException("Erreur ajout chaussette", e);
        }
    }

    public boolean modifierChaussette(int id, Chaussette nouvelle) {
        try (Connection conn = dbManager.getConnection()) {
            nouvelle.setIdentifiant(id);
            return dao.update(conn, nouvelle);
        } catch (Exception e) {
            throw new RuntimeException("Erreur modif chaussette", e);
        }
    }

    public boolean supprimerChaussette(int id) {
        try (Connection conn = dbManager.getConnection()) {
            return dao.delete(conn, id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur suppression chaussette", e);
        }
    }

    public List<Chaussette> listerChaussettes() {
        try (Connection conn = dbManager.getConnection()) {
            return dao.findAllAvailable(conn);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Chaussette> rechercherChaussette(int id) {
        try (Connection conn = dbManager.getConnection()) {
            Chaussette c = dao.findById(conn, id);
            return c != null ? List.of(c) : Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Chaussette> rechercherParCriteres(String couleur, String taille) {
        try (Connection conn = dbManager.getConnection()) {
            return dao.findByCriteria(conn, couleur, taille);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}