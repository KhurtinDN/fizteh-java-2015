package ru.mipt.diht.students.sopilnyak.moduletests.library;

import java.io.*;

public class Help {

    private static final String HELP_FILE = "help.txt";

    protected static boolean showHelp() {
        File file = new File(HELP_FILE);

        try {

            if (!file.exists()) {
                return false;
            }

            BufferedReader in = new BufferedReader(
                    new FileReader(file.getAbsoluteFile()));

            try {
                String string;
                while ((string = in.readLine()) != null) {
                    System.out.println(string);
                }
            } catch (IOException e) {
                return false;
            } finally {
                in.close();
            }
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
