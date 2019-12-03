//-----------------------------------------//
// MATH226 - Numerical Methods for EE
// Project 02
//
// Name-Surname: Erdal Sidal Dogan
// Student ID: 041702023
//-----------------------------------------//
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.print("Choose the function:\n" +
                "[1]\tx^3 - 2x - 5\n" +
                "[2]\te^(-x) - x\n" +
                "[3]\txsin(x) - 1\n" +
                "[4]\tx^3 - 3x^2 + 3x - 1\n" +
                "[5]\t0.5 - xe^(-x^2)\n" +
                "[6]\tx^2 + 4cos(x)\n" +
                "[7]\tx^4 - 14x^3 + 60x^2 - 70x\n\n" +
                "Enter the function index (1-7):");
        int functionSelection = new Scanner(System.in).nextInt(); // user choose which function to use

        System.out.print("\n\nChoose the operation:\n" +
                "[1]\tFind the roots of the function (Secant & Bisection Method)\n" +
                "[2]\tFind the minimum (Newton's Method)\n" +
                "Enter the operation index (1-2):");
        int methodSelection = new Scanner(System.in).nextInt(); // user choose which operation to perform

        if (methodSelection == 1){  // root finding
            printGreen("Solution is:\t" + secant(functionSelection) + "\n");
            printGreen("Solution is:\t" + bisection(functionSelection) + "\n");
        }
        else if (methodSelection == 2) { // minimization
            double result_x = newtons(functionSelection);
            printGreen("Minimum value is at x:\t" + result_x + ", f(x) = " +
                    functionSelector(functionSelection, result_x) +"\n");
        }
    }

    private static double bisection(int selection) throws AssertionError{  // root finding bisection method
        System.out.println("\n\nBisection Method");
        double sign_fa = 0; // sign of f(a)
        double sign_fb = 0;
        double f_a = 0; // value of f(a)
        double f_b = 0;
        double a = 0; // interval [a, b]
        double b = 0;

        while (sign_fa == sign_fb) { // repeatedly ask for new intervals if function value given ones have same signs
            System.out.println("Please enter intervals\n a=");
            a = new Scanner(System.in).nextDouble(); // interval [a, b]
            System.out.println("b=");
            b = new Scanner(System.in).nextDouble();
            f_a = functionSelector(selection, a); // value of the function at point 'a'
            f_b = functionSelector(selection, b); // value of the function at point 'b'
            sign_fa = Math.signum(f_a); // sign of the function, either 1 or -1
            sign_fb = Math.signum(f_b);
            if (sign_fa == sign_fb) { // same signs on both values means there is(are) no zero(s) between the interval
                printRed("Signs of the function values are same!\nChoose another interval or quit\n" +
                        "(Type 'q' to quit, any other value to choose another interval)");
                String userChoice = new Scanner(System.in).next().toLowerCase();
                if (userChoice.equals("q")) // quit
                    return 0;
            }
        }
        double tolerance = 0.0001;
        double error = 1; // difference between the f(a) & f(b), will converge to 0 zero as we get closer to the root
        System.out.format("%-15s%-15s%-15s%-15s\n","a", "f(a)", "b", "f(b)");
        while (error > tolerance){
            System.out.printf("%-15f%-15f%-15f%-15f\n", a, f_a, b, f_b);
            sign_fa = Math.signum(f_a);
            sign_fb = Math.signum(f_b);
            double middle = a + (b - a) / 2; // middle point of the interval, a or b will be shifted here later
            double sign_mid = Math.signum(functionSelector(selection, middle)); // sign of middle point
            if (sign_mid == sign_fa) // whether a has same sign w/ middle or not. If same, move a to middle point
                a = middle;
            else if (sign_mid == sign_fb) // same for b
                b = middle;
            f_a = functionSelector(selection, a); // new value of f(a)
            f_b = functionSelector(selection, b); // new value of f(b)
            error = Math.abs(f_a - f_b);
        }
        return b;
    }

    private static double secant(int selection){
        System.out.println("\n\nSecant Method");
        System.out.println("Enter your initial guess for x_0: ");
        double xi0 = new Scanner(System.in).nextDouble();
        System.out.println("Enter your initial guess for x_1: ");
        double xi = new Scanner(System.in).nextDouble();
        double tolerance = 0.0001, error = 1;
        double fxi0 = functionSelector(selection, xi0), fxi = functionSelector(selection, xi), fxi1;
        System.out.format("%-15s%-15s%-15s%-15s\n","xi0", "xi", "xi1", "f(xi1)");
        while (error > tolerance){
            double xi1 = xi - (fxi * (xi - xi0) / (fxi - fxi0));
            fxi1 = functionSelector(selection, xi1);
            error = Math.abs(xi1 - xi);
            System.out.printf("%-15f%-15f%-15f%-15f\n", xi0, xi, xi1, fxi1);
            xi0 = xi; // xi0 = xi-
            xi = xi1; // xi1 = xi+
            fxi0 = functionSelector(selection, xi0); // fxi = f(xi)
            fxi = functionSelector(selection, xi);
        }
        return xi;
    }

    private static double newtons(int selection){
        System.out.println("\n\nNewton's Method");
        System.out.println("Enter your initial guess: ");
        double xk = new Scanner(System.in).nextDouble();
        double xk1 = 0;// next xk (xk+)
        double f_xk; // most recent f(xk)
        double f_xk_temp = -10; // hold previous value for convergence check
        System.out.format("%-20s%-20s%-20s%-20s\n","xk", "f(xk)", "f'(xk)", "f''(xk)");
        for (int i = 0; i < 200; i++){ // 200 iterations at max
            f_xk = functionSelector(selection, xk); // most recent f(xk)
            if (Math.abs(f_xk_temp - f_xk) < 0.00001) // if converging
                break;
            f_xk_temp = f_xk; // hold current value
            double prime = functionSelectorPrimes(selection, xk); // f'(xk)
            double double_prime = functionSelectorDoublePrimes(selection, xk); // f''(x)
            if (double_prime <= 0){
                printRed("Newton's Method is diverging! (f‘‘(x) ≤ 0)\n");
                break;
            }
            xk1 = xk - (prime / double_prime);// x_(k+1), next xk
            System.out.format("%-20.4f%-20.4f%-20.4f%-20.4f\n",xk, f_xk, prime, double_prime);
            xk = xk1; // update the xk and start new iteration
        }
        return xk1;
    }

    private static double functionSelector(int selection, double x){
        switch (selection) {
            case 1: return Math.pow(x, 3) - (2 * x) - 5;
            case 2: return Math.exp(-x) - x;
            case 3: return x * Math.sin(x) - 1;
            case 4: return Math.pow(x, 3) - (3 * Math.pow(x,2)) + (3 * x) - 1;
            case 5: return 0.5 - x * Math.exp(-Math.pow(x, 2));
            case 6: return Math.pow(x, 2) + 4 * Math.cos(x);
            case 7: return Math.pow(x, 4) - 14 * Math.pow(x, 3) + 60 * Math.pow(x, 2) - 70 * x;

        }
        return 0;
    }

    private static double functionSelectorPrimes(int selection, double x){
        switch (selection){
            case 1: return 3 * Math.pow(x, 2) - 2;
            case 2: return -Math.exp(-x) - 1;
            case 3: return Math.sin(x) + (x * Math.cos(x));
            case 4: return 3 * Math.pow(x, 2) - 6 * x + 3;
            case 5: return -Math.exp(-Math.pow(x, 2)) + 2 * Math.pow(x, 2) * Math.exp(-Math.pow(x, 2));
            case 6: return (2 * x) - (4 * Math.sin(x));
            case 7: return 4 * Math.pow(x, 3) - 42 * Math.pow(x, 2) + 120 * x - 70;

        }
        return 0;
    }

    private static double functionSelectorDoublePrimes(int selection, double x){
        switch (selection){
            case 1: return 6 * x;
            case 2: return Math.exp(-x);
            case 3: return Math.cos(x + Math.cos(x) - Math.sin(x) * x);
            case 4: return 6 * x - 6;
            case 5: return 2 * x * (3 - 2 * Math.pow(x, 2)) * Math.exp(-Math.pow(x, 2));
            case 6: return 2 - 4 * Math.cos(x);
            case 7: return 12 * Math.pow(x, 2) - 84 * x + 120;
        }
        return 0;
    }

    private static void printRed(String x){
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_RESET  = "\u001B[0m";
        System.out.print(ANSI_RED + x + ANSI_RESET);
    }

    private static void printGreen(String x){
        final String ANSI_GREEN  = "\u001B[32m";
        final String ANSI_RESET  = "\u001B[0m";
        System.out.print(ANSI_GREEN + x + ANSI_RESET);
    }
}
