#include <iostream>
#include <vector>
#include <algorithm>
#include <stdio.h>
#include <string>
#include <limits.h>
#include <cstdlib>
#include <assert.h>

const int INFINITY = INT_MAX;

struct Node{
    int value;
    int add = 0;
};

class RangeTree{
public:
    int size;
    //найти ближайшую сверху степень 2
    int getSize(int dataSize) {
        int newSize = 1;
        while (true) {
            if (newSize>=dataSize){
                return newSize;
            }
            assert(newSize < (INT_MAX-1)/2);
            newSize<<=1;
        }
        return 0;
    }
    
    std::vector<Node> tree;
    //конструктор дерева
    explicit RangeTree(std::vector<int> &data){
        size = getSize((int)data.size());
        //нумерация начинается с 1, то есть tree[1] - корень
        tree.resize(2*size);
        
        //заполняем оставшиеся до степени 2 элементы фиктивными
        for (int i = 0; i <= size+1 - data.size(); ++i){
            data.push_back (INFINITY);
        }
        
        //строим листья
        for (int i = 0; i < size; ++i){
            tree[size+i].value = data[i];
        }
        
        //строим узлы с min из значений детей
        for (int j = int(size) - 1; j >= 1; --j){
            tree[j].value = std::min(tree[2*j].value,
                                     tree[2*j + 1].value);
        }
    }

    void push(int nodeIndex){
        tree[2*nodeIndex].add += tree[nodeIndex].add;
        tree[2*nodeIndex + 1].add += tree[nodeIndex].add;
        tree[nodeIndex].add = 0;
    }
                       
    void update(int nodeIndex, int a, int b, int addToRange){
        assert(a <= b);
        int l = nodeIndex*2, r = nodeIndex*2 + 1;
        
        //отрезки не пересекаются
        if ( r < a || l > b){
            return;
        }
        
        //[l,r] вложен в [a,b]
        if (l >= a && r <= b){
            tree[nodeIndex].add += addToRange;
        }
        
        update(l, a, b, addToRange);
        update(r, a, b, addToRange);
        
        
        tree[nodeIndex].value = std::min(tree[l].value + tree[l].add,
                                         tree[r].value + tree[r].add);
        push(nodeIndex);
    }
    
    int rmq (int nodeIndex, int l, int r, int a, int b) {
        if (a > b)
            return INFINITY;
        if (a == l && b == r)
            return tree[nodeIndex].value + tree[nodeIndex].add;
        push(nodeIndex);
        int middle = (l + r) / 2;
        int result = std::min(rmq (nodeIndex*2, l, middle, a, std::min(b,middle)),
                              rmq (nodeIndex*2+1, middle+1, r, std::max(a,middle+1), b));
        tree[nodeIndex].value = std::min(tree[2*nodeIndex].value + tree[2*nodeIndex].add,
                                         tree[2*nodeIndex + 1].value + tree[2*nodeIndex + 1].add);
        return result;
    }
    
    void outTree(){
        for (int i = 1; i < size*2; ++i){
            tree[i].value==INFINITY?(std::cout << "inf "):(std::cout << tree[i].value << " ");
        }
    }
    
};

int main(){
    
    int n, input;
    std::vector<int> data;
    std::cout << "size and numbers:\n";
    std::cin >> n;
    assert(n>0);
    for (int i = 0; i < n; ++i){
        std::cin >> input;
        data.push_back(input);
    }
    
    RangeTree tree(data);
    //tree.outTree();
    std::cout << "\n'm 3 5' MIN[3,5]\n'a 3 5 2' ADD 2 TO [3,5]\n'0' EXIT\n\n";
    int a, b, ans, add;
    char operation;
    while (true){
        std::cin >> operation;
        if (operation == '0')
            break;
        if (operation == 'm'){
            std::cin >> a >> b;
            ans = tree.rmq(1, 1, tree.size, a, b);//nodeIndex, l, r, a, b
            std::cout << ans << "\n";
        }
        if (operation == 'a'){
            std::cin >> a >> b >> add;
            tree.update(1, a, b, add);
        }
    }
    
    return 0;
}
