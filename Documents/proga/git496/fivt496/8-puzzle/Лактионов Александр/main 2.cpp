#include <iostream>
#include <set>
#include <map>
#include <string>
#include <algorithm>
#include <vector>
#include <cmath>

using namespace std;

class gameMap{
public:
    vector<int> map;
    int stepsFromStart = 0; // шагов до этого состояния
    int badCellsCounter = 0; // сколько не на своем месте
    
    bool recalcBadCells(){
        int counter = 0;
        int lineSize = sqrt(map.size());
        // manhattan
        for (int i = 0; i < map.size(); ++i){
            if (map[i] != 0)
                counter += abs( i/lineSize - map[i]/lineSize )
                + abs( i%lineSize - map[i]%lineSize );
        }
        // L & C: в своих ли строке / столбце
        for (int i = 0; i < lineSize; ++i){
            for (int j = 0; j < lineSize; ++j){
                if (map[i*lineSize+j]/lineSize != i) // row
                    ++counter;
                if (map[i*lineSize+j]%lineSize != j) // column
                    ++counter;
            }
        }
        badCellsCounter = counter;
        return (counter == 0);
    }
    
    
    bool operator<(const gameMap &b) const{
        if (stepsFromStart+badCellsCounter != b.stepsFromStart+b.badCellsCounter)
            return stepsFromStart+badCellsCounter < b.stepsFromStart+b.badCellsCounter;
        else
            return map < b.map;
    }
};

class Azvezda {
public:
    explicit Azvezda(vector<int> startSituation){
        start.map = startSituation;
        int lineSize = sqrt(start.map.size());
        direction = {(-1)*lineSize, lineSize, -1, 1};
        final = azvezdochka(start);
    }
    
    pair<int, vector<string>> getAns(){
        vector<string> answer;
        if (counter != -1){
            answer = getAnswer();
            if (checkCorrectness(answer, start.map) == false)
                cout << "----\nWRONG\n----\n";
        }
        return pair<int, vector<string>>(counter, answer);
    }
    
private:
    
    struct directions{
        int UP, DOWN, LEFT, RIGHT;
    };
    
    gameMap start, final;
    int counter = 0;
    directions direction;
    
    int whereIsEmpty (vector<int> &v){
        for (int i = 0; i < start.map.size(); ++i)
            if (v[i] == 0)
                return i;
        return -1;
    }
    
    set<gameMap> opened;
    map<vector<int>, pair<int, vector<int>>> marked; //situation, <distance, prevSituation>
    
    void checkNeighbour(gameMap &node, int prevStepsFromStart, int action, size_t emptyPos){
        int lastDist = -1; //если еще не смотрели вершину, то новое расстояние - лучшее
        vector<int> previous = node.map;
        swap(node.map[emptyPos], node.map[emptyPos + action]);
        if (marked.count(node.map)){
            lastDist = ((*marked.find(node.map)).second).first;
            node.recalcBadCells();
        }else{
            node.stepsFromStart = prevStepsFromStart + 1;
            node.recalcBadCells();
            opened.insert(node);
            marked.insert(pair<vector<int>, pair<int, vector<int>>>
                          (node.map,
                           pair<int, vector<int>>(node.stepsFromStart+node.badCellsCounter, previous)));
        }
        if (prevStepsFromStart + 1 + node.badCellsCounter < lastDist){
            gameMap old = node;
            old.stepsFromStart = lastDist - old.badCellsCounter;
            opened.erase(old);
            node.recalcBadCells();
            node.stepsFromStart = prevStepsFromStart + 1;
            opened.insert(node);
            auto s = marked.find(node.map);
            s->second = pair<int, vector<int>>(node.stepsFromStart + node.badCellsCounter, previous);
        }
    }
    
    gameMap azvezdochka(gameMap &start){
        gameMap current = start;
        current.recalcBadCells();
        size_t length = sqrt(start.map.size());
        vector<int> empty(start.map.size());
        empty[0] = -1;
        marked.insert(pair<vector<int>, pair<int, vector<int>>>
                      (current.map,
                       pair<int, vector<int>>(current.stepsFromStart+current.badCellsCounter, empty)));
        opened.insert(current);
        gameMap neighbour;
        while (!opened.empty()){
            current = *(opened.begin());
            opened.erase(opened.begin());
            if (current.recalcBadCells())
                return current;
            int emptyPos = whereIsEmpty(current.map);
            neighbour = current;
            if (emptyPos >= length) // we can go up
                checkNeighbour(neighbour, current.stepsFromStart, direction.UP, emptyPos);
            neighbour = current;
            if (emptyPos < length*(length-1)) // we can go down
                checkNeighbour(neighbour, current.stepsFromStart, direction.DOWN, emptyPos);
            neighbour = current;
            if (emptyPos % length != length - 1) // we can go right
                checkNeighbour(neighbour, current.stepsFromStart, direction.RIGHT, emptyPos);
            neighbour = current;
            if (emptyPos % length != 0) // we can go left
                checkNeighbour(neighbour, current.stepsFromStart, direction.LEFT, emptyPos);
        }
        return start; // fail
    }
    
    vector<string> getAnswer(){
        if (final.map == start.map)
            return vector<string>();
        int empCur, empPrev;
        vector<string> steps;
        vector<int> prev = ((*marked.find(final.map)).second).second;
        vector<int> currentSituation = final.map;
        while(true){
            empCur = whereIsEmpty(currentSituation);
            empPrev = whereIsEmpty(prev);
            if (prev[0] == -1)
                break;
            if (empCur == empPrev + direction.LEFT)
                steps.push_back("LEFT");
            if (empCur == empPrev + direction.RIGHT)
                steps.push_back("RIGHT");
            if (empCur == empPrev + direction.DOWN)
                steps.push_back("DOWN");
            if (empCur == empPrev + direction.UP)
                steps.push_back("UP");
            ++counter;
            currentSituation = prev;
            prev = ((*marked.find(prev)).second).second;
        }
        reverse(steps.begin(), steps.end());
        return steps;
    }
    
    bool checkCorrectness(vector<string> &seq, vector<int> &map){
        for (int i = 0; i < seq.size(); ++i){
            int emptyPos = whereIsEmpty(map);
            int action = 0;
            if (seq[i] == "UP")
                action = direction.UP;
            if (seq[i] == "DOWN")
                action = direction.DOWN;
            if (seq[i] == "LEFT")
                action = direction.LEFT;
            if (seq[i] == "RIGHT")
                action = direction.RIGHT;
            swap(map[emptyPos], map[emptyPos + action]);
        }
        for (int i = 0; i < map.size(); ++i){
            if (map[i] != i)
                return false;
        }
        return true;
    }
    
};

// для замера времени раскомментировать все строки в main
int main() {
    int k;
    cin >> k;
    
    vector<int> start(k*k);
    for (auto &iter: start){
        cin >> iter;
    }
    //auto timeBegin = std::chrono::steady_clock::now();
    Azvezda game(start);
    //auto timeEnd = std::chrono::steady_clock::now();
    //auto totalTime = std::chrono::duration_cast<std::chrono::microseconds>(timeEnd - timeBegin).count()*0.000001;
    
    //cout << "time: " << totalTime << "\n\n";
    auto answer = game.getAns();
    cout << answer.first;
    for (auto &iter: answer.second)
        cout << "\n" << iter;
    
    return 0;
}