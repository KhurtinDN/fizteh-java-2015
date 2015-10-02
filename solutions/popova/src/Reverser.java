//import java.util.Scanner;
public class Reverser {
	public static void main(String[] args){
		/*Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        String[] s1=s.split("");*/
		String solve = new String();
        for(int i=0; i < args.length; ++i) {
        	solve=solve + args[i]+' ';
        }
        String[] solution = new String[args.length];
        solution = solve.split("\\D+");
        for(int i = solution.length-1 ; i >= 0; --i ){
        	System.out.print(solution[i]+" ");
        }
	}
}
