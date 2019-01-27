package com.org.worker.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConverterUtils {
    public static String[][] toTable(List<String> values, String separator) {
        if (values.size() > 0) {
            String[][] table = new String[values.size() ][];
            table[0] = values.get(0).split(separator);
            for (int i = 1; i < values.size(); i++) {
                String[] columns = new String[table[0].length];
                String[] separated = values.get(i).split(separator);
                for (int j = 0; j < table[0].length; j++) {
                    if (separated.length == 0) {
                        continue;
                    }
                    if (j == separated.length) {
                        columns[j] = StringUtils.EMPTY;
                    } else {
                        columns[j] = separated[j];
                    }
                }
                table[i] = columns;
            }
            return table;
        }
        return new String[][]{};
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
