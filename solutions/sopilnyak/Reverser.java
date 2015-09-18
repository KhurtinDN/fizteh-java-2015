import java.util.ArrayList;

public class Reverser {
    public static void main(String[] args) {

        ArrayList<String> newVector = new ArrayList<>();

        int flag = 0; // индикатор числа
        int k = 0; // счетчик по новому вектору
        newVector.add("");

        for (int i = 0; i < args.length; i++) {
            for (int j = 0; j < args[i].length(); j++) {
                String string = Character.toString(args[i].charAt(j));
                if (string.matches("\\d")) {
                    newVector.set(k, newVector.get(k) + string);
                    flag = 1; // встретили число
                }
                else {
                    if (flag == 1) {
                        k++; // следующее слово
                        newVector.add("");
                        flag = 0;
                    }
                }
            }
            if (flag == 1) {
                k++;
                newVector.add("");
                flag = 0;
            }
        }

        for (int it = newVector.size() - 2; it > 0; it--) {
            System.out.print(newVector.get(it) + " ");
        }
        System.out.print(newVector.get(0));

    }
}