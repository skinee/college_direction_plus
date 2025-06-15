import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PathDAO {
    public List<Path> getAllPaths() throws SQLException {
        String sql = "SELECT id, start_location_id, end_location_id, distance FROM paths";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Path> paths = new ArrayList<>();
            while (rs.next()) {
                paths.add(new Path(
                        rs.getInt("id"),
                        rs.getInt("start_location_id"),
                        rs.getInt("end_location_id"),
                        rs.getInt("distance")
                ));
            }
            return paths;
        }
    }

    public void addPath(int startLocationId, int endLocationId, int distance) throws SQLException {
        // 检查是否已经存在道路
        String checkSql = "SELECT COUNT(*) FROM paths WHERE start_location_id = ? AND end_location_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, startLocationId);
            checkStmt.setInt(2, endLocationId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("道路已经存在，无法重复添加。");
                }
            }
        }

        // 添加道路
        String insertSql = "INSERT INTO paths (start_location_id, end_location_id, distance) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, startLocationId);
            pstmt.setInt(2, endLocationId);
            pstmt.setInt(3, distance);
            pstmt.executeUpdate();
        }
    }

    public void deletePath(int id) throws SQLException {
        String sql = "DELETE FROM paths WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    public List<Integer> getPathsIdsByLocationId(int locationId) throws SQLException {
        List<Integer> pathIds = new ArrayList<>();
        String sql = "SELECT id FROM paths WHERE start_location_id = ? OR end_location_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, locationId);
            pstmt.setInt(2, locationId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pathIds.add(rs.getInt("id"));
            }
        }
        return pathIds;
    }
}