package com.company;

public class Main {

    public static void main(String[] args) {
        String[] arguments = args;
        String str = new String();
        for(int i = 0; i < arguments.length; i++) {
            str = str + arguments[i] + ' ';
        }

        String[] newstr = str.split("\\D+");

        for(int i = newstr.length - 1; i >= 0; i--) {
            System.out.print(newstr[i] + " ");
        }
    }
}
