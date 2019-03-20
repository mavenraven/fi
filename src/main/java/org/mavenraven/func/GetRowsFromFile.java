package org.mavenraven.func;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mavenraven.Row;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetRowsFromFile implements Function<FileReader, List<Row>> {
    private final Function<CSVRecord, Row> rowDeserializer;

    public GetRowsFromFile(Function<CSVRecord, Row> rowDeserializer) {
        this.rowDeserializer = rowDeserializer;
    }

    @Override
    public List<Row> apply(FileReader fileReader) {
        try {
            var parsed = CSVFormat.DEFAULT.withDelimiter(';').withHeader().parse(fileReader);
            return parsed.getRecords().stream().map(rowDeserializer).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
