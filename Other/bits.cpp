#include <iostream>

std::string toBinaryString(int number) {
    std::string binary = "";
    while (number > 0) {
        binary = (number % 2 == 0 ? "0" : "1") + binary;
        number /= 2;
    }
    return binary.empty() ? "0" : "000" + binary; 
}

int toBinaryInt(std::string binary) {
    int num = 0;
    int place = 1;
    for (int i = binary.size()-1; i > 2 ; i--)
    {
        num += (binary[i] == '1' ? place : 0);
        place *= 2;
    }
    return num;
}

int main() {
    int x = 7;
    std::cout << "In bit: " << toBinaryString(x) << "\n";
    std::cout << "In regular: " << toBinaryInt(toBinaryString(x)) << "\n";

    return 0;
}