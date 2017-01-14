#include <iostream>
#include <vector>
#include <algorithm>
#include <iterator>
#include <random>
#include <cmath>
#include <cstdlib>
#include <ctime>
#include <limits.h>
#include <queue>

struct node{
    int key;
    int height;
    int weight;
    node* left;
    node* right;
    node* parent;
    node(int k, node* p){
        key = k;
        parent = p;
        left = nullptr;
        right = nullptr;
        height = 1;
        weight = 1;
    }
};

class AVL_tree{
    
public:
    node* p = nullptr;
    node* inserted;
    node* deleted;
    node* r;
    
    int kth;
    
    std::queue<node*> Q;
    
    int height(node* p){
        if (p)
            return p->height;
        else
            return 0;
    }
    
    int weight(node* p){
        if (p)
            return p->weight;
        else
            return 0;
    }
    
    int diff(node *p){
        if (p==nullptr)
            return 0;
        else
            return height(p->left)-height(p->right);
    }

    void fixheight(node* p){
        if (p==nullptr) return;
        int hl = height(p->left);
        int hr = height(p->right);
        if (hl > hr){
            p->height = hl + 1;
        }else{
            p->height = hr + 1;
        }
        p->weight = weight(p->left) + weight(p->right) + 1;
    }

    void outTree(node* p){
        if (p!=nullptr){
            std::cout<< p->key;
            if (p->left!=nullptr){
                std::cout << "; left: " << p->left->key;
            }
            if (p->right!=nullptr){
                std::cout << "; right: " << p->right->key;
            }

            std::cout << "\n";
            outTree(p->left);
            outTree(p->right);
        }
    }

    node* rotateleft(node* p){
        if (!p) return p;
        if (!(p->right)) return p;
        node* q = p->right;
        node* B = q->left;
        if (p->parent){
            node* T = p->parent;
            if (p==T->left) T->left = q;
            if (p==T->right) T->right = q;
            q->parent = T;
        }
        p->right = B;
        if (B) B->parent = p;
        q->left = p;
        q->parent = p->parent;
        p->parent = q;
        fixheight(p);
        fixheight(q);
        return q;
    }

    node* rotateright(node* p){
        if (!p) return p;
        if (!(p->left)) return p;
        node* q = p->left;
        node* B = q->right;
        if (p->parent){
            node* T = p->parent;
            if (p==T->left) T->left = q;
            if (p==T->right) T->right = q;
            q->parent = T;
        }
        p->left = B;
        if (B) B->parent = p;
        q->right = p;
        q->parent = p->parent;
        p->parent = q;
        fixheight(p);
        fixheight(q);
        return q;
    }

    node* search(node* p, int key){
        if (p == nullptr) return p;
        if (p->key == key) return p;
        if (p->key > key)
            return search(p->left, key);
                else
                    return search(p->right, key);
    }

    node* balance(node* &p){
        if (p==nullptr)
            return p;
        fixheight(p);
        node* q = p->right;
        if ( (diff(p)==-2 && (diff(q)==0 || diff(q)==-1)) ){
            return rotateleft(p);
        }
        q = p->left;
        if ( (diff(p)==2 && (diff(q)==0 || diff(q)==1)) ){
            return rotateright(p);
        }
        q = p->left;
        if ( (diff(p)==2 && diff(q)==1 ) ){
            p->left =  rotateleft(p->left);
            return rotateright(p);
        }
        q = p->right;
        if ( (diff(p)==-2 && diff(q)==-1) ){
            p->right = rotateright(p->right);
            return rotateleft(p);
        }
        return p;
    }

