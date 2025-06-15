import java.util.*;

public class Graph {
    private int n; // 景点数量
    private Location[] locations; // 景点数组
    private int[][] edges; // 邻接矩阵

    public Graph(int n) {
        this.n = n;
        this.locations = new Location[n];
        this.edges = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(edges[i], Integer.MAX_VALUE);
            edges[i][i] = 0;
        }
    }

    public void addLocation(int index, Location location) {
        locations[index] = location;
    }

    public void addEdge(int start, int end, int distance) {
        edges[start][end] = distance;
        edges[end][start] = distance; // 无向图
    }

    public ShortestPathResult dijkstra(int start, int end) {
        int[] dist = new int[n];
        boolean[] visited = new boolean[n];
        int[] prev = new int[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[start] = 0;

        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(i -> dist[i]));
        pq.offer(start);

        while (!pq.isEmpty()) {
            int u = pq.poll();
            if (u == end) break; // 找到终点，提前退出

            if (visited[u]) continue;
            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (!visited[v] && edges[u][v] != Integer.MAX_VALUE) {
                    int newDist = dist[u] + edges[u][v];
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        prev[v] = u;
                        pq.offer(v);
                    }
                }
            }
        }

        if (dist[end] == Integer.MAX_VALUE) {
            return null; // 没有路径
        }

        // 回溯路径
        List<Location> path = new ArrayList<>();
        for (int at = end; at != -1; at = prev[at]) {
            path.add(locations[at]);
        }
        Collections.reverse(path);

        return new ShortestPathResult(path, dist[end]);
    }

    public class PathResult {
        public List<Location> path;
        public int length;

        public PathResult(List<Location> path, int length) {
            this.path = path;
            this.length = length;
        }
    }

    public PathResult fixedVerticesPath(String startName, String endName, int num) {
        int startIndex = -1, endIndex = -1;
        for (int i = 0; i < n; i++) {
            if (locations[i].getName().equals(startName)) {
                startIndex = i;
            }
            if (locations[i].getName().equals(endName)) {
                endIndex = i;
            }
        }

        if (startIndex == -1 || endIndex == -1) {
            System.out.println("起点或终点不存在！");
            return null;
        }

        List<Integer> path = new ArrayList<>();
        boolean[] visited = new boolean[n];
        int[] minPathLength = {Integer.MAX_VALUE};
        List<Integer> bestPath = new ArrayList<>();

        dfs(startIndex, endIndex, num, path, visited, 0, minPathLength, bestPath);

        if (bestPath.isEmpty()) {
            System.out.println("没有找到经过 " + num + " 个地点的路径！");
            return null;
        } else {
            List<Location> resultPath = new ArrayList<>();
            for (int index : bestPath) {
                resultPath.add(locations[index]);
            }
            return new PathResult(resultPath, minPathLength[0]); // 返回路径和长度
        }
    }

    private void dfs(int u, int end, int num, List<Integer> path, boolean[] visited, int length, int[] minPathLength, List<Integer> bestPath) {
        if (path.size() > num) return;
        if (u == end && path.size() == num) {
            if (length < minPathLength[0]) {
                minPathLength[0] = length;
                bestPath.clear();
                bestPath.addAll(path);
            }
            return;
        }

        visited[u] = true;
        path.add(u);

        for (int v = 0; v < n; v++) {
            if (edges[u][v] != Integer.MAX_VALUE && !visited[v]) {
                dfs(v, end, num, path, visited, length + edges[u][v], minPathLength, bestPath);
            }
        }

        path.remove(path.size() - 1);
        visited[u] = false;
    }

}