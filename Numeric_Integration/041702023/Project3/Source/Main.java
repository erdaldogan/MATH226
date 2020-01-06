//-----------------------------------------//
// MATH226 - Numerical Methods for EE, Fall 2019
// Project 03
//
// Name-Surname: Erdal Sidal Dogan
// Student ID: 041702023
//-----------------------------------------//

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.print("Choose the function:\n" +
                "[1]\t4 / (1 + x^2)\n" +
                "[2]\t√x * log(x)\n" +
                "\nEnter the function index (1-2):");
        int functionSelection = new Scanner(System.in).nextInt(); // choose which function to use

        System.out.print("\nEnter the interval:\n" +
                "Lower Bound:\n");
        double lower = new Scanner(System.in).nextDouble(); // lower limit of integration
        System.out.print("Upper Bound:\n");
        double upper = new Scanner(System.in).nextDouble(); // upper limit of integration

        if (lower > upper) { // error check
            System.err.println("Lower bound cannot be greater than upper bound!");
            System.exit(1);
        }
        System.out.print("Enter a value for n:\n(integer)");
        int n = new Scanner(System.in).nextInt(); // how many parts will the function be divided into?

        System.out.printf("\nMidpoint Rule: %4.6f\n", midpointRule(lower, upper, n, functionSelection));
        System.out.printf("Trapezoid Rule: %4.6f\n", trapezoidRule(lower, upper, n, functionSelection));
    }

    private static double midpointRule(double lower, double upper, int n, int functionSelect){
        double h = (upper - lower) / n; // step size
        double sum = 0;
        double temp_lower, temp_upper; // temp values will store lower and upper bounds for each step
        for (double i = h; i <= upper; i += h){
            temp_lower = i - h; // previous value of i
            temp_upper = i;
            double midpoint = (temp_lower + temp_upper) / 2;
            sum += selectFunction(functionSelect, midpoint); // midpoint rule formula suggests
        }
        return h * sum; // midpoint rule formula
    }

    private static double trapezoidRule(double lower, double upper, int n, int functionSelect){
        double h = (upper - lower) / n; // step size
        double sum = 0;
        double temp_lower, temp_upper; // temp values will store lower and upper bounds for each step
        for (double i = h; i <= upper; i += h){
            temp_lower = i - h; // previous value of i
            temp_upper = i;

            // trapezoid rule formula suggests
            sum += (h / 2) * (selectFunction(functionSelect, temp_lower) + selectFunction(functionSelect, temp_upper));
        }
        return sum;
    }

    private static double selectFunction(int select, double value){
        switch (select){
            case 1: return 4 / (1 + Math.pow(value, 2));
            case 2:
                if (value <=0) // avoid log(0) and negatives.
                    value = 0.00001;
                return Math.sqrt(value) * Math.log(value);
        }
        System.err.println("Function doesn't exist!"); // if user selects anything other than available functions
        System.exit(2);
        return 0;
    }
}
