#include <iostream>
#include <string>

int minOperationCost(const std::string &str, int x, int y)
{
    // x = removes two identical characters(cost)
    // y = removes two different characters(cost)
    // hehl -> x = 1, y = 1, return x + y = 2

    int n = str.size();
    if (n % 2 != 0)
        return -1;
    int i = 0, j = n - 1;
    int cost = 0;

    while (i < j) {
        if (str[i] == str[j]) {
            cost += x; 
        } else {
            cost += y; 
        }
        i++;
        j--;
    }

    return cost;
}

int main()
{
    std::string str = "alea";
    int x = 1, y = 1;
    std::cout << "Minimum cost: " << minOperationCost(str, x, y) << std::endl;
    return 0;
}
