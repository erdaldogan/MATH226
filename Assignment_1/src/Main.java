import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner inputA = new Scanner(new File("A.txt"));
        Scanner inputB = new Scanner(new File("B.txt"));
        int[] dimensionsA = {inputA.nextInt(), inputA.nextInt()};
        int[] dimensionsB = {inputB.nextInt(), inputB.nextInt()};
        int[][] matrixA = new int[dimensionsA[0]][dimensionsA[1]];
        int[][] matrixB = new int[dimensionsB[0]][dimensionsB[1]];

        for (int i = 0; i < dimensionsA[0]; i++){
            for (int j = 0; j < dimensionsA[1]; j++)
                matrixA[i][j] = inputA.nextInt();
        }

        for (int i = 0; i < dimensionsB[0]; i++){
            for (int j = 0; j < dimensionsB[1]; j++)
                matrixB[i][j] = inputB.nextInt();
        }

        getLUDecompositionNoPivoting(matrixA);

        interchangeRows(getIdentityMatrix(3), 0, 2);


        inputA.close();
        inputB.close();

    }
    private static int[][] matrixMultiplication(int[][] a, int[][] b) throws IllegalArgumentException {
        int rowCountA = a.length, rowCountB = b.length,
                colCountA = a[0].length, colCountB = b[0].length;
        if (colCountA != rowCountB)
            throw new IllegalArgumentException("These two matrices cannot be multiplied!\n" +
                    "Number of columns of the first matrix must be equal to number of rows of the second matrix!");

        int[][] output = new int[rowCountA][colCountB];
        for(int i = 0; i < rowCountA; i++){
            for (int j = 0; j < colCountB; j++){
                for (int k = 0; k < colCountA; k++){
                    output[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return output;
    }

    private static void getLUDecompositionNoPivoting(int[][] a){
        int rowCount = a.length, colCount = a[0].length;
        if (rowCount != colCount)
            throw new IllegalArgumentException("Input must be a square matrix!");
        int[][] L = getIdentityMatrix(rowCount);
        int[][] U = new int[rowCount][colCount];
        U[0] = a[0].clone();
        for (int i = 1; i < rowCount; i++){
            L[i][0] = a[i][0] / U[0][0];
            for (int j = 1; j < colCount; j++){
                if (i <= j)
                    U[i][j] = a[i][j] - getSigmaSum(L, U, i, j);
                else
                    L[i][j] = (a[i][j] - getSigmaSum(L, U, i, j)) / U[j][j];
            }

        }
        System.out.printf("\nLower Triangular Matrix (L);");
        printMatrix(L);
        System.out.printf("\nUpper Triangular Matrix (U);");
        printMatrix(U);


    }

    private static int getSigmaSum(int[][] L, int[][] U, int rowIndex, int colIndex){
        int total = 0;
        for (int i = 0; i < rowIndex; i++)
            total += L[rowIndex][i] * U[i][colIndex];
        return total;
    }

    private static void interchangeRows(int[][] a, int row1, int row2){
        int[] temp = a[row1];
        a[row1] = a[row2];
        a[row2] = temp;

        System.out.print("\nRow-Interchanged Matrix;");
        printMatrix(a);
    }


    private static int[][] getIdentityMatrix(int len){
        int[][] output = new int[len][len];
        for (int i = 0; i < len; i++)
            output[i][i] = 1;
        return output;
    }

    private static void printMatrix(int[][] matrix){
        System.out.print("\n");
        for (int[] row: matrix){
            for (int index: row)
                System.out.printf("%5d", index);
            System.out.print("\n");
        }
    }
}
