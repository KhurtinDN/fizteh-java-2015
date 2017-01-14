#include <iostream>
#include <set>
#include <map>
#include <assert.h>
#include <string>
#include <vector>

using namespace std;

class gameMap{
public:
    vector<int> situation;
    vector<int> prevSituation;
    int stepsFromStart = 0; // шагов до этого состояния
    int badCellsCounter = 0; // сколько не на своем месте
    bool operator<(const gameMap &b){
        if (stepsFromStart+badCellsCounter != b.stepsFromStart+b.badCellsCounter)
            return stepsFromStart+badCellsCounter < b.stepsFromStart+b.badCellsCounter;
        else
            return situation < b.situation;
    }
};

enum{
    START = -2,
    UP = -3,
    DOWN = 3,
    LEFT = -1,
    RIGHT = 1
};

bool operator<(const vector<int> &a, const vector<int> &b){
    for (int i = 0; i < a.size(); ++i){
        if (a[i] > b[i])
            return false;
    }
    return true;
}

class Azvezda {
public:
    explicit Azvezda(vector<int> startSituation){
        start.situation = startSituation;
    }
    
    void calc(){
        final = azvezdochka(start);
    }
    
    vector<string> getAns(){
        return getAnswer();
    }
    
    int getNumberOfSteps(){
        assert(counter >= 0);
        return counter;
    }
private:
    gameMap start, final;
    int counter = -1;

    bool recalcBadCells(gameMap &node){
        int counter = 0;
        for (int i = 0; i < 9; ++i)
            if (node.situation[i] != i)
                counter++;
        node.badCellsCounter = counter;
        return (counter == 0);
    }

    int whereIsEmpty (vector<int> &v){
        for (int i = 0; i < 9; ++i)
            if (v[i] == 0)
                return i;
        return -1;
    }

    set<gameMap> opened;
    map<vector<int>, pair<int, vector<int>>> marked; //situation, <distance, prevSituation>

    void check(gameMap &node, gameMap &prev, ssize_t offset, size_t emptyPos){
        int lastDist, newDist;
        swap(node.situation[emptyPos], node.situation[emptyPos-1]);
        if (marked.count(node.situation)){
            lastDist = ((*marked.find(node.situation)).second).first;
            recalcBadCells(node);
        }else{
            node.prevSituation = prev.situation;
            node.stepsFromStart = prev.stepsFromStart + 1;
            recalcBadCells(node);
            opened.insert(node);
            marked.insert(pair<vector<int>, pair<int, vector<int>>>
                          (node.situation,
                          pair<int, vector<int>>(node.stepsFromStart+node.badCellsCounter, node.prevSituation)));
            return;
        }
        newDist = prev.stepsFromStart + 1 + node.badCellsCounter;
        if (newDist < lastDist){
            gameMap old = node;
            old.stepsFromStart = lastDist - old.badCellsCounter;
            opened.erase(old);
            recalcBadCells(node);
            node.prevSituation = prev.situation;
            node.stepsFromStart = prev.stepsFromStart + 1;
            opened.insert(node);
            auto s = marked.find(node.situation);
            s->second = pair<int, vector<int>>(node.stepsFromStart + node.badCellsCounter, node.prevSituation);
        }
    }

    gameMap azvezdochka(gameMap &start){
        gameMap current = start;
        recalcBadCells(current);
        vector<int> empty(9);
        empty[0] = START;
        marked.insert(pair<vector<int>, pair<int, vector<int>>>
                      (current.situation,
                      pair<int, vector<int>>(current.stepsFromStart+current.badCellsCounter, empty)));
        opened.insert(current);
        gameMap neighbour;
        while (opened.size()){
            current = *(opened.begin());
            opened.erase(opened.begin());
            if (recalcBadCells(current))
                return current;
            int emptyPos = whereIsEmpty(current.situation);
            neighbour = current;
            if (emptyPos > 2) // we can go up
                check(neighbour, current, UP, emptyPos);
            neighbour = current;
            if (emptyPos < 6) // we can go down
                check(neighbour, current, DOWN, emptyPos);
            neighbour = current;
            if (emptyPos != 2 && emptyPos != 5 && emptyPos != 8) // we can go right
                check(neighbour, current, RIGHT, emptyPos);
            neighbour = current;
            if (emptyPos != 0 && emptyPos != 3 && emptyPos != 6) // we can go left
                check(neighbour, current, LEFT, emptyPos);
        }
        return start; // fail
    }
    
    vector<string> getAnswer(){
        if (final.situation == start.situation){
            cout << '0';
            return vector<string>();
        }
        int empCur, empPrev;
        counter = 0;
        vector<string> steps;
        vector<int> prev = ((*marked.find(final.situation)).second).second;
        vector<int> currentSituation = final.situation;
        while(true){
            empCur = whereIsEmpty(currentSituation);
            empPrev = whereIsEmpty(prev);
            if (prev[0] == START)
                break;
            if (empCur == empPrev + LEFT)
                steps.push_back("LEFT");
            if (empCur == empPrev + RIGHT)
                steps.push_back("RIGHT");
            if (empCur == empPrev + DOWN)
                steps.push_back("DOWN");
            if (empCur == empPrev + UP)
                steps.push_back("UP");
            ++counter;
            currentSituation = prev;
            prev = ((*marked.find(prev)).second).second;
        }
        reverse(steps.begin(), steps.end());
        return steps;
    }
};

int main() {
    int k;
    cin >> k;
    if (k != 3){
        cout << '0';
        return 0;
    }
    vector<int> start;
    int input;
    for (int i = 0; i < 9; ++i){
        cin >> input;
        start.push_back(input);
    }
    
    Azvezda game(start);
    game.calc();
    vector<string> answer = game.getAns();
    int steps = game.getNumberOfSteps();
    
    cout << steps;
    for (int i = 0; i < answer.size(); ++i)
        cout << "\n" << answer[i];
    
    return 0;
}