public class First_task {
    public static void main(String[] args) {
            for (int i = args.length - 1; i >= 0; i--) {
                String[] strings = args[i].split("\\s+");
                for (int j = strings.length - 1; j >= 0; j--) {
                    System.out.printf("%s ", strings[j]);
                }
            }
        System.out.println();
    }
}
