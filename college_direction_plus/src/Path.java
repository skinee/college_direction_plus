public class Path {
    private int id;
    private int startLocationId;
    private int endLocationId;
    private int distance;

    public Path(int id, int startLocationId, int endLocationId, int distance) {
        this.id = id;
        this.startLocationId = startLocationId;
        this.endLocationId = endLocationId;
        this.distance = distance;
    }

    public int getId() { return id; }
    public int getStartLocationId() { return startLocationId; }
    public int getEndLocationId() { return endLocationId; }
    public int getDistance() { return distance; }

    @Override
    public String toString() {
        return "起点ID: " + startLocationId + ", 终点ID: " + endLocationId + ", 距离: " + distance;
    }
}