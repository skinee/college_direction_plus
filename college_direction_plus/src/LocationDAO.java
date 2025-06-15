import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO {

    private PathDAO pathDAO; // 声明 PathDAO 类型的成员变量

    public LocationDAO() {
        this.pathDAO = new PathDAO(); // 初始化 PathDAO 实例
    }

    public List<Location> getAllLocations() throws SQLException {
        String sql = "SELECT * FROM locations";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Location> locations = new ArrayList<>();
            while (rs.next()) {
                locations.add(new Location(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
            }
            return locations;
        }
    }

    public int getLocationCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM locations";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public void addLocation(String name, String description) throws SQLException {
        String sql = "INSERT INTO locations (name, description) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.executeUpdate();
        }
    }

    public void deleteLocation(int id) throws SQLException {
        deletePathsByLocationId(id);
        String sql = "DELETE FROM locations WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private void deletePathsByLocationId(int locationId) throws SQLException {
        // 获取与该地点相关联的所有道路的 ID
        List<Integer> pathIds = pathDAO.getPathsIdsByLocationId(locationId);

        // 删除这些道路
        for (Integer pathId : pathIds) {
            String sql = "DELETE FROM paths WHERE id = ?";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, pathId);
                pstmt.executeUpdate();
            }
        }
    }
}