package mergesort;

import java.util.concurrent.RecursiveAction;

class Task<T extends Comparable<T>> extends RecursiveAction {
    private final int left;
    private final int right;
    private final T[] array;
    private final int minSizeForParallel;

    Task(T[] arr, int left, int right, int minSizeForParallel) {
        this.array = arr;
        this.left = left;
        this.right = right;
        this.minSizeForParallel = minSizeForParallel;
    }

    @Override
    protected void compute() {
        if (right - left < minSizeForParallel) {
            normalMergeSort(left, right);
            return;
        }
        int mid = (left + right) / 2;
        Task<T> task1 = new Task<T>(array, left, mid, minSizeForParallel);
        Task<T> task2 = new Task<T>(array, mid + 1, right, minSizeForParallel);

        invokeAll(task1, task2);
        MergeSort.merge(array, left, mid, right);
    }

    private void normalMergeSort(int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            normalMergeSort(left, mid);
            normalMergeSort(mid + 1, right);
            MergeSort.merge(array, left, mid, right);
        }
    }
}