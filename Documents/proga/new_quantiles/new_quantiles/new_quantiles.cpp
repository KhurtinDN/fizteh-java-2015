#include <iostream>
#include <vector>
#include <algorithm>
#include <iterator>
#include <random>
#include <cmath>
#include <cstdlib>

void minheap_siftDown(ptrdiff_t elementIndex, std::vector<int> &minheap){
    using std::swap;
    while (2*elementIndex + 1 < minheap.size()){
        ptrdiff_t l = 2*elementIndex + 1, r = 2*elementIndex + 2, j = l;
        if (r < minheap.size() && minheap[r] < minheap[l])
            j = r;
        if (minheap[elementIndex] <= minheap[j])
            break;
        swap(minheap[elementIndex], minheap[j]);
        elementIndex = j;
    }
}

void minheap_siftUp(ptrdiff_t elementIndex, std::vector<int> &minheap){
    using std::swap;
        while (minheap[elementIndex] < minheap[(elementIndex-1)/2]){
            swap(minheap[elementIndex], minheap[(elementIndex-1)/2]);
            elementIndex = (elementIndex-1)/2;
        }
}

void maxheap_siftDown(ptrdiff_t elementIndex, std::vector<int> &maxheap){
    using std::swap;
    while (2*elementIndex + 1 < maxheap.size()){
        ptrdiff_t l = 2*elementIndex + 1, r = 2*elementIndex + 2, j = l;
        if (r < maxheap.size() && maxheap[r] > maxheap[l])
            j = r;
        if (maxheap[elementIndex] >= maxheap[j])
            break;
        swap(maxheap[elementIndex], maxheap[j]);
        elementIndex = j;
    }
}

void maxheap_siftUp(ptrdiff_t elementIndex, std::vector<int> &maxheap){
    using std::swap;
        while (maxheap[elementIndex] > maxheap[(elementIndex-1)/2]){
            swap(maxheap[elementIndex], maxheap[(elementIndex-1)/2]);
            elementIndex = (elementIndex-1)/2;
        }
}

std::vector<int> quantileCount(int const n, double const alpha){
    int newOrder = 0, prevOrder = 0, currentQuantile = 0, inputCurrent, swapQuantile;
    std::vector<int> minheap, maxheap, quantiles;
    
    for (ptrdiff_t inputIterator = 0; inputIterator < n; ++inputIterator){
        std::cin >> inputCurrent;
        if (inputIterator == 0){
            minheap.push_back(inputCurrent);
            maxheap.push_back(inputCurrent);
            currentQuantile = inputCurrent;
            quantiles.push_back(currentQuantile);
        }else{
            newOrder = inputIterator*alpha;
            //k не изменился
            if (newOrder == prevOrder){
                //х направо
                if (inputCurrent >= currentQuantile){
                    minheap.push_back(inputCurrent);
                    minheap_siftUp(int(minheap.size()-1), minheap);
                    quantiles.push_back(currentQuantile);
                //x налево
                }else{
                    maxheap.push_back(inputCurrent);
                    std::swap(maxheap[0], maxheap[maxheap.size()-1]);
                    minheap.push_back(maxheap[maxheap.size()-1]);
                    maxheap.pop_back();
                    maxheap_siftDown(0, maxheap);
                    minheap_siftUp(int(minheap.size())-1, minheap);
                    minheap[0] = maxheap[0];
                    currentQuantile = minheap[0];
                    quantiles.push_back(currentQuantile);
                }
            //k изменился
            }else{
                //x направо
                if (inputCurrent >= currentQuantile){
                    swapQuantile = currentQuantile;
                    minheap[0] = inputCurrent;
                    minheap_siftDown(0, minheap);
                    maxheap[0] = minheap[0];
                    maxheap.push_back(swapQuantile);
                    maxheap_siftUp(int(maxheap.size())-1, maxheap);
                    currentQuantile = minheap[0];
                    quantiles.push_back(currentQuantile);
                //x налево
                }else{
                    maxheap.push_back(inputCurrent);
                    maxheap_siftUp(int(maxheap.size()-1), maxheap);
                    quantiles.push_back(currentQuantile);
                }
            }
        }
        prevOrder = newOrder;
    }
    return quantiles;
}


int main(){
    int n;
    double alpha;
    std::vector<int> quantiles;
    std::cin >> n >> alpha;
    
    quantiles = quantileCount(n, alpha);
    
    for (auto outputIterator = quantiles.begin(); outputIterator != quantiles.end(); ++outputIterator)
        std::cout << *outputIterator << " ";
    
    return 0;
    
}
