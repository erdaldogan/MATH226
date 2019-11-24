import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println(secant());
        System.out.println(bisection());
        System.out.println(newtons());


    }

    private static double bisection() throws AssertionError{
        System.out.println("\n\nBisection Method\n");
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
            f_a = f1(a);
            f_b = f1(b);
            sign_fa = Math.signum(f_a);
            sign_fb = Math.signum(f_b);
            if (sign_fa == sign_fb)
                System.err.println("Signs of the function values are same!\nChoose another interval!");
        }
        double tolerance = 0.0001;
        double error = 100;
        System.out.format("%-15s%-15s%-15s%-15s\n","a", "f(a)", "b", "f(b)");
        while (error > tolerance){
            sign_fa = Math.signum(f_a);
            double middle = a + (b - a) / 2;
            double sign_mid = Math.signum(f1(middle));
            if (sign_mid == sign_fa)
                a = middle;
            else
                b = middle;
            f_a = f1(a);
            f_b = f1(b);
            error = Math.abs(f_a - f_b);
            System.out.printf("%-15f%-15f%-15f%-15f\n", a, f_a, b, f_b);
        }
        return b;
    }

    private static double secant(){
        System.out.println("\n\nSecant Method\n");
        System.out.println("Enter your initial guess x_0: ");
        double xi0 = new Scanner(System.in).nextDouble();
        System.out.println("Enter your initial guess x_1: ");
        double xi = new Scanner(System.in).nextDouble();
        double tolerance = 0.0001, error = 1;

        double fxi0 = f1(xi0), fxi = f1(xi), fxi1 = 0;
        System.out.format("%-15s%-15s%-15s%-15s\n","xi0", "xi", "xi1", "f(xi1)");
        while (error > tolerance){
            double xi1 = xi - (fxi * (xi - xi0) / (fxi - fxi0));
            fxi1 = f1(xi1);
            error = Math.abs(xi1 - xi);
            System.out.printf("%-15f%-15f%-15f%-15f\n", xi0, xi, xi1, fxi1);
            xi0 = xi;
            xi = xi1;
            fxi0 = f1(xi0);
            fxi = f1(xi);
        }
        return xi;
    }

    private static double newtons(){
        System.out.println("\n\nNewston's Method\n");
        System.out.println("Enter your initial guess: ");
        double xk = new Scanner(System.in).nextDouble();
        double xk1 = 0;
        double tolerance = 0.0001, error = 1;
        System.out.format("%-15s%-15s\n","xk", "xk1");
        while (error > tolerance){
            xk1 = xk - (f1(xk) / f1_prime(xk));
            error = Math.abs(xk1 - xk);
            System.out.format("%-15f%-15f\n",xk, xk1);
            xk = xk1;
        }
        return xk1;
    }


    private static double f(double x){
        return Math.pow(x, 3) - (2 * x) - 5;
    }

    private static double f_prime(double x){
        return 3 * Math.pow(x, 2) - 2;
    }

    private static double f1(double x){
        return Math.exp(-x) - x;
    }

    private static double f1_prime(double x){
        return -Math.exp(-x) - 1;
    }

    private static double f2(double x){
        return x * Math.sin(degToRad(x)) - 1;
    }

    private static double f2_prime(double x){
        return Math.sin(degToRad(x)) + (x * Math.cos(degToRad(x)));
    }

    private static double f3(double x){
        return Math.pow(x, 3) - (3 * Math.pow(x,2)) + 3 * x - 1;

    }

    private static double f3_prime(double x){
        return 3 * Math.pow(x, 2) - 6 * x + 3;
    }

    private static double degToRad(double num){
        return (Math.PI / 180) * num;

    }

}

