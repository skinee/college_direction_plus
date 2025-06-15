import java.util.*;

public class CampusNavigation {
    private Map<Integer, Location> locations;
    private Map<Integer, List<Path>> adjacencyList;

    public CampusNavigation(List<Location> locations, List<Path> paths) {
        this.locations = new HashMap<>();
        this.adjacencyList = new HashMap<>();

        for (Location location : locations) {
            this.locations.put(location.getId(), location);
        }

        for (Path path : paths) {
            if (!adjacencyList.containsKey(path.getStartLocationId())) {
                adjacencyList.put(path.getStartLocationId(), new ArrayList<>());
            }
            adjacencyList.get(path.getStartLocationId()).add(path);
        }
    }

    public List<Location> findShortestPath(int startId, int endId) {
        PriorityQueue<Path> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.getDistance()));
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>();

        distances.put(startId, 0);
        pq.add(new Path(0, startId, startId, 0));

        while (!pq.isEmpty()) {
            Path current = pq.poll();
            int currentId = current.getEndLocationId();

            if (currentId == endId) {
                break;
            }

            if (adjacencyList.containsKey(currentId)) {
                for (Path path : adjacencyList.get(currentId)) {
                    int neighborId = path.getEndLocationId();
                    int newDistance = distances.get(currentId) + path.getDistance();

                    if (!distances.containsKey(neighborId) || newDistance < distances.get(neighborId)) {
                        distances.put(neighborId, newDistance);
                        predecessors.put(neighborId, currentId);
                        pq.add(new Path(0, currentId, neighborId, newDistance));
                    }
                }
            }
        }

        List<Location> path = new ArrayList<>();
        if (distances.containsKey(endId)) {
            for (int at = endId; at != startId; at = predecessors.get(at)) {
                path.add(locations.get(at));
            }
            path.add(locations.get(startId));
            Collections.reverse(path);
        }

        return path;
    }
}