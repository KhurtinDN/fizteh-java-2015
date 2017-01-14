//
//  Network.h
//  
//
//  Created by Lokotochek on 06.10.15.
//
//

#ifndef ____Network__
#define ____Network__

#include "graph.h"

class Network {
public:
    
    struct NetEdge {
        size_t from;
        size_t to;
        long long capacity;
        long long flow;
    };
    
    vector<NetEdge> edges;
    
    long long residualWeight ( size_t edgeNum );
    long long residualWeight ( NetEdge e );
};

#endif /* defined(____Network__) */
