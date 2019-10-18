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

    //private static int[] getColumn(int[][] x, int colIndex){
      //  int[] output = new int[x[0].length];

    //}

    private static void printMatrix(int[][] matrix){
        System.out.print("\n");
        for (int[] row: matrix){
            for (int index: row)
                System.out.printf("%5d", index);
            System.out.print("\n");
        }
    }
}
