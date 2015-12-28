package ru.fizteh.fivt.students.JenkaEff.CollectionQuery.impl;

public class Pair <T, R>{
    private T key;
    private R value;
    Pair(T k, R v) {
        key = k;
        value = v;
    }
    public T getKey() {
        return key;
    }
    public void setKey(T key) {
        this.key = key;
    }
    public R getValue() {
        return value;
    }
    public void setValue(R value) {
        this.value = value;
    }
    
}
