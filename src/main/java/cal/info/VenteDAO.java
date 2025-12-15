package cal.info;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenteDAO {

    public List<Vente> findAll(Connection conn) throws SQLException {
        List<Vente> list = new ArrayList<>();
        String sql = "SELECT * FROM vente";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Vente> findByDate(Connection conn, String dateMin, String dateMax) throws SQLException {
        List<Vente> list = new ArrayList<>();
        String sql = "SELECT * FROM vente WHERE date_vente >= ? AND date_vente <= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dateMin + " 00:00:00");
            stmt.setString(2, dateMax + " 23:59:59");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Vente findById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM vente WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public void save(Connection conn, Vente v) throws SQLException {
        String sql = "INSERT INTO vente (total) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, v.getTotal());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) v.setIdentifiant(rs.getInt(1));
            }
        }
        refreshDate(conn, v);
    }

    public void update(Connection conn, Vente v) throws SQLException {
        String sql = "UPDATE vente SET total = ? WHERE id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setDouble(1, v.getTotal());
            stmt.setInt(2, v.getIdentifiant());
            stmt.executeUpdate();
        }
    }

    public boolean delete(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM vente WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private void refreshDate(Connection conn, Vente v) throws SQLException {
        String sql = "SELECT date_vente FROM vente WHERE id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, v.getIdentifiant());
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()) v.setDateVente(rs.getString(1));
            }
        }
    }

    private Vente map(ResultSet rs) throws SQLException {
        Vente v = new Vente();
        v.setIdentifiant(rs.getInt("id"));
        v.setDateVente(rs.getString("date_vente"));
        v.setTotal(rs.getDouble("total"));
        return v;
    }
}