#include <iostream>
#include <unordered_map>
#include <vector>
#include <string>
#include <sstream>
#include <algorithm>
#include <cctype>



// TODO: Correct while loop and for loop
// TODO: if function...

class Interpreter {
public:
    static const int IncreDecreValue = 1;

    // Familiar words with the system
    enum class FAMWORDS {
        VAR, PRINT, IF, WHILE, FOR, FUN, CLEAR, EXIT, COMMENT, LEN, PP, MM
    };

    static std::unordered_map<std::string, FAMWORDS> keywordMap;

    static std::unordered_map<std::string, double> intVariables;
    static std::unordered_map<std::string, std::string> stringVariables;
    // [fun_name], [variables, content]
    static std::unordered_map<std::string, std::pair<std::vector<std::string>, std::vector<std::string>>> functions;

    Interpreter() {
        keywordMap = {
            {"var", FAMWORDS::VAR}, {"print", FAMWORDS::PRINT}, {"if", FAMWORDS::IF},
            {"while", FAMWORDS::WHILE}, {"for", FAMWORDS::FOR}, {"fun", FAMWORDS::FUN},
            {"clear", FAMWORDS::CLEAR}, {"exit", FAMWORDS::EXIT}, {"/", FAMWORDS::COMMENT},
            {"len", FAMWORDS::LEN}, {"++", FAMWORDS::PP}, {"--", FAMWORDS::MM}
        };
    }

    void execute() {
        std::string line;
        bool cls = false;
        while (true) {
            if (cls) {
                std::cout << ">> ";
                cls = false;
            }

            std::getline(std::cin, line);
            if (line == "exit") {
                break;
            }
            if (line == "clear") {
                cleanScreen();
                cls = true;
            }
            if (cls)
                continue;

            runCode(line);
            std::cout << ">> ";
        }
    }

    void cleanScreen() {
        std::cout << "\033[H\033[2J";
        std::cout.flush();
    }

    void runCode(const std::string& code) {
        std::istringstream iss(code);
        std::string line;
        while (std::getline(iss, line)) {
            executeLine(line, false);
        }
    }

    static void executeLine(const std::string& line, bool innerFun) {
        if (line.empty()) {
            return;
        }

        std::istringstream iss(line);
        std::vector<std::string> tokens;
        std::string token;
        while (iss >> token) {
            tokens.push_back(token);
        }

        auto it = keywordMap.find(tokens[0]);
        FAMWORDS keyword = it != keywordMap.end() ? it->second : FAMWORDS::VAR;

        if (keyword == FAMWORDS::VAR) {
            executeVarDeclaration(tokens);
        } else if (keyword == FAMWORDS::PRINT) {
            executePrint(tokens);
        } else if (keyword == FAMWORDS::IF) {
            executeIf(tokens);
        } else if (keyword == FAMWORDS::FUN) {
            if (!innerFun) {
                executeFunction(tokens);
            } else {
                std::cout << "\n| 'Syntax Error: Function definition cannot be inside one.' |\n";
            }
        } else if (keyword == FAMWORDS::WHILE) {
            executeWhile(tokens);
        } else if (keyword == FAMWORDS::FOR) {
            executeFor(tokens);
        } else if (keyword == FAMWORDS::COMMENT) {
            // Do nothing for comments
        } else if (keyword == FAMWORDS::LEN) {
            len(tokens, 'l');
            std::cout << " letters";
            len(tokens, 'w');
            std::cout << " words";
        } else {
            std::cout << "Unexpected keyword: " << tokens[0] << std::endl;
        }
    }

    static bool revaluateVariable(const std::vector<std::string>& tokens) {
        if (tokens.size() < 3) {
            std::cout << "Invalid Revaluate Variable statement: " << tokens[0] << std::endl;
            return false;
        }

        std::string varName = tokens[0];
        if (intVariables.find(varName) != intVariables.end()) {
            double currentValue = std::stod(tokens[2]);
            intVariables[varName] = currentValue;
            return true;
        } else if (stringVariables.find(varName) != stringVariables.end()) {
            stringVariables[varName] = tokens[2];
            return true;
        } else {
            std::cout << "Variable not found: " << varName << std::endl;
            return false;
        }
    }

