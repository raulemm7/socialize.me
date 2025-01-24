package org.example.labx.utils;

public class Pageable {
    private final Long pageSize;
    private final Long pageNumber;

    public Pageable(Long pageSize, Long pageNumber) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public Long getPageNumber() {
        return pageNumber;
    }
}
