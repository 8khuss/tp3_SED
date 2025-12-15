package cal.info;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseManager {
    private final HikariDataSource dataSource;

    public DataBaseManager() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:./hautlesbasdb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");

        this.dataSource = new HikariDataSource(config);
        initTables();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void initTables() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS vente (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "date_vente TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "total DOUBLE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS chaussette (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "couleur VARCHAR(50), " +
                    "taille VARCHAR(50), " +
                    "type_tissu VARCHAR(50), " +
                    "prix DOUBLE, " +
                    "vente_id INT, " +
                    "FOREIGN KEY (vente_id) REFERENCES vente(id))");

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'initialisation de la BD", e);
        }
    }
}