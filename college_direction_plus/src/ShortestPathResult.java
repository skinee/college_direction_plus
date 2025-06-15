import java.util.List;

public class ShortestPathResult {
    private List<Location> path;
    private int pathLength;

    public ShortestPathResult(List<Location> path, int pathLength) {
        this.path = path;
        this.pathLength = pathLength;
    }

    public List<Location> getPath() {
        return path;
    }

    public int getPathLength() {
        return pathLength;
    }
}