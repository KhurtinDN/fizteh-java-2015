package ru.mipt.diht.students.kitnoel.Reverser;

/**
 * Created by kitnoel
 */
class Reverser {
    public static void main(final String[] args) {
        for (int i = args.length - 1; i >= 0; i--) {
            String[] res = args[i].split("\\s");
            for (int j = res.length - 1; j >= 0; j--) {
                System.out.print(res[j] + " ");
            }
        }
        System.out.println();
    }
}
