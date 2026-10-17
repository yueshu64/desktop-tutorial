package com.suse.lostfound.util;

import java.util.ArrayList;
import java.util.List;

public class PageResult<T> {
    private List<T> records = new ArrayList<T>();
    private int page = 1;
    private int pageSize = 6;
    private int total;

    public int getTotalPages() {
        if (pageSize <= 0 || total <= 0) {
            return 0;
        }
        return (total + pageSize - 1) / pageSize;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
