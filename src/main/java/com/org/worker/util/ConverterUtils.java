package com.org.worker.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConverterUtils {
    public static String[][] toTable(List<String> values, String separator) {
        String[][] table = new String[values.size()][];
        for (int i = 0; i < values.size(); i++) {
            String[] columns = values.get(i).split(separator);
            table[i] = columns;
        }
        return table;
    }

    public static String[][] transport(String[][] matrix) {
        String[][] m = new String[matrix[0].length][matrix.length];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                m[j][i] = matrix[i][j];
            }
        }
        return m;
    }


}