    static void incrementVar(const std::vector<std::string>& tokens) {
        if (tokens.size() < 2) {
            std::cout << "Invalid increment statement: " << tokens[0] << std::endl;
            return;
        }
        std::string varName = tokens[0];

        if (intVariables.find(varName) != intVariables.end()) {
            double currentValue = intVariables[varName];
            intVariables[varName] = currentValue + IncreDecreValue;
        } else {
            std::cout << "Variable not found: " << varName << std::endl;
        }
    }

    static void decrementVar(const std::vector<std::string>& tokens) {
        if (tokens.size() < 2) {
            std::cout << "Invalid decrement statement: " << tokens[0] << std::endl;
            return;
        }
        std::string varName = tokens[0];

        if (intVariables.find(varName) != intVariables.end()) {
            double currentValue = intVariables[varName];
            intVariables[varName] = currentValue - IncreDecreValue;
        } else {
            std::cout << "Variable not found: " << varName << std::endl;
        }
    }

    static void executeWhile(const std::vector<std::string>& tokens) {
        if (tokens.size() < 5 || tokens[2] != "%" || tokens.back() != "$") {
            std::cout << "\n| 'Syntax Error: Expected '%' and '$' for while loop.' |\n";
            return;
        }

        std::string condition = tokens[3];
        std::istringstream iss(condition);
        std::vector<std::string> conditionParts;
        std::string part;
        while (iss >> part) {
            conditionParts.push_back(part);
        }

        if (conditionParts.size() != 3) {
            std::cout << "\n| 'Syntax Error: Invalid condition in while loop.' |\n";
            return;
        }

        std::string body = tokens[4];
        std::istringstream bodyIss(body);
        std::vector<std::string> bodyLines;
        std::string line;
        while (std::getline(bodyIss, line, ';')) {
            bodyLines.push_back(line);
        }

        while (evaluateCondition(conditionParts[0], conditionParts[1], conditionParts[2])) {
            for (const auto& line : bodyLines) {
                executeLine(line, false);
            }
        }
    }

    static bool evaluateCondition(const std::string& leftOperand, const std::string& op, const std::string& rightOperand) {
        double leftValue = getValue(leftOperand);
        double rightValue = getValue(rightOperand);

        if (op == "<") return leftValue < rightValue;
        if (op == ">") return leftValue > rightValue;
        if (op == "==") return leftValue == rightValue;
        if (op == "!=") return leftValue != rightValue;
        if (op == "<=") return leftValue <= rightValue;
        if (op == ">=") return leftValue >= rightValue;

        std::cout << "\n| 'Invalid operator in condition: " << op << "' |\n";
        return false;
    }

    static double getValue(const std::string& operand) {
        if (intVariables.find(operand) != intVariables.end()) {
            return intVariables[operand];
        }
        try {
            return std::stod(operand);
        } catch (const std::invalid_argument&) {
            std::cout << "\n| 'Invalid operand: " << operand << "' |\n";
        }
        return -1;
    }

    static void executeFor(const std::vector<std::string>& tokens) {
        if (tokens.size() < 7 || tokens[1] != "%" || tokens.back() != "$") {
            std::cout << "\n| 'Syntax Error: Expected '%' and '$' for for loop.' |\n";
            return;
        }

        std::string varName = tokens[2];
        int start = std::stoi(tokens[3]);
        int end = std::stoi(tokens[5]);
        std::string body = tokens[7];
        std::istringstream bodyIss(body);
        std::vector<std::string> bodyLines;
        std::string line;
        while (std::getline(bodyIss, line, ';')) {
            bodyLines.push_back(line);
        }

        for (int i = start; i < end; i++) {
            intVariables[varName] = static_cast<double>(i);
            for (const auto& line : bodyLines) {
                executeLine(line, false);
            }
        }
    }

    static void executeExistFunction(const std::vector<std::string>& tokens) {
        std::string functionName = tokens[0];
        if (functions.find(functionName) != functions.end()) {
            executeUserFunction(functionName, tokens);
        } else {
            std::cout << "Invalid statement or function not found: " << functionName << std::endl;
        }
    }

