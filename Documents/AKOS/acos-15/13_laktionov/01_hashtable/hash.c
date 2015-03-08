#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdlib.h>

struct ListNode{
    char* key;
    void* data;
    struct ListNode* next;
    struct ListNode* prev;
};

struct Table{
    int size;
    struct ListNode** cell;
};

struct Table* createTable(int size){
    struct Table* hashTable = (struct Table*)malloc(size*sizeof(struct Table));
    hashTable->cell = (struct ListNode**)malloc(size*sizeof(struct ListNode*));
    hashTable->size = size;
    for (int i = 0; i < size; ++i)
        hashTable->cell[i] = NULL;
    return hashTable;
}

int hashFunction(char* key, int size){
    int hashSum = 0;
    for (int i = 0; i < strlen(key); ++i){
        hashSum += key[i];
    }
    return (hashSum % size);
}

struct Table* insert(char* Newkey, struct Table* hashTable){
    char *key;
    key = malloc(sizeof(*Newkey*255));
    //*key = *Newkey;//*(Newkey);
    memmove(key, Newkey, strlen(Newkey));
    int hashPos = hashFunction(key, hashTable->size);
    struct ListNode* newNode = (struct ListNode*)malloc(sizeof(struct ListNode));
    newNode->key = key;
    
    if (hashTable->cell[hashPos] == NULL){
        newNode->prev = NULL;
        newNode->next = NULL;
        hashTable->cell[hashPos] = newNode;
    }else{
        struct ListNode* currentNode = hashTable->cell[hashPos];
        while(currentNode->next != NULL)
            currentNode = currentNode->next;
        newNode->prev = currentNode;
        newNode->next = NULL;
        currentNode->next = newNode;
    }
    return hashTable;
}

int contains(char* key, struct Table* hashTable){
    int hashPos = hashFunction(key, hashTable->size);
    if (hashTable->cell[hashPos] == NULL)
        return 0;
    struct ListNode* currentNode = hashTable->cell[hashPos];
    while (currentNode->next != NULL){
        if (strcmp(currentNode->key, key)==0)//ключи совпадают
            return 1;
        currentNode = currentNode->next;
    }
    if (strcmp(currentNode->key, key)==0)
        return 1;
    return 0;
}

void deleteList(struct ListNode* currentNode){
    free (currentNode);
    currentNode = NULL;
}

struct Table* delete(char* key, struct Table* hashTable){
    int hashPos = hashFunction(key, hashTable->size);
    if (contains(key, hashTable)==0)
        return hashTable;
    struct ListNode* currentNode = hashTable->cell[hashPos];
    while(1){
        if (strcmp(currentNode->key, key)==0)
            break;
        currentNode = currentNode->next;
    }
    if (currentNode->prev == NULL){ //удаляется первый
        if (currentNode->next != NULL){
            currentNode->next->prev = NULL;
            hashTable->cell[hashPos] = currentNode->next;
        }else{
            hashTable->cell[hashPos] = NULL;
        }
        deleteList(currentNode);
        return hashTable;
    }
    if (currentNode->next == NULL){ //удаляется последний
        currentNode->prev->next = NULL;
        deleteList(currentNode);
        return hashTable;
    }
    currentNode->prev->next = currentNode->next;
    currentNode->next->prev = currentNode->prev;
    deleteList(currentNode);
    return hashTable;
}

void outTable(struct Table* hashTable){
    for (int i = 0; i < hashTable->size; ++i){
        if (hashTable->cell[i] != NULL){
            struct ListNode* currentNode = hashTable->cell[i];
            printf("cell %d: ", i);
            printf("\n");
            puts(currentNode->key);
            while (currentNode->next != NULL){
                currentNode = currentNode->next;
                puts(currentNode->key);
            }
        }
    }
}

struct Table* clearTable(struct Table* hashTable){
    for (int i = 0; i < hashTable->size; ++i){
        if (hashTable->cell[i] != NULL){
            struct ListNode* currentNode = hashTable->cell[i];
            while (currentNode->next != NULL){
                currentNode = currentNode->next;
                deleteList (currentNode->prev);
            }
            deleteList (currentNode);
            hashTable->cell[i] = NULL;
        }
    }
    return hashTable;
}

struct Table* getDataFromConsole(struct Table* hashTable, int number){
    char* key = malloc(sizeof(char)*255);
    printf("Enter keys: ");
    for (int i = 0; i < number; ++i){
        scanf("%s", key);
        hashTable = insert(key, hashTable);
    }
    return hashTable;
}
