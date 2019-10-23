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

public class Main{
    public static void main(String[] args) throws FileNotFoundException {
        Scanner inputA = new Scanner(new File("input_data/A.txt"));
        Scanner inputB = new Scanner(new File("input_data/B.txt"));
        ArrayList<ArrayList<Double>> matrixAList = new ArrayList<>();
        ArrayList<Double> matrixBList = new ArrayList<>();

        String[] tempString;
        while(inputA.hasNextLine()) { // reading the input file a
            ArrayList<Double> tempDouble = new ArrayList<>();
            tempString = inputA.nextLine().split("\\s+"); // split by space characters
            for (String num: tempString) // iterate over elements of row
                tempDouble.add(Double.parseDouble(num)); // parse string to double and add to arrayList
            matrixAList.add(tempDouble); // add row to 2d arrayList
        }
        while (inputB.hasNextDouble()) // reading the input file B
            matrixBList.add(inputB.nextDouble()); // assuming it is a vector

        // check for empty inputs
        if (matrixAList.isEmpty())
            throw new IllegalArgumentException("Given Text File(Arg1) Is Empty!");
        else if (matrixBList.isEmpty())
            throw new IllegalArgumentException("Given Text File(Arg2) Is Empty!");

        double[][] matrixA = matrixListToArray(matrixAList); // cast ArrayList to primitive array
        double[] matrixB = vectorListToArray(matrixBList);

        // check dimensions of the matrices to see if they are in appropriate size
        if (matrixAList.size() != matrixBList.size())
            throw new IllegalArgumentException("Row counts of A and b matrix must be equal!");
        else if(matrixA.length != matrixA[0].length)
            throw new IllegalArgumentException("Matrix A (first arg.) must be a square matrix!");

        System.gc(); // garbage collector

        // close inputs
        inputA.close();
        inputB.close();

        // print given matrices for user to check if they are correctly read etc.
        System.out.printf("\nA matrix(%dx%d)", matrixA.length, matrixA[0].length);
        printMatrix(matrixA);

        System.out.printf("\nB vector(%dx1)", matrixB.length);
        printMatrix(matrixB);

        solveSystem(matrixA, matrixB); // solve given system
    }

    private static void solveSystem(double[][] A, double[] b){
        boolean needsPartialPivoting = needsPartialPivoting(A); // decide if partial pivoting will be used or not
        double[][] L;
        double[][] U;
        double[] y;
        if (needsPartialPivoting){
            System.out.println("Partial Pivoting is used!");
            double[][][] LandU= getLUDecompositionPartialPivoting(A);
            L = LandU[0];
            U = LandU[1];
            double[][] P = LandU[2];
            double[] dummyB = matrixMultiplication(P, b);
            y = getForwardSubstitutionSolutionVector(L, dummyB);
        }
        else {
            double[][][] LandU= getLUDecompositionNoPivoting(A);
            L = LandU[0];
            U = LandU[1];
            y = getForwardSubstitutionSolutionVector(L, b);
        }

        double[] x = getBackwardSubstitutionSolutionVector(U, y);

        System.out.print("\nLower Triangular Matrix (L);");
        printMatrix(L);
        System.out.print("\nUpper Triangular Matrix (U);");
        printMatrix(U);
        if (needsPartialPivoting)
            System.out.print("\nLU Multiplication (PA Matrix);");
        else
            System.out.print("\nLU Multiplication;");

        printMatrix(matrixMultiplication(L,U));

        if (isCloseToZero(multiplyDiagonalElements(U)))
            throw new java.lang.ArithmeticException("The matrix is singular. (determinant ~= 0)");

        printResultVector(x);

        System.out.print("\nAx = ");
        printMatrix(matrixMultiplication(A, x));
    }

    private static double[][][] getLUDecompositionNoPivoting(double[][] a){
        int rowCount = a.length, colCount = a[0].length;
        if (rowCount != colCount)
            throw new IllegalArgumentException("Input must be a square matrix!");
        double[][] L = getIdentityMatrix(rowCount);
        double[][] U = new double[rowCount][colCount];
        double[][][] output = new double[2][rowCount][rowCount];
        U[0] = a[0].clone();
        for (int i = 1; i < rowCount; i++){
            L[i][0] = a[i][0] / U[0][0];
            for (int j = 1; j < colCount; j++){
                if (i <= j)
                    U[i][j] = a[i][j] - getSigmaSumForCalculatingL(L, U, i, j);
                else
                    L[i][j] = (a[i][j] - getSigmaSumForCalculatingL(L, U, i, j)) / U[j][j];
            }
        }
        output[0] = L;
        output[1] = U;
        return output;
    }

