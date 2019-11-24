import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println(bisection());


    }

    private static double bisection() throws AssertionError{
        double sign_fa = 0;
        double sign_fb = 0;
        double f_a = 0;
        double f_b = 0;
        double a = 0;
        double b = 0;

        while (sign_fa == sign_fb) {
            System.out.println("Please enter intervals\n a=");
            a = new Scanner(System.in).nextDouble();
            System.out.println("b=");
            b = new Scanner(System.in).nextDouble();
            f_a = f3(a);
            f_b = f3(b);
            sign_fa = Math.signum(f_a);
            sign_fb = Math.signum(f_b);
            if (sign_fa == sign_fb)
                System.err.println("Signs of the function values are same!\nChoose another interval!");
        }
        double tolerance = 0.000000001;
        double error = 100;
        System.out.format("%-15s%-15s%-15s%-15s\n","a", "f(a)", "b", "f(b)");
        while (error > tolerance){
            sign_fa = Math.signum(f_a);
            double middle = a + (b - a) / 2;
            double sign_mid = Math.signum(f3(middle));
            if (sign_mid == sign_fa)
                a = middle;
            else
                b = middle;
            f_a = f3(a);
            f_b = f3(b);
            error = Math.abs(f_a - f_b);
            System.out.printf("%-15f%-15f%-15f%-15f\n", a, f_a, b, f_b);
        }
        return b;
    }


    private static double f(double x){
        return Math.pow(x, 3) - (2 * x) - 5;
    }

    private static double f1(double x){
        return Math.exp(-x) - x;
    }

    private static double f2(double x){
        return x * Math.sin(degToRad(x)) - 1;
    }

    private static double f3(double x){
        return Math.pow(x, 3) - (3 * Math.pow(x,2)) + 3 * x - 1;
    }

    private static double degToRad(double num){
        return (Math.PI / 180) * num;
    }

}

