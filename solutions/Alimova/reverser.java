public class Reverser {

        public static void main(String[] args){

            String a = "";
            for (int i = 0; i < args.length; i++)
                a = a + args[i] + ' ';
            String[] newstr = a.split("\\D+");
            for (int j = newstr.length - 1; j >= 0; j--){
                System.out.print(newstr[j] + ' ');
            }
        }
}
