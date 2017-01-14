struct Table* createTable(int size);

int hashFunction(char* key, int size);

struct Table* insert(char* Newkey, struct Table* hashTable);

char contains(char* key, struct Table* hashTable);

struct Table* delete(char* key, struct Table* hashTable);

void outTable(struct Table* hashTable);

void deleteList(struct ListNode* currentNode);

struct Table* clearTable(struct Table* hashTable);

struct Table* getDataFromConsole(struct Table* hashTable, int number);
