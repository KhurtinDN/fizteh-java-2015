#include <unistd.h>
#include <stdio.h>
#include <dirent.h>
#include <fcntl.h>
#include <stdlib.h>
#include <limits.h>
#include <errno.h>
#include <string.h>
#include <sys/stat.h>

void countWords(FILE* file, char* filepath){
    int counter = 0, flag = 1;
    char c;
    while (1){
        c = getc(file);
        if (c==EOF)
            break;
        if ( (c>='a' && c<='z') || (c>='A' && c<='Z') || (c=='_') || (c>='0' && c<='9') ){
            if (flag == 0)
                counter++;
            flag = 1;
        }else
            flag = 0;
    }
    printf("%s: %d\n", filepath, counter+1);
    fclose(file);
}

char** visited_files;
int vis_counter = 0, c = 0;

int isVisited(char* file){// 0 -> added, 1 -> already visited
    if (!c)
        return 0;
    if (vis_counter==0)
        visited_files = (char**)malloc(sizeof(char*));
    for (size_t i = 0; i < vis_counter; ++i){
        if (strcmp(file, visited_files[i])==0)
            return 1;
    }
    visited_files = (char**)realloc(visited_files, sizeof(char*)*vis_counter+1);
    visited_files[vis_counter] = (char*)malloc(1024);
    strcpy(visited_files[vis_counter], file);
    vis_counter++;

    return 0;
}

void insideDir(const char* dirname, int depth, int s){

    DIR* dir = NULL;
    struct dirent entry;
    struct dirent *entryPtr = NULL;
    int retval = 0;
    char pathName[255];
    dir = opendir(dirname);
    if (dir == NULL)
        return;
    retval = readdir_r(dir, &entry, &entryPtr);
    while (entryPtr != NULL){
        if (retval != 0){
            fprintf(stderr, "couldn't open something. error %d", retval);
        }
        struct stat entryInfo;
        strcpy( pathName, dirname );
        strcat( pathName, "/");
        strcat( pathName, entry.d_name);
        lstat(pathName, &entryInfo);
        if ( strcmp(entry.d_name, ".") == 0 || strcmp(entry.d_name, "..") == 0){
            retval = readdir_r(dir, &entry, &entryPtr);
            continue;
        }
        if (S_ISDIR(entryInfo.st_mode)){
            if (depth > 1){
                depth--;
                insideDir(pathName, depth, s);
                ++depth;
            }else if (depth == -1)
                insideDir(pathName, depth, s);
        }else if (S_ISREG(entryInfo.st_mode)){
            //file
            if (isVisited(pathName) == 0){
                FILE* file = fopen(pathName, "r");
                countWords(file, pathName);
            }
        }else if (S_ISLNK(entryInfo.st_mode && s == 1)){
            //symlink
            char buf[255];
            //find pathname
            realpath(pathName, buf);
            struct stat entryLinkInfo;
            lstat(buf, &entryLinkInfo);
            if (S_ISDIR(entryLinkInfo.st_mode)){
                depth--;
                insideDir(buf, depth, s);
                depth++;
            }else if (S_ISREG(entryLinkInfo.st_mode)){
                if (isVisited(pathName) == 0)
                    insideDir(buf, depth, s);
            }else
                insideDir(buf, depth, s);
        }
        retval = readdir_r(dir, &entry, &entryPtr);
    }
}

int main(int argc, const char* argv[]) {
//    
    int depth = 0, s = 0;
    for (int i = 1; i < argc; ++i){
        if (strcmp(argv[i], "-r") == 0){
            depth = atoi(argv[i+1]);
            if (depth == 0)
                depth--;
        }
        if (strcmp(argv[i], "-s")==0)
            s = 1;
        if (strcmp(argv[i], "-c")==0){
            s = 1;
            c = 1;
        }
    }
    
    insideDir(argv[1], depth, s);

    return 0;
}

