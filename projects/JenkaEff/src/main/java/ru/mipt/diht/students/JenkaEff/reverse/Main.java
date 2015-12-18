package projects.JenkaEff.src.main.java.ru.mipt.diht.students.JenkaEff.reverse;

public class Main {
	public static void main(String[] args) {
		String allNumbers = String.join(" ", args).trim();
		String[] numbers = allNumbers.split("\\s+");
		for (int i = numbers.length - 1; i >= 0; i--){
			System.out.print(numbers[i] + " ");
		}
		System.out.println();
	}
}
