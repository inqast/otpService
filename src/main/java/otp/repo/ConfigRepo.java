package otp.repo;

import org.springframework.stereotype.Component;

import otp.model.OtpConfigDto;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Component
public class ConfigRepo {
    private final DataSource dataSource;

    public ConfigRepo(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<OtpConfigDto> getConfig() {
        String sql = "SELECT code_length, ttl_seconds FROM config WHERE id = 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(new OtpConfigDto(
                        rs.getInt("code_length"),
                        rs.getInt("ttl_seconds")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean updateConfig(OtpConfigDto config) {
        String sql = "UPDATE config SET code_length = ?, ttl_seconds = ? WHERE id = 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, config.codeLength());
            stmt.setInt(2, config.ttlSeconds());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }
    }
}

