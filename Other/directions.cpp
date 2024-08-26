#include <iostream>
#include <vector>
#include <queue>
#include <utility>
#include <limits>

using namespace std;

class PathFinder {
private:
    vector<vector<char>> grid; 
    int rows, cols;
    int x1, y1, x2, y2; 

    // right, down, left, up
    const vector<pair<int, int>> directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    const vector<string> direction_names = {"right", "down", "left", "up"};

    struct Node {
        int x, y, cost;
        Node(int x, int y, int cost) : x(x), y(y), cost(cost) {}
        bool operator>(const Node& other) const {// operator to determine the cumulative "expense"(distance) incurred to reach a particular node (-cost)
            return cost > other.cost;
        }
    };

    pair<int, int> direction_to_pair(int index) {
        return directions[index];
    }

    string direction_to_string(int index) {
        return direction_names[index];
    }

    void add_move_direction(int dx, int dy, string& move_description) {
        if (dx > 0) move_description += "east";
        else if (dx < 0) move_description += "west";
        if (dy > 0) move_description += (dx != 0) ? "-northeast" : "north";
        else if (dy < 0) move_description += (dx != 0) ? "-southwest" : "south";
    }

public:
    PathFinder(int startX, int startY, int endX, int endY, int rows, int cols)
        : x1(startX), y1(startY), x2(endX), y2(endY), rows(rows), cols(cols) {
        
        // Initialize the grid with borders and empty spaces
        grid.resize(rows, vector<char>(cols, '.'));
        for (int i = 0; i < rows; ++i) {
            grid[i][0] = grid[i][cols - 1] = '#';
        }
        for (int j = 0; j < cols; ++j) {
            grid[0][j] = grid[rows - 1][j] = '#';
        }
        grid[y1][x1] = '&'; // Start position
        grid[y2][x2] = '$'; // End position
    }

    void addObstacle(int x, int y) {
        if (x > 0 && x < cols - 1 && y > 0 && y < rows - 1) {
            grid[y][x] = 'X'; // Place an obstacle
        }
    }

    void calculate_steps() {
        priority_queue<Node, vector<Node>, greater<Node>> pq;
        pq.push(Node(x1, y1, 0));

        vector<vector<int>> cost(rows, vector<int>(cols, numeric_limits<int>::max()));
        vector<vector<pair<int, int>>> came_from(rows, vector<pair<int, int>>(cols, {-1, -1}));

        cost[y1][x1] = 0;
        //Dijkstra algorithm
        while (!pq.empty()) {
            Node current = pq.top();
            pq.pop();

            if (current.x == x2 && current.y == y2) break; // Reached destination

            for (int i = 0; i < directions.size(); ++i) {
                int dx = directions[i].first;
                int dy = directions[i].second;
                int nx = current.x + dx;
                int ny = current.y + dy;
                int new_cost = current.cost + 1;

                if (nx >= 0 && nx < cols && ny >= 0 && ny < rows && grid[ny][nx] != 'X' && new_cost < cost[ny][nx]) {
                    cost[ny][nx] = new_cost;
                    came_from[ny][nx] = {current.x, current.y};
                    pq.push(Node(nx, ny, new_cost));
                }
            }
        }

        // Reconstruct path
        int cx = x2, cy = y2;
        string move_description;
        while (cx != x1 || cy != y1) {
            auto [px, py] = came_from[cy][cx];// auto because too longgg
            if (px == -1 || py == -1) break; // No path found

            int dx = cx - px;
            int dy = cy - py;

            if (dx != 0 && dy != 0) {
                grid[cy][cx] = (dx > 0 && dy > 0) ? '/' : '\\'; // Diagonal move
                move_description = (dx > 0) ? (dy > 0 ? "northeast" : "southeast") : (dy > 0 ? "northwest" : "southwest");
            } else if (dx != 0) {
                grid[cy][cx] = (dx > 0) ? '>' : '<'; // Horizontal move
                move_description = (dx > 0) ? "east" : "west";
            } else {
                grid[cy][cx] = (dy > 0) ? '^' : 'v'; // Vertical move
                move_description = (dy > 0) ? "north" : "south";
            }

            cout << "Move " << move_description << " from (" << px << ", " << py << ") to (" << cx << ", " << cy << ")\n";

            cx = px;
            cy = py;
        }

        grid[y1][x1] = '&'; // Ensure start is marked - shit
        grid[y2][x2] = '$'; // Ensure end is marked - shit

        drawIllustration();
    }

    void drawIllustration() {
        for (int i = rows - 1; i >= 0; --i) {
            for (int j = 0; j < cols; ++j) {
                cout << grid[i][j] << ' ';
            }
            cout << "\n";
        }
    }
};

int main() {
    int x1, y1, x2, y2, rows, cols;

    cout << "Enter grid size (rows cols): ";
    cin >> rows >> cols;
    cout << "Enter current location (x1 y1): ";
    cin >> x1 >> y1;
    cout << "Enter destination location (x2 y2): ";
    cin >> x2 >> y2;

    cout << "\n";

    PathFinder path(x1, y1, x2, y2, rows, cols);

    // Instance for Obstacles
    path.addObstacle(3, 3);
    path.addObstacle(4, 2);

    path.calculate_steps();

    return 0;
}
