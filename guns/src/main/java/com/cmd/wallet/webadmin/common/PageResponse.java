package com.cmd.wallet.webadmin.common;

import com.github.pagehelper.Page;

import java.util.ArrayList;
import java.util.List;

public class PageResponse<T> {
    private List<T> rows;
    private long   total;
    private int    pageNum;
    private int    pageSize;

    public PageResponse(Page<T> page) {
        this.rows = new ArrayList<T>(page.size());
        this.rows.addAll(page);
        this.total = page.getTotal();
        this.pageNum = page.getPageNum();
        this.pageSize = page.getPageSize();
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
