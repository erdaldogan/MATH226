public class Main {
    public static void main(String[] args) {
        System.out.println(midpointRule(0, 1, 100, 2));
        System.out.println(trapezoidRule(0, 1, 100, 2));

    }

    private static double midpointRule(double a, double b, int n, int functionSelect){
        double h = (b - a) / n;
        double sum = 0;
        double lower, upper;
        for (double i = h; i <= b; i += h){
            lower = i - h;
            upper = i;
            double midpoint = (lower + upper) / 2;
            sum += selectFunction(functionSelect, midpoint);
        }
        return h * sum;
    }

    private static double trapezoidRule(double a, double b, int n, int functionSelect){
        double h = (b - a) / n;
        double sum = 0;
        double lower, upper;
        for (double i = h; i <=b; i += h){
            lower = i - h;
            upper = i;
            //System.out.printf("lower %.2f\n", selectFunction(functionSelect, lower));
            //System.out.printf("upper %.2f\n", selectFunction(functionSelect, upper));
            sum += (h / 2) * (selectFunction(functionSelect, lower) + selectFunction(functionSelect, upper));
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
        return -2;
    }

}