    private static double[][][] getLUDecompositionPartialPivoting(double[][] a){
        int rowCount = a.length, colCount = a[0].length; // number of rows, number of columns i.e. dimensions
        if (rowCount != colCount) // if input is not square matrix
            throw new IllegalArgumentException("Input must be a square matrix!");
        double[][][] output = new double[3][rowCount][rowCount];
        double[][] P = getIdentityMatrix(rowCount); // permutation matrix
        double[][] L = getIdentityMatrix(rowCount); // lower triangular matrix
        double[][] U = new double[rowCount][colCount]; // upper triangular matrix
        for (int i = 0; i < rowCount; i++) // perform deep-clone
            U[i] = a[i].clone();

        // iterate over all the columns, find the largest element in that column below the diagonal
        for (int i = 0; i < colCount; i++){
            int maxValueIndex = i;
            for (int j = i; j < rowCount; j++){ // iterate below the diagonal
                if (Math.abs(U[j][i]) > U[maxValueIndex][i]) // if greater than the value greatest value we located yet
                    maxValueIndex = j; // hold that index
            }
            interchangeRows(U, i, maxValueIndex); // row swap in upper matrix
            interchangeRows(P, i, maxValueIndex); // row swap in permutation matrix

            for (int k = i + 1; k < rowCount; k++){ // iterate below main diagonal
                double factor = U[k][i] / U[i][i]; // constant factor for a row
                L[k][i] = U[k][i] / U[i][i]; // calculate L value at index k,i
                U[k][i] = 0; // since we are iterating below main diagonal, all values must be zero for upper trig.
                for (int l = i + 1; l < colCount; l++) // calculate values of upper trig. matrix
                    U[k][l] = U[k][l] - (factor * U[i][l]);
            }

            if (i > 0) { // i is the column iterator. i > 0 means index is not at first column
                for (int b = i; b > 0; b--) { // row swap elements below the main diagonal at lower triangular matrix
                    double tempValue = L[i][b - 1]; // basic element swap
                    L[i][b - 1] = L[maxValueIndex][b - 1];
                    L[maxValueIndex][b - 1] = tempValue;
                }
            }
        }

        // return the L, U and P matrices
        output[0] = L;
        output[1] = U;
        output[2] = P;

        System.out.print("\nP Matrix;");
        printMatrix(P);
        return output;
    }

    // matrix multiplication of 2d matrices
    private static double[][] matrixMultiplication(double[][] a, double[][] b) throws IllegalArgumentException {
        int rowCountA = a.length, rowCountB = b.length,
                colCountA = a[0].length, colCountB = b[0].length; // dimensions
        if (colCountA != rowCountB)
            throw new IllegalArgumentException("These two matrices cannot be multiplied!\n" +
                    "Number of columns of the first matrix must be equal to number of rows of the second matrix!");

        double[][] output = new double[rowCountA][colCountB]; // declare output matrix
        for(int i = 0; i < rowCountA; i++){ // iterate over rows of A
            for (int j = 0; j < colCountB; j++){ // for each row of A, iterate over columns of B
                for (int k = 0; k < colCountA; k++) // iterate over elements of given row of A and column of B
                    output[i][j] += a[i][k] * b[k][j]; // multiply value from input matrices and sum
            }
        }
        return output;
    }

    // matrix multiplication for 2d and a 1d matrix
    private static double[] matrixMultiplication(double[][] a, double[] b) throws IllegalArgumentException {
        int rowCountA = a.length, rowCountB = b.length,
                colCountA = a[0].length;
        if (colCountA != rowCountB)
            throw new IllegalArgumentException("These two matrices cannot be multiplied!\n" +
                    "Number of columns of the first matrix must be equal to number of rows of the second matrix!");

        double[] output = new double[rowCountA];
        for(int i = 0; i < rowCountA; i++){
                for (int k = 0; k < colCountA; k++){
                    output[i] += a[i][k] * b[k];
            }
        }
        return output;
    }

    // get sigma summation in doolittle algorithm
    private static double getSigmaSumForCalculatingL(double[][] L, double[][] U, int rowIndex, int colIndex){
        double total = 0;
        for (int i = 0; i < rowIndex; i++)
            total += L[rowIndex][i] * U[i][colIndex];
        return total;
    }

