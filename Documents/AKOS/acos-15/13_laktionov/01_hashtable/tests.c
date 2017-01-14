#include "hash.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdlib.h>

int main() {
    printf("Enter the size of table and the number of keys: ");
    int n, k;
    scanf("%d %d", &n, &k);
    struct Table* hashTable = createTable(n);
    getDataFromConsole(hashTable, k);
    
    printf("Here is the hash table: \n--------------\n");
    outTable(hashTable);
    printf("--------------\n");
    
    printf("Contains - ask something (print EXIT to end)\n");
    char* key = malloc(sizeof(char)*255);
    while(1){
        scanf("%s", key);
        if (strcmp(key, "EXIT")==0)
            break;
        if (contains(key, hashTable))
            printf("Yes\n");
        else
            printf("No\n");
    }

    printf("Delete - ask something (print EXIT to end)\n");
    while(1){
        scanf("%s", key);
        if (strcmp(key, "EXIT")==0)
            break;
        hashTable = delete(key, hashTable);
    }
    
    printf("Here is hash table after deletion: \n--------------\n");
    outTable(hashTable);
    printf("--------------\n");
    
    printf("Let's clear it\n");
    hashTable = clearTable(hashTable);
    
    printf("Here is the hash table after clearing (must be empty): \n--------------\n");
    outTable(hashTable);
    printf("--------------\n");
    
    for (int i = 0; i < k*10; ++i){
        key = i%2?"a":"b";
        hashTable = insert(key, hashTable);
    }
    
    printf("Here is hash table after attack: \n--------------\n");
    outTable(hashTable);
    printf("--------------\n");
    
    hashTable = clearTable(hashTable);
    
    return 0;
}