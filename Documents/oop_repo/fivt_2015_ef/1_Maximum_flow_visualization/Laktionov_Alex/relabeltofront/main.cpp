#include "MaxFlow.h"
using namespace std;

int main(){
    
    size_t vertex, edgesNumber;
    cin >> vertex >> edgesNumber;
    Graph g;
    g.readEdges( vertex, edgesNumber );
    MaxFlow pr( g );
    long long maxFlowValue = pr.findMaxFlow();
    cout << maxFlowValue << "\n";
    
    Network finalNetwork = pr.getNetwork();
    for ( size_t i = 0; i < finalNetwork.edges.size(); ++i ) {
        cout << finalNetwork.edges[i].flow << "\n";
    }
    
    return 0;
}

/* TESTS
 
4 5
1 2 3
1 3 3
2 3 1
3 4 4
2 4 1
 
 
4 5
1 2 1
1 3 3
3 2 3
2 4 2
3 4 2
 
 
 !!!
4 5
1 2 1
1 3 2
3 2 1
2 4 2
3 4 1
 
*/