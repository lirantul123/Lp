#include <iostream>
#include <cmath>
#include <algorithm>

class PathFinder {
private:
    char grid[7][10]; // Private member to store the grid
    int x1, y1, x2, y2; // Private members for coordinates

public:
    PathFinder(int startX, int startY, int endX, int endY) : x1(startX), y1(startY), x2(endX), y2(endY) {
        // Initialize the grid with borders and empty spaces
        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < 10; ++j) {
                if (i == 0 || i == 6 || j == 0 || j == 9) {
                    grid[i][j] = '#';
                } else {
                    grid[i][j] = '.';
                }
            }
        }
        grid[y1][x1] = '&'; // Start position
        grid[y2][x2] = '$'; // End position

        std::cout << "(" << x1 << ", " << y1 << ")" << grid[y1][x1] << "\n";
        std::cout << "(" << x2 << ", " << y2 << ")" << grid[y2][x2] << "\n";
    }

    void drawIllustration(char grid[7][10]) {
        // Print the grid, flipped for 180-degree orientation
        for (int i = 6; i >= 0; --i) {
            for (int j = 0; j < 10; ++j) {
                std::cout << grid[i][j] << ' ';
            }
            std::cout << std::endl;
        }
    }

    void calculate_steps() {
        int dx = x2 - x1;
        int dy = y2 - y1;

        int diagonal_steps = std::min(abs(dx), abs(dy));
        int diagonal_direction_x = (dx > 0) ? 1 : -1;
        int diagonal_direction_y = (dy > 0) ? 1 : -1;

        // Move diagonally and mark on the grid
        for (int i = 0; i < diagonal_steps; ++i) {
            x1 += diagonal_direction_x;
            y1 += diagonal_direction_y;

            if (diagonal_direction_x == 1 && diagonal_direction_y == 1)
                grid[y1][x1] = '/';  // Northeast
            else if (diagonal_direction_x == -1 && diagonal_direction_y == 1)
                grid[y1][x1] = '\\'; // Northwest
            else if (diagonal_direction_x == 1 && diagonal_direction_y == -1)
                grid[y1][x1] = '\\'; // Southeast
            else if (diagonal_direction_x == -1 && diagonal_direction_y == -1)
                grid[y1][x1] = '/';  // Southwest
        }

        // Move horizontally after diagonal movement
        for (int i = 0; i < abs(dx) - diagonal_steps; ++i) {
            x1 += (dx > 0) ? 1 : -1;
            if (grid[y1][x1] != '$') {
                grid[y1][x1] = '-';
            }     
        }

        // Move vertically after horizontal movement
        for (int i = 0; i < abs(dy) - diagonal_steps; ++i) {
            y1 += (dy > 0) ? 1 : -1;
            if (grid[y1][x1] != '$') {
                grid[y1][x1] = '|';
            }
        }

        // Output the grid with the path
        drawIllustration(grid);

        // Output the specific direction
        if (diagonal_steps > 0) {
            std::cout << "Move " << diagonal_steps << " steps ";
            if (diagonal_direction_x == 1 && diagonal_direction_y == 1)
                std::cout << "Northeast\n";
            else if (diagonal_direction_x == -1 && diagonal_direction_y == 1)
                std::cout << "Northwest\n";
            else if (diagonal_direction_x == 1 && diagonal_direction_y == -1)
                std::cout << "Southeast\n";
            else if (diagonal_direction_x == -1 && diagonal_direction_y == -1)
                std::cout << "Southwest\n";
        }

        // Remaining vertical steps
        if (dy > diagonal_steps)
            std::cout << "Move " << dy - diagonal_steps << " steps North\n";
        else if (dy < -diagonal_steps)
            std::cout << "Move " << abs(dy + diagonal_steps) << " steps South\n";

        // Remaining horizontal steps
        if (dx > diagonal_steps)
            std::cout << "Move " << dx - diagonal_steps << " steps East\n";
        else if (dx < -diagonal_steps)
            std::cout << "Move " << abs(dx + diagonal_steps) << " steps West\n";
    }
};


int main() {
    int x1, y1, x2, y2;

    std::cout << "Enter current location (x1 y1): ";
    std::cin >> x1 >> y1;

    std::cout << "Enter destination location (x2 y2): ";
    std::cin >> x2 >> y2;

    try {
        // Validate input to ensure coordinates are within bounds
        if (x1 < 0 || x1 > 9 || y1 < 0 || y1 > 6 || x2 < 0 || x2 > 9 || y2 < 0 || y2 > 6) {
            throw std::out_of_range("Coordinates must be within grid bounds (0-9 for x and 0-6 for y).");
        }

        // Create a PathFinder object
        PathFinder path(x1, y1, x2, y2);
        path.calculate_steps();

    } catch (const std::out_of_range &e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }

    return 0;
}
