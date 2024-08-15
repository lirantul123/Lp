package Lp.Bullll;

import java.util.*;

public class Dijkstra3d {
    private static final int[] dx = {-1, 1, 0, 0, 0, 0};
    private static final int[] dy = {0, 0, -1, 1, 0, 0};
    private static final int[] dz = {0, 0, 0, 0, -1, 1};
    
    public static void main(String[] args) {
        char[][][] grid = {
            {
                {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
                {'#', '$', '.', '.', '.', '.', '.', '.', '.', '#'},
                {'#', '.', '.', '#', '.', '#', '#', '#', '.', '#'},
                {'#', '.', '#', '.', '.', '.', '.', '.', '.', '#'},
                {'#', '#', '.', '.', '#', '#', '#', '#', '#', '#'},
                {'#', '#', '#', '.', '.', '.', '.', '.', '.', '#'},
                {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
            },
            {
                {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
                {'#', '.', '.', '.', '.', '.', '.', '.', '.', '#'},
                {'#', '.', '.', '#', '.', '#', '#', '#', '.', '#'},
                {'#', '.', '#', '.', '.', '.', '.', '.', '.', '#'},
                {'#', '#', '.', '.', '#', '#', '#', '#', '#', '#'},
                {'#', '#', '#', '.', '.', '.', '.', '.', '&', '#'},
                {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
            }
        };
        
        // Initialize start and end coordinates
        int startX = -1, startY = -1, startZ = -1;
        int endX = -1, endY = -1, endZ = -1;
        // Find the start point ($) and the end point (&)
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                for (int k = 0; k < grid[i][j].length; k++) {
                    if (grid[i][j][k] == '$') {
                        startX = i;
                        startY = j;
                        startZ = k;
                    } else if (grid[i][j][k] == '&') {
                        endX = i;
                        endY = j;
                        endZ = k;
                    }
                }
            }
        }

        Dijkstra3d dijkstra = new Dijkstra3d();
        List<int[]> path = new ArrayList<>();
        if (startX != -1 && startY != -1 && startZ != -1 && endX != -1 && endY != -1 && endZ != -1) {
            path = dijkstra.findShortestPath(grid, startX, startY, startZ, endX, endY, endZ);
        } else {
            System.out.println("Start or end point are not found.");
        }

        // Mark the path on the grid
        for (int[] p : path) {
            if (grid[p[0]][p[1]][p[2]] == '.') {
                grid[p[0]][p[1]][p[2]] = '*';
            }
        }

        // Print the grid with the path for each level
        for (int i = 0; i < grid.length; i++) {
            System.out.println("Level " + i + ":");
            for (char[] row : grid[i]) {
                for (char cell : row) {
                    System.out.print(cell + " ");
                }
                System.out.println();
            }
        }
    }
    
    public List<int[]> findShortestPath(char[][][] grid, int startX, int startY, int startZ, int endX, int endY, int endZ) {
        int levels = grid.length;
        int rows = grid[0].length;
        int cols = grid[0][0].length;
        PriorityQueue<Node> queue = new PriorityQueue<>();
        int[][][] distances = new int[levels][rows][cols];
        int[][][][] previous = new int[levels][rows][cols][3];
    
        for (int i = 0; i < levels; i++) {
            for (int j = 0; j < rows; j++) {
                Arrays.fill(distances[i][j], Integer.MAX_VALUE);
                for (int k = 0; k < cols; k++) {
                    previous[i][j][k] = new int[]{-1, -1, -1};
                }
            }
        }
        distances[startX][startY][startZ] = 0;
    
        queue.add(new Node(startX, startY, startZ, 0));
    
        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            int x = currentNode.x;
            int y = currentNode.y;
            int z = currentNode.z;
    
            if (x == endX && y == endY && z == endZ) {
                return buildPath(previous, endX, endY, endZ);
            }
    
            for (int i = 0; i < 6; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                int nz = z + dz[i];
    
                if (nx >= 0 && ny >= 0 && nz >= 0 && nx < levels && ny < rows && nz < cols && grid[nx][ny][nz] != '#') {
                    int newDist = distances[x][y][z] + 1;
                    if (newDist < distances[nx][ny][nz]) {
                        distances[nx][ny][nz] = newDist;
                        previous[nx][ny][nz] = new int[]{x, y, z};
                        queue.add(new Node(nx, ny, nz, newDist));
                    }
                }
            }
        }
        return new ArrayList<>();
    }
    
    private List<int[]> buildPath(int[][][][] previous, int endX, int endY, int endZ) {
        List<int[]> path = new LinkedList<>();
        for (int[] at = {endX, endY, endZ}; at[0] != -1; at = previous[at[0]][at[1]][at[2]]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}

class Node implements Comparable<Node> {
    int x, y, z;
    int distance;

    public Node(int x, int y, int z, int distance) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.distance = distance;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.distance, other.distance);
    }
}
