//
//  MaxFlow.h
//  
//
//  Created by Lokotochek on 06.10.15.
//
//

#ifndef ____MaxFlow__
#define ____MaxFlow__

#include <stdio.h>
#include <iostream>
#include <vector>
#include <queue>
#include <list>
#include <limits>
#include <utility>
#include "graph.h"
#include "network.h"

class MaxFlow {
public:
    
    MaxFlow(Graph g);
    
    long long findMaxFlow();
    
    Network getNetwork();
    
private:
    
    size_t vertex, edgesNumber;
    vector<vector<size_t>> sm;
    vector<size_t> h, current, position;
    vector<long long> e;
    Network network;
    long long maxFlowValue;
    
    inline long long min( long long a, long long b );
    inline size_t numberOfBackEdge( size_t edgeNum );
    void push( Network::NetEdge &edge, Network::NetEdge &backedge );
    void relabel( size_t u );
    bool isPushable( size_t currentEdge, size_t u, size_t v );
    void discharge( size_t u );
    void initPreFlow();
    void push_relabel_worker();
};


#endif /* defined(____MaxFlow__) */
