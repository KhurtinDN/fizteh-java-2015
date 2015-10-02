
public class ReverseNumers {
	public static void main(String[] args) {
		for (int i = args.length - 1; i >= 0; --i) {
			String[] arr = args[i].split("\\s");
			for (int j = arr.length - 1; j >= 0; --j) {
				System.out.print(arr[j] + " ");
			}
		}
	}
}
