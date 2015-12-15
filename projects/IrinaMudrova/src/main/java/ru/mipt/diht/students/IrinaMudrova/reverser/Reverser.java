package main.java.ru.mipt.diht.students.IrinaMudrova.reverser;

public class Reverser {
    public static void main(String[] args) {
        for (int i = args.length - 1; i >= 0; --i) {
            String[] str = args[i].split("\\D+");
            for (int j = str.length - 1; j >= 0; --j) {
                System.out.print(str[j] + " ");
            }
        }
        System.out.println();
    }
}
