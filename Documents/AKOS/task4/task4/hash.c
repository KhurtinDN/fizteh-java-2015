#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdlib.h>
#include "hash.h"

struct ListNode{
    char* key;
    void* data;
    struct ListNode* next;
    struct ListNode* prev;
};

struct Table{
    int numberOfCells;
    int numberOfElements;
    struct ListNode** cell;
};

struct Table* createTable(int size){
    struct Table* hashTable = (struct Table*)malloc(size*sizeof(struct Table));
    hashTable->cell = (struct ListNode**)malloc(size*sizeof(struct ListNode*));
    hashTable->numberOfCells = size;
    hashTable->numberOfElements = 0;
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


struct Table* hashTableResize(struct Table* oldHashTable);

struct Table* insert(char* Newkey, void* data, struct Table* hashTable){
    hashTable->numberOfElements++;
    if (hashTable->numberOfElements > 2 * hashTable->numberOfCells)
        hashTable = hashTableResize(hashTable);
    char *key = malloc(sizeof(char)*strlen(Newkey));
    memmove(key, Newkey, strlen(Newkey));
    int hashPos = hashFunction(key, hashTable->numberOfCells);
    struct ListNode* newNode = (struct ListNode*)malloc(sizeof(struct ListNode));
    newNode->key = key;
    newNode->data = data;
    
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

struct Table* hashTableResize(struct Table* oldHashTable){
    struct Table* newHashTable = createTable(2*oldHashTable->numberOfCells);
    for (int i = 0; i < oldHashTable->numberOfCells; ++i){
        if (oldHashTable->cell[i] != NULL){
            struct ListNode* currentNode = oldHashTable->cell[i];
            while (currentNode->next != NULL){
                currentNode = currentNode->next;
                insert(currentNode->key, currentNode->data, newHashTable);
                free (currentNode->prev);
            }
            insert(currentNode->key, currentNode->data, newHashTable);
            free (currentNode);
        }
    }
    return newHashTable;
}

int contains(char* key, struct Table* hashTable){
    int hashPos = hashFunction(key, hashTable->numberOfCells);
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

void delete(char* key, struct Table* hashTable){
    int hashPos = hashFunction(key, hashTable->numberOfCells);
    if (contains(key, hashTable) == 0)
        return;
    hashTable->numberOfElements--;
    struct ListNode* currentNode = hashTable->cell[hashPos];
    while(1){
        if (strcmp(currentNode->key, key)==0)
            break;
        currentNode = currentNode->next;
    }
    
    if (currentNode->prev){
        currentNode->prev->next = currentNode->next;
    }else{
        hashTable->cell[hashPos] = currentNode->next;
    }
    if (currentNode->next)
        currentNode->next->prev = currentNode->prev;
    free(currentNode);
    return;
}

void outTable(struct Table* hashTable){
    for (int i = 0; i < hashTable->numberOfCells; ++i){
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

void clearTable(struct Table* hashTable){
    for (int i = 0; i < hashTable->numberOfCells; ++i){
        if (hashTable->cell[i] != NULL){
            struct ListNode* currentNode = hashTable->cell[i];
            while (currentNode->next != NULL){
                currentNode = currentNode->next;
                free (currentNode->prev);
            }
            free (currentNode);
        }else{
            free(hashTable->cell[i]);
        }
    }
}

struct Table* getDataFromConsole(struct Table* hashTable, int number){
    char* key = malloc(sizeof(char)*255);
    printf("Enter keys: ");
    for (int i = 0; i < number; ++i){
        scanf("%s", key);
        hashTable = insert(key, NULL,  hashTable);
    }
    return hashTable;
}
