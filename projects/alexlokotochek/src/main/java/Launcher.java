import java.util.Scanner;
import counter.Counter;
import ready.Ready;

/**
 * Created by lokotochek on 13.12.15.
 */
public class Launcher {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        sc.close();

        String[] inputSplitted = input.split(" ");
        int number = Integer.parseInt(inputSplitted[1]);

        switch (inputSplitted[0]) {

            case "Counter" : {
                Counter.go(number);
                break;
            }

            case  "Ready" : {
                Ready.go(number);
                break;
            }

            default:
                System.out.println("Wrong arguments!");

        }
    }
}
