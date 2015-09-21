public class Reverser {

	public static void main(String[] args) {
		for (int i = args.length - 1; i >= 0; --i) {
			String[] splittedString = args[i].split("\\s+");
			for (int j = splittedString.length - 1; j >= 0; --j) {
				System.out.print(splittedString[j] + " ");
			}
		}
		System.out.println();
	}
}
		
	
