package ru.mipt.diht.students.dkhurtin.test;

/**
 * @author Denis Khurtin
 */
public class InitializationOrder {
    static class A {
        static String generateString(Class<?> aClass, String message) {
            String aClassName = aClass.getSimpleName();
            System.out.println("Field " + message + "  of " + aClassName);
            return aClassName;
        }

        static String a = generateString(A.class, "static a");

        String a1 = generateString(A.class, "a1");

        static {
            System.out.println("Static block in A");
        }

        static String aa = generateString(A.class, "static aa");

        String a2 = generateString(A.class, "a2");

        {
            System.out.println("Block in A");
        }

        String a3 = generateString(A.class, "a3");

        A() {
            System.out.println("Constructor in A");
        }
    }

    static class B extends A {
        static String b = generateString(B.class, "static b");

        String b1 = generateString(B.class, "b1");

        static {
            System.out.println("Static block in B");
        }

        static String bb = generateString(B.class, "static bb");

        String b2 = generateString(B.class, "b2");

        {
            System.out.println("Block in B");
        }

        String b3 = generateString(B.class, "b3");

        B() {
            System.out.println("Constructor in B");
        }
    }

    public static void main(String[] args) {
        System.out.println(new B());
    }
}
