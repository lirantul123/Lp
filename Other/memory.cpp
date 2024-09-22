#include <iostream>
#include <cstdlib> 

class memory {
public:
    memory() {
        std::cout << "Memory object constructed.\n";
    }
    ~memory() {
        std::cout << "Memory object destructed.\n";
    }

    void check1(int& v) {
        v = 100;
    }
    void check2(int v) {
        v = 100;
    }
};

int main() {
    // Allocate memory for 2 memory objects using malloc
    memory* m = (memory*) malloc(2 * sizeof(memory));  
    
    if (!m) {  
        std::cerr << "Memory allocation failed!\n";
        return 1;
    }
    
    // Manually call the constructors using placement new
    new (&m[0]) memory();  // Placement new for m[0]
    new (&m[1]) memory();  // Placement new for m[1]

    int v = 50;
    // Use dot operator because m[0] and m[1] are objects
    m[0].check1(v);
    std::cout << "After check1: " << v << "\n";  // Output: 100
    
    v = 50;
    m[1].check2(v);
    std::cout << "After check2: " << v << "\n";  // Output: 50

    // Manually call the destructors
    m[0].~memory();
    m[1].~memory();
    
    free(m);

    return 0;
}