    static void executeUserFunction(const std::string& functionName, const std::vector<std::string>& tokens) {
        bool cannot_run_function = false;

        if (tokens.size() < 3 || tokens[1] != "%" || tokens.back() != "%") {
            std::cout << "\n| 'Syntax Error: Expected '%' to surround the function variables.' |\n";
            return;
        }

        std::vector<std::string> variableTokens;
        for (size_t i = 2; i < tokens.size() - 1; ++i) {
            variableTokens.push_back(tokens[i]);
        }
        std::vector<std::string> providedVariables = variableTokens;

        bool skipBeNum = true;
        for (const auto& var : providedVariables) {
            if (!std::all_of(var.begin(), var.end(), ::isdigit)) {
                skipBeNum = false;
                if (intVariables.find(var) == intVariables.end()) {
                    std::cout << "\n| Variable - '" << var << "' was not set. Therefore, cannot execute - '" << functionName << "' method |\n";
                    cannot_run_function = true;
                }
            }
        }

        auto function = functions[functionName];
        const auto& functionVariables = function.first;
        const auto& functionContent = function.second;

        if (providedVariables.size() != functionVariables.size()) {
            std::cout << "\n| 'Mismatching Argument: The number of provided variables does not match the function's variables.' |\n";
            return;
        }

        std::cout << (cannot_run_function ? "\n| Cannot execute - '" + functionName + "' method ↑ |\n" : executeFunContent(functionName, providedVariables, functionContent));
    }

    static std::string executeFunContent(const std::string& functionName, const std::vector<std::string>& providedVariables, const std::vector<std::string>& functionContent) {
        std::string mess = "Executing function " + functionName + " with variables: " + std::accumulate(providedVariables.begin(), providedVariables.end(), std::string(),
            [](const std::string& a, const std::string& b) { return a + " " + b; });
        for (const auto& content : functionContent) {
            executeLine(content, true);
        }
        return mess;
    }

    static void executeFunction(const std::vector<std::string>& tokens) {
        if (tokens.size() < 3) {
            std::cout << "\n| 'Syntax Error: Expected '%' at position 2 in the token array, but the array is too short.' |\n";
            return;
        }

        std::string functionName = tokens[1];
        std::vector<std::string> functionVariables;
        std::vector<std::string> functionContent;

        if (tokens[2] != "%") {
            std::cout << "\n| 'Syntax Error: Expected '%' at position 2 in the token array.' |\n";
            return;
        }

        size_t i = 3;
        while (i < tokens.size() && tokens[i] != "%") {
            if (!findIfNumberOnly(tokens[i])) {
                std::cout << "\n| 'Mismatching Argument: variables cannot be only numbers' |\n";
            }
            if (tokens[i] != ",")
                functionVariables.push_back(tokens[i]);
            i++;
        }
        if (i >= tokens.size() || tokens[i] != "%") {
            std::cout << "\n| 'Syntax Error: Expected '%' at position " << i << " in the token array.' |\n";
            return;
        }
        i++;
        if (i >= tokens.size() || tokens[i] != "$") {
            std::cout << "\n| 'Syntax Error: Expected '$' after token[" << i << "] in the token array.' |\n";
            return;
        }
        i++;
        std::stringstream contentBuilder;
        while (i < tokens.size() && tokens[i] != "$") {
            contentBuilder << tokens[i] << " ";
            i++;
        }
        if (i >= tokens.size() || tokens[i] != "$") {
            std::cout << "\n\n| 'Syntax Error: Expected closing '$' in the token array.' |\n";
            return;
        }
        std::string content = contentBuilder.str();
        std::istringstream contentIss(content);
        std::string command;
        while (std::getline(contentIss, command, ';')) {
            if (!command.empty()) {
                functionContent.push_back(command);
            }
        }
        functions[functionName] = std::make_pair(functionVariables, functionContent);
    }

    static bool findIfNumberOnly(const std::string& token) {
        return !std::all_of(token.begin(), token.end(), ::isdigit);
    }