    node* insert(node* &p, int k){
        if (!p){
            inserted = new node(k, nullptr);
            return inserted;
        }
        if (k < p->key){
            if (!p->left){
                p->left = new node(k, p);
                inserted = p->left;
            }else{
                p->left = insert(p->left, k);
            }
        }else{
            if (!p->right){
                p->right = new node(k, p);
                inserted = p->right;
            }else{
                p->right = insert(p->right, k);
            }
        }
        fixheight(p);
        if (diff(p) == 2){
            if (k < p->left->key){
                return rotateright(p);
            }else{
                p->left = rotateleft(p->left);
                return rotateright(p);
            }
        }else{
            if (diff(p) == -2){
                if (k >= p->right->key){
                    return rotateleft(p);
                }else{
                    p->right = rotateright(p->right);
                    return rotateleft(p);
                }
            }
        }
        return p;
    }

    node* minimal(node* p){
        if (p->left){
            return minimal(p->left);
        }else{
            return p;
        }
    }

    std::string LorRson(node* p){
        if (!p)
            return "nullptr";
        if (!p->parent)
            return "nullptr";
        if (p->parent->left == p)
            return "left";
        return "right";
    }

    node* AVL_delete(node* &p, node* &deleted){
        node* balancingNode;
        node* nextNode;
        if (!deleted || !p){
            return p;
        }
        
        if (deleted == p && !p->left && !p->right){
            return nullptr;
        }
        
        if (!deleted->left && !deleted->right){             //deleted is leaf
            
            if (!deleted->parent)
                return nullptr;
            if (LorRson(deleted)=="left"){
                deleted->parent->left = nullptr;
                balancingNode = deleted->parent;
            }
            else{
                deleted->parent->right = nullptr;
                balancingNode = deleted->parent;
            }
            
        }else if(!deleted->left && deleted->right){         //deleted has only right child
            
            if (!deleted->parent){
                deleted->right->parent=nullptr;
                return deleted->right;
            }
            
            deleted->right->parent = deleted->parent;
            if (LorRson(deleted)=="left")
                deleted->parent->left = deleted->right;
            else
                deleted->parent->right = deleted->right;
            balancingNode = deleted->right;
            
        }else if(!deleted->right && deleted->left){         //deleted has only left child
            if (!deleted->parent){
                deleted->left->parent=nullptr;
                return deleted->left;
            }
            deleted->left->parent = deleted->parent;
            if (LorRson(deleted)=="left")
                deleted->parent->left = deleted->left;
            else
                deleted->parent->right = deleted->left;
            balancingNode = deleted->left;
            
        }else{                                              //deleted has both children
            
            nextNode = minimal(deleted->right);
            if (!nextNode->right){
                
                balancingNode = nextNode->parent;
                if (balancingNode == deleted)
                    balancingNode = deleted->left;
                
                if (nextNode == deleted->right){
                    deleted->left->parent = nextNode;
                    nextNode->left = deleted->left;
                    if (deleted->parent){
                        if (LorRson(deleted) == "left"){
                            deleted->parent->left = nextNode;
                        }else{
                            deleted->parent->right = nextNode;
                        }
                        nextNode->parent = deleted->parent;
                    }
                }else{
                    
                    if (LorRson(nextNode) == "left"){
                        nextNode->parent->left = nullptr;
                    }else{
                        nextNode->parent->right = nullptr;
                    }

                    if (deleted->parent){
                        if (LorRson(deleted) == "left"){
                            deleted->parent->left = nextNode;
                        }else{
                            deleted->parent->right = nextNode;
                        }
                        nextNode->parent = deleted->parent;
                    }
                    
                    if (!deleted->parent)
                        nextNode->parent = nullptr;
                    
                    deleted->right->parent = nextNode;
                    deleted->left->parent = nextNode;
                    nextNode->left = deleted->left;
                    nextNode->right = deleted->right;
                }
            }else{
                
                balancingNode = nextNode->right;
                
                if(nextNode == deleted->right){
                    
                    if (deleted->parent){
                        if (LorRson(deleted) == "left"){
                            deleted->parent->left = nextNode;
                        }else{
                            deleted->parent->right = nextNode;
                        }
                        nextNode->parent = deleted->parent;
                    }
                    if (!deleted->parent)
                        nextNode->parent = nullptr;
                    nextNode->left = deleted->left;
                    deleted->left->parent = nextNode;
                    
                }else{
                    if (LorRson(nextNode) == "left"){
                        nextNode->parent->left = nextNode->right;
                    }else{
                        nextNode->parent->right = nextNode->right;
                    }
                
                    nextNode->right->parent = nextNode->parent;
                
                    if (deleted->parent){
                        if (LorRson(deleted) == "left"){
                            deleted->parent->left = nextNode;
                        }else{
                            deleted->parent->right = nextNode;
                        }
                        nextNode->parent = deleted->parent;
                    }
                
                    if (!deleted->parent)
                        nextNode->parent = nullptr;
                
                    deleted->right->parent = nextNode;
                    deleted->left->parent = nextNode;
                    nextNode->left = deleted->left;
                    nextNode->right = deleted->right;
                }
            
            }
        }
        
        
        while(balancingNode->parent){                 //балансируем, поднимаясь до корня
            balancingNode = balance(balancingNode);
            balancingNode = balancingNode->parent;
        }
        
        if (!balancingNode->parent){
            balancingNode = balance(balancingNode);
        }
        
        return balancingNode;
        
    }

