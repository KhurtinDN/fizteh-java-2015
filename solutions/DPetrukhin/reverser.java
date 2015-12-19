package ru.mipt.diht.students.dpetrukhin.reverser;

/**
 * Created by daniel on 19.12.15.
 */
public class reverser {
    public static void main(String[] args) {
        for (int i = args.length - 1; i >= 0; --i) {
            String[] splitted = args[i].split("\\s+");
            for (int j = splitted.length - 1; j >= 0; --j) {
                System.out.print(splitted[j]);
            }
        }
    }
}