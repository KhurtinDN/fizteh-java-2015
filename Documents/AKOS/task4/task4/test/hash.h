struct Listnode {
    char* key;
    struct Listnode* nextnode;
};

struct Hashtable {
    int elementsnumber;
    int numberoflists;
    struct Listnode** Tablelists;
};



struct Hashtable* Createtable(int numberoflists);
int Hashcalculate(char* ourstring, int numberoflists);
void Insert(struct Hashtable** ourtable, char* ourstring);
int Search(struct Hashtable* ourtable, char* ourstring);
void Memoryclean(struct Hashtable* ourtable);
void Fullmemoryclean(struct Hashtable* ourtable);
void Realloctable(struct Hashtable** left, int newsize);
void Check(struct Hashtable* ourtable);
