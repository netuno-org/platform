package org.netuno.tritao.db.form.pagination;

/**
 * Pagination - Object to config preferences of the page
 * @author Jailton de Araujo Santos - @jailtonaraujo
 */
public class Pagination {
    private int page = 1;
    private int pageSize = 10;
    private int offset = 0;
    private String distinct = null;
    private boolean useGroup = false;

    public Pagination(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public int page() {
        return page;
    }

    public Pagination setPage(int page) {
        this.page = page;
        return this;
    }

    public Pagination page(int page) {
        this.page = page;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int size() {
        return pageSize;
    }

    public Pagination setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Pagination size(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getOffset() {
        this.offset = (page - 1) * pageSize;
        return offset;
    }

    public int offset() {
        this.offset = (page - 1) * pageSize;
        return offset;
    }

    public Pagination setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public Pagination offset(int offset) {
        this.offset = offset;
        return this;
    }

    public String getDistinct() {
        return distinct;
    }

    public String distinct() {
        return getDistinct();
    }

    public Pagination setDistinct(String distinct) {
        this.distinct = distinct;
        return this;
    }

    public Pagination distinct(String distinct) {
        return setDistinct(distinct);
    }

    public boolean isUseGroup() {
        return useGroup;
    }

    public boolean useGroup() {
        return isUseGroup();
    }

    public Pagination setUseGroup(boolean useGroup) {
        this.useGroup = useGroup;
        return this;
    }

    public Pagination useGroup(boolean useGroup) {
        return setUseGroup(useGroup);
    }
}
