#include <iostream>
#include <vector>

const int infinity = 1000000001;

int main() {
    int numberOfFloors, numberOfRooms, inputCost;
    
    std::cin >> numberOfFloors >> numberOfRooms;
    std::vector<std::vector<int>> inputNumbers(numberOfFloors), minWaysTable(numberOfFloors);
    
    for (ptrdiff_t row = 0; row < numberOfFloors; ++row){
        inputNumbers[row].reserve(numberOfRooms+2);
        minWaysTable[row].resize(numberOfRooms+2);
        minWaysTable[row][0] = infinity;
        minWaysTable[row][numberOfRooms+1] = infinity;
        for (ptrdiff_t inputColumn = 0; inputColumn < numberOfRooms; ++inputColumn){
            std::cin >> inputCost;
            inputNumbers[row].push_back(inputCost);
        }
    }
    
    std::copy(inputNumbers[inputNumbers.size()-1].begin(), inputNumbers[inputNumbers.size()-1].end(), minWaysTable[inputNumbers.size()-1].begin()+1); //копируем первый этаж

    for (ptrdiff_t row = numberOfFloors-2; row >= 1; --row){
        for (ptrdiff_t wayColumn = 1; wayColumn < numberOfRooms + 1; ++wayColumn){
            ptrdiff_t inputColumn = wayColumn - 1;
            minWaysTable[row][wayColumn] = inputNumbers[row][inputColumn] + std::min(minWaysTable[row+1][wayColumn], minWaysTable[row][wayColumn-1]);
        }
        for (ptrdiff_t wayColumn = numberOfRooms; wayColumn >= 1; --wayColumn){
            ptrdiff_t inputColumn = wayColumn - 1;
            int compareTemp = inputNumbers[row][inputColumn] + std::min(minWaysTable[row+1][wayColumn], minWaysTable[row][wayColumn+1]);
            minWaysTable[row][wayColumn] = std::min(compareTemp, minWaysTable[row][wayColumn]);
        }
    }
    
    int minimal;
    ptrdiff_t minimalIndex;
    std::vector<ptrdiff_t> steps;
    
    if (numberOfFloors > 1){
        minimal = minWaysTable[1][1] + inputNumbers[0][0];
        minimalIndex = 1;
        
        for (ptrdiff_t wayColumn = 1; wayColumn < numberOfRooms + 1; ++wayColumn){
            ptrdiff_t inputColumn = wayColumn - 1;
            minWaysTable[0][wayColumn] = inputNumbers[0][inputColumn] + minWaysTable[1][wayColumn];
            if (minWaysTable[0][wayColumn] < minimal){
                minimal = minWaysTable[0][wayColumn];
                minimalIndex = wayColumn;
            }
        }
        
        steps.push_back(minimalIndex);
        ptrdiff_t wayColumn = minimalIndex;
        
        int minPrev;
        
        for (ptrdiff_t row = 0; row < numberOfFloors-1; ++row){
            while(1){
                minPrev = std::min(std::min(minWaysTable[row][wayColumn-1], minWaysTable[row][wayColumn+1]), minWaysTable[row+1][wayColumn]);
                if (minPrev == minWaysTable[row][wayColumn-1]){
                    --wayColumn;
                    steps.push_back(wayColumn);
                }else{
                    if (minPrev == minWaysTable[row][wayColumn+1]){
                        ++wayColumn;
                        steps.push_back(wayColumn);
                    }else{
                        steps.push_back(wayColumn);
                        break;
                    }
                }
            }
        }
        
    }else{
        minimalIndex = 0;
        minimal = inputNumbers[0][0];
        for (ptrdiff_t row = 1; row < numberOfRooms; ++row){
            if (inputNumbers[0][row] < minimal){
                minimal = inputNumbers[0][row];
                minimalIndex = row;
            }
        }
        steps.push_back(minimalIndex + 1);
    }
    
    
    
    for (size_t iter = 0; iter != steps.size(); ++iter){
        std::cout << steps[iter] << " ";
    }
    
    return 0;
}