//
//  Network.cpp
//  
//
//  Created by Lokotochek on 06.10.15.
//
//

#include "Network.h"


long long Network::residualWeight ( size_t edgeNum ) {
    return edges[edgeNum].capacity - edges[edgeNum].flow;
}

long long Network::residualWeight ( NetEdge e ) {
    return e.capacity - e.flow;
}