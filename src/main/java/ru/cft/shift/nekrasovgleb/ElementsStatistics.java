package ru.cft.shift.nekrasovgleb;

import java.lang.reflect.Field;

public class ElementsStatistics<T extends Number & Comparable<T>> {
    private T minElement;
    private T maxElement;
    private T elementsSum;
    private int elementsCount;

    public ElementsStatistics() {
        minElement = null;
        maxElement = null;
        elementsSum = null;
        elementsCount = 0;
    }

    public T getMin() {
        return minElement;
    }

    public T getMax() {
        return maxElement;
    }

    public T getSum() {
        return elementsSum;
    }

    public int getCount() {
        return elementsCount;
    }

    public void updateCount() {
        elementsCount++;
    }

    public void updateAll(T newElement) {
        if (newElement == null) {
            return;
        }

        //noinspection unchecked
        elementsSum = (T) calcSum(elementsSum, newElement);

        if (minElement == null) {
            minElement = newElement;
            maxElement = newElement;
            elementsCount++;

            return;
        }

        if (newElement.getClass() != minElement.getClass()) {
            System.out.println("При подсчете статистики возникла ошибка из-за несовпадения класса нового элемента " +
                    "с классом предыдущих элементов.");
            return;
        }

        if (newElement.compareTo(minElement) < 0) {
            minElement = newElement;
        }

        if (newElement.compareTo(maxElement) > 0) {
            maxElement = newElement;
        }

        elementsCount++;
    }

    public static <T extends Number> Number calcSum(T x, T y) {
        try {
            if (x == null) {
                return y;
            }

            if (y == null) {
                return x;
            }

            Field primitiveField = x.getClass().getField("TYPE");
            var primitiveType = primitiveField.get(null);
            //noinspection rawtypes
            var adder = x.getClass().getMethod("sum", (Class)primitiveType,(Class)primitiveType);
            var result = adder.invoke(null, x, y);
            return (Number) result;
        } catch (Exception e) {
            System.out.println("При расчете суммы чисел возникла ошибка");
            return null;
        }
    }
}
