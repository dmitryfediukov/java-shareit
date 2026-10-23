package ru.practicum.shareit.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record OffsetPageRequest(long offset, int pageSize) implements Pageable {
    public OffsetPageRequest {
        if (offset < 0 || pageSize < 1) {
            throw new IllegalArgumentException("Invalid pagination");
        }
    }

    @Override
    public int getPageNumber() {
        return (int) (offset / pageSize);
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return Sort.unsorted();
    }

    @Override
    public Pageable next() {
        return new OffsetPageRequest(offset + pageSize, pageSize);
    }

    @Override
    public Pageable previousOrFirst() {
        return new OffsetPageRequest(Math.max(0, offset - pageSize), pageSize);
    }

    @Override
    public Pageable first() {
        return new OffsetPageRequest(0, pageSize);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Invalid page");
        }
        return new OffsetPageRequest((long) pageNumber * pageSize, pageSize);
    }

    @Override
    public boolean hasPrevious() {
        return offset > 0;
    }
}
