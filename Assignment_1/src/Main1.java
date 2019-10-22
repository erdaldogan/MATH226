//-----------------------------------------//
// MATH226 - Numerical Methods for EE
// Project 01
//
// Name-Surname: Erdal Sidal Dogan
// Student ID: 041702023
//-----------------------------------------//
import java.io.File;
        import java.io.FileNotFoundException;
        import java.util.ArrayList;
        import java.util.Scanner;

public class Main1{
    public static void main(String[] args) throws FileNotFoundException {
        Scanner inputA = new Scanner(new File("input_data/A.txt"));
        Scanner inputB = new Scanner(new File("input_data/B.txt"));
        ArrayList<ArrayList<Double>> matrixA = new ArrayList<>();
        ArrayList<Double> matrixB = new ArrayList<>();

        String[] tempString;
        while(inputA.hasNextLine()) {
            ArrayList<Double> tempDouble = new ArrayList<>();
            tempString = inputA.nextLine().split("\\s+");
            for (String num: tempString)
                tempDouble.add(Double.parseDouble(num));
            matrixA.add(tempDouble);
        }

        while (inputB.hasNextDouble())
            matrixB.add(inputB.nextDouble());

        System.gc();



        inputA.close();
        inputB.close();

        System.out.print("\nA matrix");
        printMatrix(matrixA);

        System.out.print("\nB vector");
        printVector(matrixB);

        ArrayList<ArrayList<ArrayList<Double>>> res = getLUDecompositionNoPivoting(matrixA);
        System.out.println("L Matrix");
        printMatrix(res.get(0));
        System.out.println("U Matrix");
        printMatrix(res.get(1));
        System.out.println("LU Matrix");
        printMatrix(matrixMultiplication(res.get(0), res.get(1)));

    }

    private static void solveSystem(ArrayList<ArrayList<Double>> A, ArrayList<Double> b){
        ArrayList<ArrayList<Double>> L = getLUDecompositionNoPivoting(A).get(0);
        ArrayList<ArrayList<Double>> U = getLUDecompositionNoPivoting(A).get(1);

        ArrayList<Double> y = getForwardSubstitutionSolutionVector(L, b);
        ArrayList<Double> x = getBackwardSubstitutionSolutionVector(U, y);

        System.out.print("\nLower Triangular Matrix (L);");
        printMatrix(L);
        System.out.print("\nUpper Triangular Matrix (U);");
        printMatrix(U);
        System.out.print("\nLU Multiplication;");
        printMatrix(matrixMultiplication(L,U));

        printResultVector(x);

    }




    private static ArrayList<ArrayList<ArrayList<Double>>> getLUDecompositionNoPivoting(ArrayList<ArrayList<Double>> a){
        int rowCount = a.size(), colCount = a.get(0).size();
        if (rowCount != colCount)
            throw new IllegalArgumentException("Input must be a square matrix!");
        ArrayList<ArrayList<Double>> L = getIdentityMatrix(rowCount);
        ArrayList<ArrayList<Double>> U = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Double>>> output = new ArrayList<>();
        U.add(0, a.get(0));
        for (int i = 1; i < rowCount; i++){
            L.get(i).add(0, a.get(i).get(0) / U.get(0).get(0));
            for (int j = 1; j < colCount; j++){
                if (i <= j)
                    U.get(i).add(j, a.get(i).get(j) - getSigmaSumForCalculatingL(L, U, i, j));
                else
                    L.get(i).add(j, (a.get(i).get(j) - getSigmaSumForCalculatingL(L, U, i, j)) / U.get(j).get(j));
            }
        }

        /*
        System.out.print("\nLower Triangular Matrix (L);");
        printMatrix(L);
        System.out.print("\nUpper Triangular Matrix (U);");
        printMatrix(U);
        System.out.print("\n(LU);");
        printMatrix(matrixMultiplication(L,U));
        */

        output.add(0, L);
        output.add(1, U);
        return output;

    }