    static void executeVarDeclaration(const std::vector<std::string>& tokens) {
        if (tokens.size() < 4 || tokens[2] != "=") {
            std::cout << "Invalid variable declaration: " << tokens[0] << std::endl;
            return;
        }

        std::string varName = tokens[1];
        if (intVariables.find(varName) != intVariables.end() || stringVariables.find(varName) != stringVariables.end()) {
            std::cout << "Variable already exists! Sorry but you cannot redeclare." << std::endl;
            return;
        }

        if (tokens[3] == "'") {
            std::string varStringValue;
            for (size_t i = 4; i < tokens.size(); ++i) {
                if (tokens[i] == "'")
                    break;
                varStringValue += tokens[i] + " ";
            }
            stringVariables[varName] = varStringValue;
        } else {
            try {
                double varValue = std::stod(tokens[3]);
                intVariables[varName] = varValue;
            } catch (const std::invalid_argument&) {
                std::cout << "Invalid variable value: " << tokens[3] << std::endl;
            }
        }
    }

    static void executePrint(const std::vector<std::string>& tokens) {
        if (tokens.size() < 2) {
            std::cout << "Invalid print statement: " << tokens[0] << std::endl;
            return;
        }
        std::string varName = tokens[1];

        if (intVariables.find(varName) != intVariables.end()) {
            double varValue = intVariables[varName];
            if (tokens.size() > 2 && isMathExpression(tokens)) {
                double result = evaluateMathExpression(tokens, varValue);
                std::cout << result << std::endl;
            } else {
                std::cout << varValue << std::endl;
            }
        } else if (stringVariables.find(varName) != stringVariables.end()) {
            std::string varValue = stringVariables[varName];
            std::cout << varValue << std::endl;
        } else {
            if (!isNum(tokens[1])) {
                std::cout << "smt- we support only numbers for now: " << varName << std::endl;
            } else {
                if (tokens.size() > 2 && isMathExpression(tokens)) {
                    double num = strToDouble(tokens[1]);
                    double result = evaluateMathExpression(tokens, num);
                    std::cout << result << std::endl;
                } else {
                    std::cout << tokens[1] << std::endl;
                }
            }
        }
    }

    static double strToDouble(const std::string& str) {
        return std::stod(str);
    }

    static bool isNum(const std::string& value) {
        try {
            std::stod(value);
            return true;
        } catch (const std::invalid_argument&) {
            return false;
        }
    }

    static void executeIf(const std::vector<std::string>& tokens) {
        if (tokens.size() < 3) {
            std::cout << "Invalid if statement: " << tokens[0] << std::endl;
            return;
        }

        std::string operatorStr = tokens[1];
        int operand1;
        int operand2;

        try {
            operand1 = std::stoi(tokens[0]);
            operand2 = std::stoi(tokens[2]);
        } catch (const std::invalid_argument&) {
            std::cout << "Invalid operands for operation: " << tokens[0] << std::endl;
            return;
        }

        // Acceptable: if x + 3 - 1 > 5 * a // $ $
        // String varName = tokens[1];
        // double num;
        // if (intVariables.find(varName) != intVariables.end()) {
        //     int varValue = intVariables[varName];
        //     if (tokens.size() > 2 && isMathExpression(tokens)) {
        //         double result = evaluateMathExpression(tokens, varValue);
        //         std::cout << result << std::endl;
        //     } else {
        //         std::cout << varValue << std::endl;
        //     }
        // } else if (stringVariables.find(varName) != stringVariables.end()) {
        //     std::string varValue = stringVariables[varName];
        //     std::cout << varValue << std::endl;
        // } else {
        //     if (!isNum(tokens[1])) {
        //         std::cout << "smt- we support only numbers for now: " << varName << std::endl;
        //     } else {
        //         if (tokens.size() > 2 && isMathExpression(tokens)) {
        //             double num = strToDouble(tokens[1]);
        //             double result = evaluateMathExpression(tokens, num);
        //             std::cout << result << std::endl;
        //         } else {
        //             std::cout << tokens[1] << std::endl;
        //         }
        //     }
        // }
    }

    static bool isMathExpression(const std::vector<std::string>& tokens) {
        // Implement math expression evaluation logic here
        return false;
    }

    static double evaluateMathExpression(const std::vector<std::string>& tokens, double initialValue) {
        // Implement math expression evaluation logic here
        return initialValue;
    }

    static void len(const std::vector<std::string>& tokens, char type) {
        // Implement length calculation logic here
    }
};

int main() {
    Interpreter interpreter;
    interpreter.execute();
    return 0;
}