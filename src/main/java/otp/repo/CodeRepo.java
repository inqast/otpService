package otp.repo;

import otp.model.Code;
import otp.model.CodeStatus;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CodeRepo {
    private final DataSource dataSource;

    public CodeRepo(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(int userId, String code, String operationId) {
        String sql = "INSERT INTO codes (user_id, code, status, created_at, operation_id) " +
                "VALUES (?, ?, 'ACTIVE'::status_type, NOW(), ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, code);
            stmt.setString(3, operationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Code> findActiveCode(int userId, String operationId) {
        String sql = "SELECT * FROM codes WHERE user_id = ? AND operation_id = ? AND status = 'ACTIVE' ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, operationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void updateStatus(int codeId, CodeStatus newStatus) {
        String sql = "UPDATE codes SET status = ?::status_type WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus.name());
            stmt.setInt(2, codeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Code> findAllActive() {
        List<Code> result = new ArrayList<>();
        String sql = "SELECT * FROM codes WHERE status = 'ACTIVE'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Code map(ResultSet rs) throws SQLException {
        return new Code(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("code"),
                CodeStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getString("operation_id")
        );
    }
}

