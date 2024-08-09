package Lp.Bullll;

import java.util.*;

public class Dijkstra {
    private static final int[] dx = {-1, 1, 0, 0};
    private static final int[] dy = {0, 0, -1, 1};

    public static void main(String[] args) {
        char[][] grid = {
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '$', '.', '.', '.', '.', '.', '.', '.', '#'},
            {'#', '.', '.', '#', '.', '#', '#', '#', '.', '#'},
            {'#', '.', '#', '.', '.', '.', '.', '.', '.', '#'},
            {'#', '#', '.', '.', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '.', '.', '.', '.', '.', '&', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
        };

        Dijkstra dijkstra = new Dijkstra();
        // Initialize start and end coordinates
        int startX = -1, startY = -1, endX = -1, endY = -1;
        // Find the start point ($) and the end point (&)
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == '$') {
                    startX = i;
                    startY = j;
                } else if (grid[i][j] == '&') {
                    endX = i;
                    endY = j;
                }
            }
        }
        List<int[]> path = new ArrayList<>();
        if (startX != -1 && startY != -1 && endX != -1 && endY != -1) {
            // Find the shortest path using Dijkstra's algorithm
            // grid, 1, 1, 5, 8
            path = dijkstra.findShortestPath(grid, startX, startY, endX, endY);
        } else {
            System.out.println("Start or end point are fucked.");
        }

        // Mark the path on the grid
        for (int[] p : path) {
            if (grid[p[0]][p[1]] == '.') {
                grid[p[0]][p[1]] = '*';
            }
        }

        // Print the grid with the path
        for (char[] row : grid) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public List<int[]> findShortestPath(char[][] grid, int startX, int startY, int endX, int endY) {
        int rows = grid.length;
        int cols = grid[0].length;
        PriorityQueue<Node> queue = new PriorityQueue<>();
        int[][] distances = new int[rows][cols];
        int[][][] previous = new int[rows][cols][2];

        for (int i = 0; i < rows; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
            for (int j = 0; j < cols; j++) {
                previous[i][j] = new int[]{-1, -1};
            }
        }
        distances[startX][startY] = 0;

        queue.add(new Node(startX, startY, 0));

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            int x = currentNode.x;
            int y = currentNode.y;

            if (x == endX && y == endY) {
                return buildPath(previous, endX, endY);
            }

            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];

                if (nx >= 0 && ny >= 0 && nx < rows && ny < cols && grid[nx][ny] != '#') {
                    int newDist = distances[x][y] + 1;
                    if (newDist < distances[nx][ny]) {
                        distances[nx][ny] = newDist;
                        previous[nx][ny] = new int[]{x, y};
                        queue.add(new Node(nx, ny, newDist));
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private List<int[]> buildPath(int[][][] previous, int endX, int endY) {
        List<int[]> path = new LinkedList<>();
        for (int[] at = {endX, endY}; at[0] != -1; at = previous[at[0]][at[1]]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}

class Node implements Comparable<Node> {
    int x, y;
    int distance;

    public Node(int x, int y, int distance) {
        this.x = x;
        this.y = y;
        this.distance = distance;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.distance, other.distance);
    }
}
