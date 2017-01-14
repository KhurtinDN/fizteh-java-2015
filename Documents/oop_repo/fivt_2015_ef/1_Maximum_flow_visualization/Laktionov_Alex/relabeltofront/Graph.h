//
//  Graph.h
//  
//
//  Created by Lokotochek on 06.10.15.
//
//

#ifndef ____Graph__
#define ____Graph__

#include <vector>
#include <iostream>
using namespace std;


class Graph {
public:
    
    size_t vertex, edgesNumber;
    
    struct Edge {
        size_t from;
        size_t to;
        long long weight;
    };
    
    vector<Edge> edges;
    
    void readEdges( size_t vertex_input, size_t edgesNumber_input );
};


#endif /* defined(____Graph__) */
