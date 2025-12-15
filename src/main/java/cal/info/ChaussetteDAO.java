package cal.info;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChaussetteDAO {

    public List<Chaussette> findAllAvailable(Connection conn) throws SQLException {
        List<Chaussette> list = new ArrayList<>();
        String sql = "SELECT * FROM chaussette WHERE vente_id IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Chaussette> findByCriteria(Connection conn, String couleur, String taille) throws SQLException {
        List<Chaussette> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM chaussette WHERE vente_id IS NULL");
        List<Object> params = new ArrayList<>();

        if (couleur != null && !couleur.isEmpty()) {
            sql.append(" AND couleur = ?");
            params.add(couleur);
        }
        if (taille != null && !taille.isEmpty()) {
            sql.append(" AND taille = ?");
            params.add(taille);
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Chaussette findById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM chaussette WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<Chaussette> findByVenteId(Connection conn, int venteId) throws SQLException {
        List<Chaussette> list = new ArrayList<>();
        String sql = "SELECT * FROM chaussette WHERE vente_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, venteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public void save(Connection conn, Chaussette c) throws SQLException {
        String sql = "INSERT INTO chaussette (couleur, taille, type_tissu, prix) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, c.getCouleur());
            stmt.setString(2, c.getTaille());
            stmt.setString(3, c.getTypeTissu());
            stmt.setDouble(4, c.getPrix());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) c.setIdentifiant(rs.getInt(1));
            }
        }
    }

    public boolean update(Connection conn, Chaussette c) throws SQLException {
        String sql = "UPDATE chaussette SET couleur=?, taille=?, type_tissu=?, prix=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getCouleur());
            stmt.setString(2, c.getTaille());
            stmt.setString(3, c.getTypeTissu());
            stmt.setDouble(4, c.getPrix());
            stmt.setInt(5, c.getIdentifiant());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM chaussette WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public void linkToVente(Connection conn, int chaussetteId, int venteId) throws SQLException {
        String sql = "UPDATE chaussette SET vente_id = ? WHERE id = ? AND vente_id IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, venteId);
            stmt.setInt(2, chaussetteId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Impossible de vendre la chaussette " + chaussetteId);
            }
        }
    }

    public void unlinkFromVente(Connection conn, int venteId) throws SQLException {
        String sql = "UPDATE chaussette SET vente_id = NULL WHERE vente_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, venteId);
            stmt.executeUpdate();
        }
    }

    private Chaussette map(ResultSet rs) throws SQLException {
        Chaussette c = new Chaussette(
                rs.getInt("id"),
                rs.getString("couleur"),
                rs.getString("taille"),
                rs.getString("type_tissu"),
                rs.getDouble("prix")
        );
        int vid = rs.getInt("vente_id");
        if (!rs.wasNull()) c.setVenteId(vid);
        return c;
    }
}