    node* GetElementByIndex(node* p, size_t index){
        if (Q.size() < index)
            return nullptr;
        size_t thisNodeOrder;
        if (!p)
            return nullptr;
        if (p->left){
            thisNodeOrder = p->left->weight + 1;
        }else{
            thisNodeOrder = 1;
        }
        
        if (index == thisNodeOrder)
            return p;
        if (index < thisNodeOrder){
            return GetElementByIndex(p->left, index);
        }else{
            return GetElementByIndex(p->right, index - thisNodeOrder);
        }
    }
    
    void AVL_push(const int &x){
        if (!p)
            std::cout << "\n";
        p = insert(p, x);
        Q.push(inserted);
        r = GetElementByIndex(p, kth);
        if (r){
            std::cout << r->key << "\n"; // << "; PUSH " << x;
        }else{
            std::cout << "NONE\n"; // PUSH " << x;
        }
//        std::cout << "\nprint:\n";
//        outTree(p);
//        std::cout << "--------\n";
    }
    
    void AVL_pop(){
        deleted = Q.front();
        Q.pop();
        p = AVL_delete(p, deleted);
        node* r = GetElementByIndex(p, kth);
        if (r){
            std::cout << r->key << "\n";// << "; POP";
        }else{
            std::cout << "NONE\n";// POP";
        }
//        std::cout << "\nprint:\n";
//        outTree(p);
//        std::cout << "--------\n";

    }
    
    void AVL_set_k(const int &k){
        kth = k;
    }
};

int main(){
    int x, k;
    std::string input;
    AVL_tree TREE;
    std::cin >> k;
    TREE.AVL_set_k(k);
    while(true){
        std::cin >> input;
        if (input == "STOP"){
            return 0;
        }
        if (input == "PUSH"){
            std::cin >> x;
            TREE.AVL_push(x);
            //std::cout << "\n";
            //outTree(p);
            //std::cout << "\n\n";

        }
        if (input == "POP"){
            TREE.AVL_pop();
        }
    }
//    node* deleted;
//    std::cin >> n;
//    for (int i = 0; i < n; ++i){
//        std::cin >> x;
//        p = insert(p, x);
//    }
//    std::cout << "\n";
//    outTree(p);
//    std::cout << "\n\n";
//    std::cin >> kth;
//    node* found = GetElementByIndex(p, kth);
//    std::cout << "\n" << found->key;
////    int k;
////    
////    while(1){
////        std::cin >> k;
////        deleted = search(p, k);
////        p = AVL_delete(p, deleted);
////        std::cout << "\n";
////        outTree(p);
////        std::cout << "\n\n";
////    }
    return 0;
}