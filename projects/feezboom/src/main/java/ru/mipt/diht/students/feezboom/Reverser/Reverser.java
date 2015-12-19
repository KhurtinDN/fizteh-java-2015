package ru.mipt.diht.students.feezboom.Reverser;

/**
 * * Created by avk on 19.12.15.
 **/
public class Reverser {
    public static void main(String[] args) {
        for (int i = args.length - 1; i >= 0; i--) {
            String input[] = args[i].split("\\D++");
            for (int j = input.length - 1; j >= 0 ; j--) {
                System.out.println(input[j]);
            }
        }
    }
}
