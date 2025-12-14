package mergesort;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TimeComparison {
    private int calculateTime(int numberOfThreads, int minSizeForParallel, int arraySize) {
        Integer[] array = new Integer[arraySize];
        for (int i = 0; i < arraySize; i++) {
            array[i] = (int) (Math.random() * 100);
        }
        long startTime = System.nanoTime();
        MergeSort<Integer> mergeSort = new MergeSort<Integer>(array, numberOfThreads, minSizeForParallel);
        mergeSort.run();
        long endTime = System.nanoTime();
        return (int) ((endTime - startTime) / 1_000_000);
    }

    private void writeComparison(BufferedWriter out, int numberOfThreads, int minSizeForParallel, int arraySize)
        throws IOException {
        int singleThreadTime = new TimeComparison().calculateTime(1, 1, arraySize);
        int multiThreadTime = new TimeComparison().calculateTime(numberOfThreads, minSizeForParallel, arraySize);
        out.write("Array Size: " + arraySize + "\n");
        out.write("Single Thread Time " + singleThreadTime + " ms\n");
        out.write(
            "Multi Thread Time threads = " + numberOfThreads + ", minSizeForParallel = " + minSizeForParallel + ": " +
                multiThreadTime + " ms\n");
        out.write("--------------------------\n");

    }

    public void writeComparisonsToFile(String filename)
        throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filename))) {
            writeComparison(out, 4, 1000, 1_000_000);
            writeComparison(out, 4, 1000, 10_000_000);
            writeComparison(out, 4, 100, 100_000);
            writeComparison(out, 4, 1, 100_000);
            writeComparison(out, 8, 100_000_000, 100_000);
            out.flush();
        }
    }

    public static void main(String[] args) throws IOException {
        TimeComparison timeComparison = new TimeComparison();
        timeComparison.writeComparisonsToFile("merge_sort_times.txt");

    }
}
