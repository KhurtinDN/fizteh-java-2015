//
//  Graph.cpp
//  
//
//  Created by Lokotochek on 06.10.15.
//
//

#include "Graph.h"

void Graph::readEdges( size_t vertex_input, size_t edgesNumber_input ) {
    vertex = vertex_input;
    edgesNumber = edgesNumber_input;
    size_t from_input, to_input;
    Edge edge;
    long long weight_input;
    for ( size_t i = 0; i < edgesNumber; ++i ){
        cin >> from_input >> to_input >> weight_input;
        from_input--;
        to_input--;
        edge.from = from_input;
        edge.to = to_input;
        edge.weight = weight_input;
        edges.push_back(edge);

    }
}

