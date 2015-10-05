public class Reverser {
	
	public static void main(String[] args){
		
		String s = "";
		for (int i = 0; i < args.length; i++)
			s = s + args[i] + ' ';
			
		String[] newstr = s.split("\\D+");
		
		for (int j = newstr.length - 1; j >= 0; j--){
			System.out.print(newstr[j] + ' ');
		}
	}
}