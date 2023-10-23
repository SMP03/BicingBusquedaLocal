package IA.BicingBusquedaLocal;

import java.util.Comparator;

public class DoubleArrayComparator implements Comparator<Double[]> {
    @Override
    public int compare(Double[] arr1, Double[] arr2) {
        Double firstElement1 = arr1[0];
        Double firstElement2 = arr2[0];

        // Compare the first elements
        return firstElement1.compareTo(firstElement2);
    }
}