    private static ArrayList<ArrayList<ArrayList<Double>>> getLUDecompositionPartialPivoting(ArrayList<ArrayList<Double>> a){
        int rowCount = a.size(), colCount = a.get(0).size(); // number of rows, number of columns i.e. dimensions
        if (rowCount != colCount) // if input is not square matrix
            throw new IllegalArgumentException("Input must be a square matrix!");
        ArrayList<ArrayList<ArrayList<Double>>> output = new ArrayList<>();
        ArrayList<ArrayList<Double>> P = getIdentityMatrix(rowCount); // permutation matrix
        ArrayList<ArrayList<Double>> L = getIdentityMatrix(rowCount); // lower triangular matrix
        ArrayList<ArrayList<Double>> U = new ArrayList<>(); // upper triangular matrix
        for (int i = 0; i < rowCount; i++) // perform deep-clone
            U.add(i, a.get(i));
        for (int i = 0; i < colCount; i++){
            int maxValueIndex = i;
            for (int j = i; j < rowCount; j++){
                if (Math.abs(U.get(j).get(i)) > U.get(maxValueIndex).get(i))
                    maxValueIndex = j;
            }
            interchangeRows(U, i, maxValueIndex);
            interchangeRows(P, i, maxValueIndex);
            if (i > 0) {
                for (int b = i; b > 0; b--) {
                    double tempValue = L.get(i).get(b - 1);
                    L.get(i).add(b-1, L.get(maxValueIndex).get(b - 1));
                    L.get(maxValueIndex).add(b - 1,  tempValue);
                }
            }
            //System.out.println("Row-Interchanged L");
            //printMatrix(L);
            for (int k = i + 1; k < rowCount; k++){
                double factor = U.get(k).get(i) / U.get(i).get(i);
                L.get(k).add(i,  U.get(k).get(i) / U.get(i).get(i));
                U.get(k).add(i, 0.0);
                for (int l = i + 1; l < colCount; l++)
                    U.get(k).add(l,U.get(k).get(l) - (factor * U.get(i).get(l)));
            }
        }

        output.add(0, L);
        output.add(1, U);
        /*
        System.out.println("P Matrix");
        printMatrix(P);
        System.out.println("L Matrix;");
        printMatrix(L);
        System.out.println("U Matrix;");
        printMatrix(U);

        System.out.println("LU;");
        printMatrix(matrixMultiplication(L,U));

        System.out.println("PA;");
        printMatrix(matrixMultiplication(P, a));
        */
        return output;
    }

    private static ArrayList<ArrayList<Double>> matrixMultiplication(ArrayList<ArrayList<Double>> a, ArrayList<ArrayList<Double>> b) throws IllegalArgumentException {
        int rowCountA = a.size(), rowCountB = b.size(),
                colCountA = a.get(0).size(), colCountB = b.get(0).size();
        if (colCountA != rowCountB)
            throw new IllegalArgumentException("These two matrices cannot be multiplied!\n" +
                    "Number of columns of the first matrix must be equal to number of rows of the second matrix!");

        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();
        double temp = 0;
        for(int i = 0; i < rowCountA; i++){
            for (int j = 0; j < colCountB; j++){
                for (int k = 0; k < colCountA; k++){
                    temp += a.get(i).get(k) * b.get(k).get(j);
                    output.get(i).add(j, temp);
                }
            }
        }
        return output;
    }

    private static double getSigmaSumForCalculatingL(ArrayList<ArrayList<Double>> L,
                                                     ArrayList<ArrayList<Double>> U, int rowIndex,
                                                     int colIndex){
        int total = 0;
        for (int i = 0; i < rowIndex; i++)
            total += L.get(rowIndex).get(i) * U.get(i).get(colIndex);
        return total;
    }

