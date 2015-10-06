//
//  MaxFlow.cpp
//  
//
//  Created by Lokotochek on 06.10.15.
//
//

#include "MaxFlow.h"

MaxFlow::MaxFlow(Graph g) {
    vertex = g.vertex;
    edgesNumber = g.edgesNumber;
    sm.resize( vertex );
    Network::NetEdge netEdge;
    Graph::Edge edge;
    for ( size_t i = 0; i < edgesNumber; ++i ) {
        edge = g.edges[i];
        netEdge.from = edge.from;
        netEdge.to = edge.to;
        netEdge.capacity = edge.weight;
        netEdge.flow = 0;
        network.edges.push_back( netEdge );
        sm[edge.from].push_back( 2 * i );
        swap( netEdge.from, netEdge.to );
        netEdge.capacity = 0;
        network.edges.push_back( netEdge );
        sm[edge.to].push_back( (2 * i) + 1);
    }
}

long long MaxFlow::findMaxFlow() {
    push_relabel_worker();
    return maxFlowValue;
}

Network MaxFlow::getNetwork() {
    Network answer;
    for ( size_t i = 0; i < network.edges.size(); i += 2 ) {
        answer.edges.push_back( network.edges[i] );
    }
    return answer;
}

inline long long MaxFlow::min( long long a, long long b ) {
    if (a > b)
        return b;
    return a;
}

inline size_t MaxFlow::numberOfBackEdge( size_t edgeNum ) {
    if (edgeNum % 2 == 0)
        return edgeNum + 1;
    else
        return edgeNum - 1;
}


void MaxFlow::push( Network::NetEdge &edge, Network::NetEdge &backedge ) {
    size_t u = edge.from;
    long long delta = min( e[u], network.residualWeight(edge) );
    
    edge.flow += delta;
    backedge.flow -= delta;
    
    e[edge.from] -= delta;
    e[edge.to] += delta;
}


void MaxFlow::relabel( size_t u ) {
    size_t min = (2 * vertex) + 1, currentEdge;
    for (size_t i = 0; i < sm[u].size(); ++i ) {
        currentEdge = sm[u][i];
        if ( network.residualWeight( currentEdge ) > 0)
            min = std::min( h[network.edges[currentEdge].to],min );
    }
    h[u] = min + 1;
}

bool MaxFlow::isPushable( size_t currentEdge, size_t u, size_t v ) {
    return network.residualWeight(currentEdge) > 0 && h[u] == h[v]+1;
}


void MaxFlow::discharge( size_t u ) {
    size_t v, currentEdge;
    while ( e[u] > 0 ) {
        if ( position[u] < sm[u].size() ) {
            v = network.edges[sm[u][position[u]]].to;
            currentEdge = sm[u][position[u]];
            if ( isPushable( currentEdge, u, v ) ) {
                push( network.edges[currentEdge],
                     network.edges[numberOfBackEdge(currentEdge)] );
            } else {
                position[u]++;
            }
        } else {
            relabel( u );
            position[u] = 0;
        }
    }
}

void MaxFlow::initPreFlow() {
    h.assign( vertex, 0 );
    h[0] = vertex;
    e.assign( vertex, 0 );
    position.assign( vertex, 0 );
    e[0] = numeric_limits<long long>::max();
    for ( size_t i = 0; i < network.edges.size(); i += 2 ) {
        if ( network.edges[i].from == 0 ) {
            network.edges[i].flow += network.edges[i].capacity;
            network.edges[i+1].flow += -network.edges[i].capacity;
            e[network.edges[i].to] += network.edges[i].capacity;
            e[0] -= network.edges[i].capacity;
        }
    }
}

void MaxFlow::push_relabel_worker() {
    
    initPreFlow();
    
    
    list<size_t> L;
    for ( size_t i = 1; i < vertex - 1; ++i )
        L.push_back( i );
    
    list<size_t>::iterator iter = L.begin();
    size_t oldh;
    
    while ( iter != L.end() ) {
        size_t u = *iter;
        oldh = h[u];
        discharge( u );
        
        if (h[u] > oldh) {
            L.erase( iter );
            iter = L.begin();
            L.push_front( u );
        } else
            iter++;
    }
    maxFlowValue = e[vertex - 1];
}
