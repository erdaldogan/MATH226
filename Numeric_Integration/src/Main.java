import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.print("Choose the function:\n" +
                "[1]\t4 / (1 + x^2)\n" +
                "[2]\t√x * log(x)\n" +
                "Enter the function index (1-2):");
        int functionSelection = new Scanner(System.in).nextInt(); // user choose which function to use

        System.out.print("\nEnter the interval:\n" +
                "Lower Bound:\n");
        double lower = new Scanner(System.in).nextDouble(); // lower bound
        System.out.print("Upper Bound:\n");
        double upper = new Scanner(System.in).nextDouble(); // upper bound

        if (lower > upper) { // error check
            System.err.println("Lower bound cannot be greater than upper bound!");
            System.exit(1);
        }
        System.out.print("Enter a value for n:\n");
        int n = new Scanner(System.in).nextInt(); // how many parts will the function be divided into?

        System.out.printf("\nMidpoint Rule: %4.6f\n", midpointRule(lower, upper, n, functionSelection));
        System.out.printf("Trapezoid Rule: %4.6f\n", trapezoidRule(lower, upper, n, functionSelection));

    }

    private static double midpointRule(double lower, double upper, int n, int functionSelect){
        double h = (upper - lower) / n; // step size
        double sum = 0;
        double temp_lower, temp_upper;
        for (double i = h; i <= upper; i += h){
            temp_lower = i - h; // previous value of i
            temp_upper = i;
            double midpoint = (temp_lower + temp_upper) / 2;
            sum += selectFunction(functionSelect, midpoint);
        }
        return h * sum;
    }

    private static double trapezoidRule(double lower, double upper, int n, int functionSelect){
        double h = (upper - lower) / n; // step size
        double sum = 0;
        double temp_lower, temp_upper;
        for (double i = h; i <= upper; i += h){
            temp_lower = i - h;
            temp_upper = i;
            sum += (h / 2) * (selectFunction(functionSelect, temp_lower) + selectFunction(functionSelect, temp_upper));
        }
        return sum;
    }

    private static double selectFunction(int select, double value){
        switch (select){
            case 1: return 4 / (1 + Math.pow(value, 2));
            case 2:
                if (value == 0 || value == 1)
                    return 0.0001;
                return Math.sqrt(value) * Math.log(value);
        }
        System.err.println("Function doesn't exist!");
        System.exit(2);
        return 0;
    }
}
