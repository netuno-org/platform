package org.netuno.tritao.db.form.pagination;

import org.netuno.psamata.Values;

import java.util.ArrayList;
import java.util.List;

/**
 * Page - Base object to manage pagination results
 * @author Jailton de Araujo Santos - @jailtonaraujo
 */
public class Page {
    private List<Values> items = new ArrayList<>();
    private int totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    public Page(List<Values> items, int totalElements, Pagination pagination) {
        this.items = items;
        this.totalElements = totalElements;
        this.pageNumber = pagination.getPage();
        this.pageSize = pagination.getPageSize();
        this.totalPages =
                (totalElements < 1 || pagination.getPageSize() < 1)
                        ? 0
                        : (int) Math.ceil((double) this.totalElements / (double) this.pageSize);
        this.hasNext = (pageNumber - 1) + 1 < totalPages;
        this.hasPrevious = (pageNumber <= totalPages) && (pageNumber > 1);
    }

    public Values toValues() {
        Values values = new Values();
        values.set("items", this.items);
        values.set("totalElements", this.totalElements);
        values.set("pageNumber", this.pageNumber);
        values.set("pageSize", this.pageSize);
        values.set("totalPages", this.totalPages);
        values.set("hasNext", this.hasNext);
        values.set("hasPrevious", this.hasPrevious);
        return values;
    }

    public List<Values> getItems() {
        return items;
    }

    public Page setItems(List<Values> items) {
        this.items = items;
        return this;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public Page setTotalElements(int totalElements) {
        this.totalElements = totalElements;
        return this;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public Page setTotalPages(int totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public Page setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Page setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public Page setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
        return this;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public Page setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
        return this;
    }
}
