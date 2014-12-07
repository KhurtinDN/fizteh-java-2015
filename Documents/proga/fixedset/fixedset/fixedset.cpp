//  2014

#include <iostream>
#include <vector>
#include <algorithm>
#include <iterator>
#include <random>
#include <cmath>
#include <cstdlib>

const int PRIME = 2000000011, SHIFT = 1000000000;

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
    __int64_t hashCount(const int a, const int b, const int n, const int key){
        __int64_t x = ((a * key + b) % PRIME) % n;
        if (x < 0)
            x*= -1;
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
                hash_pos = int(hashCount(first_hash.a, first_hash.b, int(numbers.size()), numbers[iter]));
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
                    hash_pos = int(hashCount(second_hash.a, second_hash.b, std::max(1,int(tempFrom.size()*tempFrom.size())), tempFrom[pos]));
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
        int i = int(hashCount(first_hash.a, first_hash.b, std::max(1,sizeOfSet), number));
        int j = int(hashCount(second_hashes[i].a, second_hashes[i].b, std::max(second_hashes[i].size,1), number));
        if (hashTable[i].size() != 0){
            if (hashTable[i][j] == number){
                return 1;
            }else {
                return 0;
            }
        }else{
            return 0;
        }
    }
};

int main() {
    int number;
    size_t length, i;
    std::cin >> length;
    std::cout << "\n";
    std::vector<int> numbers;
    for (i = 0; i < length; ++i){
        std::cin >> number;
        number += SHIFT;
        numbers.push_back(number);
    }
    
    FixedSet hashTable(numbers);
    

    std::cin >> length;
    for (i = 0; i < length; ++i){
        std::cin >> number;
        number += SHIFT;
        if (hashTable.Contains(number) == 1){
            std::cout << "Yes\n";
        }else{
            std::cout << "No\n";
        }
    }
    return 0;
}
