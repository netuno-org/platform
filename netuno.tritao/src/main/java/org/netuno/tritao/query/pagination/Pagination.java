package org.netuno.tritao.query.pagination;

public class Pagination {
    private int page = 1;
    private int pageSize = 10;
    private int offset = 0;

    public Pagination(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
        this.offset = (page - 1) * pageSize;
    }

    public int getPage() {
        return page;
    }

    public Pagination setPage(int page) {
        this.page = page;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Pagination setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public Pagination setOffset(int offset) {
        this.offset = offset;
        return this;
    }
}
