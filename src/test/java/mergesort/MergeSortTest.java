package mergesort;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MergeSortTest {

    @Test
    void testMerge() {
        Integer[] array = {1,2,3,4,1,2,3,4};
        MergeSort.merge(array, 0, 3, 7);
        Integer[] expected = {1,1,2,2,3,3,4,4};
        assertArrayEquals(expected, array);
    }

    @Test
    void testNormalMergeSort() {
        Integer[] array = {4,3,2,1};
        MergeSort<Integer> mergeSort = new MergeSort<>(array, 1, 2);
        mergeSort.run();
        Integer[] expected = {1,2,3,4};
        assertArrayEquals(expected, array);
    }

    @Test
    void testParallelMergeSort() {
        Integer[] array = {4, 3, 2, 1,91,51,5,51,9,5,2,520,89,0,59,50,50,50};
        MergeSort<Integer> mergeSort = new MergeSort<>(array, 4, 5);
        mergeSort.run();
        assertTrue(MergeSort.isSorted(array));
    }

    @Test
    void testMergeSortWithStrings() {
        String[] array = {"a", "b", "d", "ab", "abc"};
        MergeSort<String> mergeSort = new MergeSort<>(array, 2, 2);
        mergeSort.run();
        String[] expected = {"a", "ab", "abc", "b", "d"};
        assertArrayEquals(expected, array);
    }

    @Test
    void testMergeSortWithDouble() {
        Double[] array = {3.1, 2.2, 5.5, 4.4, 1.1};
        MergeSort<Double> mergeSort = new MergeSort<>(array, 3, 2);
        mergeSort.run();
        Double[] expected = {1.1, 2.2, 3.1, 4.4, 5.5};
        assertArrayEquals(expected, array);
    }


}