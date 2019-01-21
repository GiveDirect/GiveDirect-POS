package io.givedirect.givedirectpos.model.persistence;

import java.util.Arrays;
import java.util.List;

public class EmbeddedHals<T> {
    private T[] records;

    public void setRecords(T[] records) {
        this.records = records;
    }

    public T[] getRecords() {
        return records;
    }

    public List<T> getRecordsList() {
        return Arrays.asList(records);
    }
}
