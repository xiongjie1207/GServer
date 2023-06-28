package com.wegame.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author xiongjie
 * @Date 2023/03/04 22:23
 **/
public class TableUtils {
    public static Table<String,String,String> createTableFromCSV(String file) throws IOException, CsvException {
        Table<String,String,String> table= HashBasedTable.create();
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file),
            StandardCharsets.UTF_8);
        CSVReader csvReader = new CSVReaderBuilder(reader).build();
        List<String[]> list = csvReader.readAll();
        csvReader.close();
        int rowCount = list.size();
        if(rowCount==0){
            return table;
        }
        String[] title = list.get(0);
        for (int row=1;row<rowCount;++row) {
            String[] strings = list.get(row);
            int colCount = strings.length;
            for (short col=1;col<colCount;++col){
                String rowValue = strings[col];
                table.put(strings[0], title[col], rowValue);
            }
        }
        return table;
    }
}
