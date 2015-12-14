import java.util.*;
import java.util.function.Function;

/**
 * Created by V on 29.11.2015.
 */
public class Aggregates {
    public static <S, T extends Comparable<T>>Function<S, T> max(final Function<S, T> expression){
        return new Aggregator<S, T>(){
            @Override
            public T apply(List<S> values){
                if(values.isEmpty()) {
                    return null;
                }
                else{
                    S maximum = values.get(0);
                    Iterator<S> valuesIterator = values.iterator();
                    while(valuesIterator.hasNext()){
                        S element = valuesIterator.next();
                        if(expression.apply(maximum).compareTo(expression.apply(element))<0);
                        maximum = element;
                    }
                    return expression.apply(maximum);
                }
            }
            @Override
            public T apply(S s){
                return null;
            }
        };
    }
    public static <S, T extends Comparable<T>>Function<S, T> min(final Function<S, T> expression){
        return new Aggregator<S, T>(){
            @Override
            public T apply(List<S> values){
                if(values.isEmpty()) {
                    return null;
                }
                else{
                    S minimum = values.get(0);
                    Iterator<S> valuesIterator = values.iterator();
                    while(valuesIterator.hasNext()){
                        S element = valuesIterator.next();
                        if(expression.apply(element).compareTo(expression.apply(minimum))<0);
                        minimum = element;
                    }
                    return expression.apply(minimum);
                }
            }
            @Override
            public T apply(S s){
                return null;
            }
        };
    }
    public static <S, T extends Comparable<T>>Function<S, T> count(final Function<S, T> expression, ArrayList<Object> query){
        return new Aggregator<S, T>(){
            @Override
            public T apply (S s){
                return null;
            }
            @Override
            public Integer apply(List<S> values){
                Set<Object> myHashSet = new HashSet<>();
                for(S value :values) {
                    if(){

                    }
                }
            }
        };
    }
}
