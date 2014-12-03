#include <iostream>
#include <vector>
#include <algorithm>
#include <iterator>
#include <random>
#include <cmath>
#include <cstdlib>
#include <ctime>

const int PRIME = 2000000011;

void fillArray(std::vector<int> &numbers, const int &length){
    srand(int(time(NULL)));
    for (int iter = 0; iter < length; ++iter){
        int x = rand() % (PRIME - 1000);
       // int x = rand() % 1000000;
        numbers.push_back(x);
    }
}

class FixedSet {
public:
    std::vector<std::vector<int>> hashTable;
    std::vector<int> tempFrom;
    int hash_pos, pos, first_hash_size_sum, Size, sizeOfSet;
    bool error;
    struct ab{
        int a;
        int b;
        int size;
    };
    ab first_hash, second_hash;
    std::vector<ab> second_hashes;
    
    int getRand(){
        //srand(int(time(NULL)));
        return (rand() % (PRIME-12));
    }
    int hashCount(const int a, const int b, const int n, const int key){
        int x = abs(((a * key + b) % PRIME) % n);
        return (x);
    }
    void fillWithNull(std::vector<int> &v){
        for (int iter = 0; iter != v.size(); ++iter){
            v[iter] = PRIME;
        }
    }
    void outTable(){
        for (int iter1 = 0; iter1 != hashTable.size(); ++iter1){
            for (auto iter2 = hashTable[iter1].begin(); iter2 != hashTable[iter1].end(); ++iter2){
                if (*iter2 == PRIME){
                    std::cout << "null ";
                }else{
                    std::cout << *iter2 << " ";
                }
            }
            std::cout << ";\n";
        }
    }
    explicit FixedSet(std::vector<int> &numbers){
        sizeOfSet = int(numbers.size());
        do{
            hashTable.clear();
            hashTable.resize(numbers.size());
            for (int iter = 0; iter != numbers.size(); ++iter){
                hashTable[iter].clear();
            }
            first_hash_size_sum = 0;
            first_hash.a = getRand()+1;
            first_hash.b = getRand();
            for (int iter = 0; iter != numbers.size(); ++iter){
                hash_pos = hashCount(first_hash.a, first_hash.b, int(numbers.size()), numbers[iter]);
                hashTable[hash_pos].push_back(numbers[iter]);
            }
            for (int iter = 0; iter != numbers.size(); ++iter){
                first_hash_size_sum += hashTable[iter].size()*hashTable[iter].size();
            }
        }while(first_hash_size_sum >= 3*numbers.size());
        for (int iter = 0; iter != numbers.size(); ++iter){
            tempFrom = hashTable[iter];
            Size = int(hashTable[iter].size());
            do{
                error = 0;
                hashTable[iter].clear();
                hashTable[iter].resize(Size*Size);
                fillWithNull(hashTable[iter]);
                second_hash.a = getRand()+1;
                second_hash.b = getRand();
                for (pos = 0; pos != tempFrom.size(); ++pos){
                    hash_pos = hashCount(second_hash.a, second_hash.b, int(tempFrom.size()*tempFrom.size()), tempFrom[pos]);
                    if (hashTable[iter][hash_pos] != PRIME){
                        error = 1;
                    };
                    hashTable[iter][hash_pos] = tempFrom[pos];
                };
            }while (error);
            second_hash.size = int(hashTable[iter].size());
            second_hashes.push_back(second_hash);
        }
    };
    bool Contains(const int &number){
        int i = hashCount(first_hash.a, first_hash.b, sizeOfSet, number);
        int j = hashCount(second_hashes[i].a, second_hashes[i].b, second_hashes[i].size, number);
        if (hashTable[i][j] == number){
            return 1;
        }else {
            return 0;
        }
    }
};

int main() {
    
    int length, number;;
    std::cin >> length;
    std::cout << "\n";
    std::vector<int> numbers;
    fillArray(numbers, length);
    FixedSet hashTable(numbers);
    hashTable.outTable();
    std::cout << "\n";
    std::cin >> number;
    std::cout << "\n" << hashTable.Contains(number) << "\n";
    
    return 0;
}
