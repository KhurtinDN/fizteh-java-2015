public class Reverser {
    public static void main(String[] args) {
        System.out.println(new StringBuffer(String.join(" ", args)).reverse().toString().replaceAll("\\s+", " "));
    }  
}