    // perform fw substitution and return result vector
    private static double[] getForwardSubstitutionSolutionVector(double[][] L, double[] b){
        int rowCount = L.length, colCount = L[0].length; // dimensions
        if (rowCount != colCount)
            throw new IllegalArgumentException("Lower Triangular matrix must be square!");
        if (colCount != b.length)
            throw new IllegalArgumentException("b vector has inappropriate size!");
        double[] y = new double[rowCount]; // output matrix
        y[0] = b[0] / L[0][0];
        for (int i = 1; i < rowCount; i++)
            y[i] = (b[i] - getSigmaSumForFWSubstitution(L, y, i)) / L[i][i];
        return y;
    }

    // get the summation part in the fw subst. equation
    private static double getSigmaSumForFWSubstitution(double[][] L, double[] y, int index){ // index of x
        double sum = 0;
        for (int i = 0; i < index; i++)
            sum += L[index][i] * y[i];

        return sum;
    }

    // perform bw substitution and return result vector
    private static double[] getBackwardSubstitutionSolutionVector(double[][] U, double[] y){
        int rowCount = U.length, colCount = U[0].length;
        if (rowCount != colCount)
            throw new IllegalArgumentException("Lower Triangular matrix must be square!");
        if (colCount != y.length)
            throw new IllegalArgumentException("b vector has inappropriate size!");

        double[] x = new double[rowCount];
        int lastIndex = rowCount - 1;
        x[lastIndex] = y[lastIndex] / U[lastIndex][lastIndex];
        for (int i = lastIndex - 1; i >= 0; i--)
            x[i] = (y[i] - getSigmaSumForBWSubstitution(U, x, i)) / U[i][i];
        return x;
    }

    // get the summation part in the bw subst. equation
    private static double getSigmaSumForBWSubstitution(double[][] U, double[] x, int index){
        int rowCount = U.length;
        double sum = 0;
        for (int i = index + 1; i < rowCount; i++)
            sum += U[index][i] * x[i];

        return sum;
    }

    // perform row interchange on given matrix for given row indices row1 and row2
    private static void interchangeRows(double[][] a, int row1, int row2){
        if (row1 != row2){ // perform if given indies are not equal, cannot swap row 3 with row 3 :)
            double[] temp = a[row1]; // basic variable swapping
            a[row1] = a[row2];
            a[row2] = temp;
        }
    }

    // return square identity matrix for given dimension, i.e. when len = 5, return 5x5 identity matrix
    private static double[][] getIdentityMatrix(int len){
        double[][] output = new double[len][len];
        for (int i = 0; i < len; i++)
            output[i][i] = 1;
        return output;
    }

    // print 2d matrix
    private static void printMatrix(double[][] matrix){
        System.out.print("\n");
        for (double[] row: matrix){
            for (double element: row)
                System.out.printf("%10.2f", element);
            System.out.print("\n");
        }
    }

    // print 1d matrix
    private static void printMatrix(double[] vector){
        System.out.print("\n");
        for (double num: vector)
            System.out.printf("%10.2f \n", num);
        System.out.print("\n");
    }

    // print vector with variable names such as x1, x2 etc.
    private static void printResultVector(double[] vector){
        int len = vector.length;
        System.out.print("\nResult Vector\n");
        for (int i = 0; i < len; i++)
            System.out.printf("\tx%1d = %7.2f\n", i, vector[i]); // output format: x1 = 0.33
    }

    // 2d arraylist to primitive array
    private static double[][] matrixListToArray(ArrayList<ArrayList<Double>> arrayList){
        int rowCount = arrayList.size(), columnCount = arrayList.get(0).size();
        double[][] output = new double[rowCount][columnCount];
        for (int i = 0; i < rowCount; i++){
            for (int j = 0; j < columnCount; j++)
                output[i][j] = arrayList.get(i).get(j);
        }
        return output;
    }

    // arraylist to primitive array
    private static double[] vectorListToArray(ArrayList<Double> vector){
        int rowCount = vector.size();
        double[] output = new double[rowCount];
        for (int i = 0; i < rowCount; i++)
            output[i] = vector.get(i);
        return output;
    }

    // check if there are any ~0's in pivot positions of a given matrix. If yes; return true
    private static boolean needsPartialPivoting(double[][] a){
        for (int i = 0; i < a.length; i++){
            if (isCloseToZero(a[i][i])) // equal to 0 or close to zero at main diagonal
                return true;
        }
        return false;
    }

    // multiply diagonal elements of given matrix
    private static double multiplyDiagonalElements(double[][] array){
        int rowCount = array.length;
        double result = 1;
        for (int i = 0; i < rowCount; i++){
            if (array[i][i] == 0)
                return 0;
                result *= array[i][i];
        }
        return result;
    }

    // return true if given input is close to zero
    private static boolean isCloseToZero(double num){
        return Math.abs(num) < 0.001 || num == 0;
    }
}