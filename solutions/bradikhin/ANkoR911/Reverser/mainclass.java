/**
 * Created by Владимир on 19.12.2015.
 */

public class mainclass
{
    public static void main(String[] args)
    {
        for(int i = args.length - 1; i >= 0; --i)
        {
            String[] str = args[i].split("\\s+");
        }
        for(int i = str.length - 1; i >= 0; i--)
        {
            System.out.print(str[i] + " ");
        }
    }
    System.out.println();
}