    private static ArrayList<Double> getForwardSubstitutionSolutionVector(ArrayList<ArrayList<Double>> L, ArrayList<Double> b){
        int rowCount = L.size(), colCount = L.get(0).size();
        if (rowCount != colCount)
            throw new IllegalArgumentException("Lower Triangular matrix must be square!");
        if (colCount != b.size())
            throw new IllegalArgumentException("b vector has inappropriate size!");
        ArrayList<Double> y = new ArrayList<>();
        y.add(0, b.get(0) / L.get(0).get(0));
        for (int i = 1; i < rowCount; i++)
            y.add(i, (b.get(i) - getSigmaSumForFWSubstitution(L, y, i)) / L.get(i).get(i));
        return y;
    }

    private static double getSigmaSumForFWSubstitution(ArrayList<ArrayList<Double>> L, ArrayList<Double> y, int index){ // index of x
        double sum = 0;
        for (int i = 0; i < index; i++)
            sum += L.get(index).get(i) * y.get(i);

        return sum;
    }

    private static ArrayList<Double> getBackwardSubstitutionSolutionVector(ArrayList<ArrayList<Double>> U, ArrayList<Double> y){
        int rowCount = U.size(), colCount = U.get(0).size();
        if (rowCount != colCount)
            throw new IllegalArgumentException("Lower Triangular matrix must be square!");
        if (colCount != y.size())
            throw new IllegalArgumentException("b vector has inappropriate size!");

        ArrayList<Double> x = new ArrayList<>();
        int lastIndex = rowCount - 1;
        x.add(lastIndex, y.get(lastIndex) / U.get(lastIndex).get(lastIndex));
        for (int i = lastIndex - 1; i >= 0; i--)
            x.add(i, y.get(i) - getSigmaSumForBWSubstitution(U, x, i) / U.get(i).get(i));
        return x;
    }

    private static double getSigmaSumForBWSubstitution(ArrayList<ArrayList<Double>> U, ArrayList<Double> x, int index){
        int rowCount = U.size();
        double sum = 0;
        for (int i = index + 1; i < rowCount; i++)
            sum += U.get(index).get(1) * x.get(i);

        return sum;
    }

    private static void interchangeRows(ArrayList<ArrayList<Double>> a, int row1, int row2){
        if (row1 == row2){} // do nothing
        else {
            ArrayList<Double> temp = a.get(row1); // basic variable swapping
            a.add(row1, a.get(row2));
            a.add(row2, temp);
        }
    }

    private static ArrayList<ArrayList<Double>> getIdentityMatrix(int len){
        ArrayList<Double> zeros = getZerosList(len);
        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();
        for (int i = 0; i < len; i++)
            zeros.add(i, 1.0);
        output.add( zeros);
        return output;
    }

    private static ArrayList<Double> getZerosList(int len){
        ArrayList<Double> zerosList = new ArrayList<>();
        for (int i = 0; i < len; i++)
            zerosList.add(0.0);
        return zerosList;
    }

    private static void printMatrix(ArrayList<ArrayList<Double>> matrix){
        System.out.print("\n");
        for (ArrayList<Double> row: matrix){
            for (Double element: row)
                System.out.printf("%10.2f", element);
            System.out.print("\n");
        }
    }

    private static void printVector(ArrayList<Double> vector){
        System.out.print("\n");
        for (double num: vector)
            System.out.printf("%10.2f \n", num);
        System.out.print("\n");

    }

    private static void printResultVector(ArrayList<Double> vector){
        int len = vector.size();
        System.out.print("\nResult Vector\n");
        for (int i = 0; i < len; i++)
            System.out.printf("x%1d = %7.2f\n", i, vector.get(i));
    }

    /*
    private static ArrayList<Double> getArrayOfDoubles(String[] strings){
        int length = strings.length;
        ArrayList<Double> output = new double[length];
        for (int i = 0; i < length; i++)
            output[i] = Double.parseDouble(strings[i]);
        return output;
    }
     */

    /*
    private static ArrayList<ArrayList<Double>> toPrimitiveArray(ArrayList<ArrayList<Double>> input){
        ArrayList<ArrayList<Double>> output;
        for (ArrayList<Double> x: input){
            for (Double y: x){

            }
        }
*/
}

// Is it okay to have dimensions given at the beginning of the input files
// For partial pivoting; PA=PLU or A=LU
