package mergesort;

import java.util.concurrent.ForkJoinPool;

public class MergeSort<T extends Comparable<T>> implements Runnable {
    private T[] array;
    private final int numberOfThreads;
    private final ForkJoinPool pool;
    private final int minSizeForParallel;

    public MergeSort(T[] array, int numberOfThreads, int minSizeForParallel) {
        if (numberOfThreads < 1) {
            throw new IllegalArgumentException("numberOfThreads must be at least 1");
        }
        this.numberOfThreads = numberOfThreads;
        this.pool = new ForkJoinPool(numberOfThreads);
        this.array = array;
        this.minSizeForParallel = minSizeForParallel;
    }

    public static <T extends Comparable<T>> void merge(T[] array, int left, int mid, int right) {
        Object[] newArray = new Object[right - left + 1];
        int index = 0;
        int i = left;
        int j = mid + 1;
        while (i <= mid && j <= right) {
            if (array[i].compareTo(array[j]) <= 0) {
                newArray[index++] = array[i++];
            } else {
                newArray[index++] = array[j++];
            }
        }
        while (i <= mid) {
            newArray[index++] = array[i++];
        }
        while (j <= right) {
            newArray[index++] = array[j++];
        }
        for (int k = 0; k < newArray.length; k++) {
            T val = (T) newArray[k];
            array[left + k] = val;
        }
    }

    private void normalMergeSort(int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            normalMergeSort(left, mid);
            normalMergeSort(mid + 1, right);
            merge(this.array, left, mid, right);
        }
    }

    private void parallelMergeSort(int left, int right) {
        if (right - left + 1 < minSizeForParallel | left >= right) {
            normalMergeSort(left, right);
            return;
        }
        pool.invoke(new Task<T>(this.array, left, right, minSizeForParallel));
    }

    @Override
    public void run() {
        if (numberOfThreads == 1) {
            normalMergeSort(0, array.length - 1);
        } else {
            parallelMergeSort(0, array.length - 1);
        }
        pool.shutdown();
    }

    public static <T extends Comparable<T>> boolean isSorted(T[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1].compareTo(array[i]) > 0) {
                return false;
            }
        }
        return true;
    }
}
