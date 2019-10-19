//-----------------------------------------//
// MATH226 - Numerical Methods for EE
// Project 01
//
// Name-Surname: Erdal Sidal Dogan
// Student ID: 041702023
//-----------------------------------------//
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner inputA = new Scanner(new File("A.txt"));
        Scanner inputB = new Scanner(new File("B.txt"));
        int[] dimensionsA = {inputA.nextInt(), inputA.nextInt()};
        int[] dimensionsB = {inputB.nextInt(), inputB.nextInt()};
        double[][] matrixA = new double[dimensionsA[0]][dimensionsA[1]];
        double[][] matrixB = new double[dimensionsB[0]][dimensionsB[1]];

        /**
        int row = 0;
        while(inputA.hasNextLine())
            matrixA[row] = getArrayOfDoubles(inputA.nextLine().split("\\s+"));
        row = 0;
        while (inputB.hasNextLine())
            matrixB[row] = getArrayOfDoubles(inputB.nextLine().split("\\s+"));

        System.gc();
        */

        for (int i = 0; i < dimensionsA[0]; i++){
            for (int j = 0; j < dimensionsA[1]; j++)
                matrixA[i][j] = inputA.nextDouble();
        }

        for (int i = 0; i < dimensionsB[0]; i++){
            for (int j = 0; j < dimensionsB[1]; j++)
                matrixB[i][j] = inputB.nextDouble();
        }

        getLUDecompositionPartialPivoting(matrixA);


        inputA.close();
        inputB.close();

    }

    private static double[] getForwardSubstitutionSolutionVector(double[][] L, double[] b){
        int rowCount = L.length, colCount = L[0].length;
        double[] y = new double[rowCount];
        y[0] = b[0];
        for (int i = 1; i < rowCount; i++)
            y[i] = b[i] - getSigmaSumForFWSubstitution(L, y, i);
        return y;
    }

    private static double[] getBackwardSubstitutionSolutionVector(double[][] U, double[] y){
        int rowCount = U.length, colCount = U[0].length;
        double[] x = new double[rowCount];

    }

    private static void getLUDecompositionNoPivoting(double[][] a){
        int rowCount = a.length, colCount = a[0].length;
        if (rowCount != colCount)
            throw new IllegalArgumentException("Input must be a square matrix!");
        double[][] L = getIdentityMatrix(rowCount);
        double[][] U = new double[rowCount][colCount];
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
        System.out.print("\nLower Triangular Matrix (L);");
        printMatrix(L);
        System.out.print("\nUpper Triangular Matrix (U);");
        printMatrix(U);
        System.out.print("\n(LU);");
        printMatrix(matrixMultiplication(L,U));

    }

    private static void getLUDecompositionPartialPivoting(double[][] a){
        int rowCount = a.length, colCount = a[0].length; // number of rows, number of columns i.e. dimensions
        if (rowCount != colCount) // if input is not square matrix
            throw new IllegalArgumentException("Input must be a square matrix!");

        double[][] P = getIdentityMatrix(rowCount); // permutation matrix
        double[][] L = getIdentityMatrix(rowCount); // lower triangular matrix
        double[][] U = new double[rowCount][colCount]; // upper triangular matrix
        for (int i = 0; i < rowCount; i++) // perform deep-clone
            U[i] = a[i].clone();
        for (int i = 0; i < colCount; i++){
            int maxValueIndex = i;
            for (int j = i; j < rowCount; j++){
                if (Math.abs(U[j][i]) > U[maxValueIndex][i])
                    maxValueIndex = j;
            }
            interchangeRows(U, i, maxValueIndex);
            interchangeRows(P, i, maxValueIndex);
            if (i > 0) {
                for (int b = i; b > 0; b--) {
                    double tempValue = L[i][b - 1];
                    L[i][b - 1] = L[maxValueIndex][b - 1];
                    L[maxValueIndex][b - 1] = tempValue;
                }
            }
            //System.out.println("Row-Interchanged L");
            //printMatrix(L);
            for (int k = i + 1; k < rowCount; k++){
                double factor = U[k][i] / U[i][i];
                L[k][i] = U[k][i] / U[i][i];
                U[k][i] = 0;
                for (int l = i + 1; l < colCount; l++)
                    U[k][l] = U[k][l] - (factor * U[i][l]);
            }
        }
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
    }

    private static double[][] matrixMultiplication(double[][] a, double[][] b) throws IllegalArgumentException {
        int rowCountA = a.length, rowCountB = b.length,
                colCountA = a[0].length, colCountB = b[0].length;
        if (colCountA != rowCountB)
            throw new IllegalArgumentException("These two matrices cannot be multiplied!\n" +
                    "Number of columns of the first matrix must be equal to number of rows of the second matrix!");

        double[][] output = new double[rowCountA][colCountB];
        for(int i = 0; i < rowCountA; i++){
            for (int j = 0; j < colCountB; j++){
                for (int k = 0; k < colCountA; k++){
                    output[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return output;
    }

    private static double getSigmaSumForCalculatingL(double[][] L, double[][] U, int rowIndex, int colIndex){
        int total = 0;
        for (int i = 0; i < rowIndex; i++)
            total += L[rowIndex][i] * U[i][colIndex];
        return total;
    }

    private static double getSigmaSumForFWSubstitution(double[][] L, double[] y, int index){ // index of x
        double sum = 0;
        for (int i = 0; i < index - 1; i++)
            sum += L[index][i] * y[i];
        return sum;
    }

    private static void interchangeRows(double[][] a, int row1, int row2){
        if (row1 == row2){} // do nothing
        else {
            double[] temp = a[row1]; // basic variable swapping
            a[row1] = a[row2];
            a[row2] = temp;
        }
    }

    private static double[][] getIdentityMatrix(int len){
        double[][] output = new double[len][len];
        for (int i = 0; i < len; i++)
            output[i][i] = 1;
        return output;
    }

    private static void printMatrix(double[][] matrix){
        System.out.print("\n");
        for (double[] row: matrix){
            for (double index: row)
                System.out.printf("%10.2f", index);
            System.out.print("\n");
        }
    }
    /*
    private static double[] getArrayOfDoubles(String[] strings){
        int length = strings.length;
        double[] output = new double[length];
        for (int i = 0; i < length; i++)
            output[i] = Double.parseDouble(strings[i]);
        return output;
    }
     */
}

// Is it okay to have dimensions given at the beginning of the input files
// For partial pivoting; PA=PLU or A=LU
