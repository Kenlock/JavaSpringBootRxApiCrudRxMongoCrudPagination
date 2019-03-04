package com.melardev.spring.rxmongcrud.dtos.responses;


import reactor.core.publisher.Mono;

import java.util.Collection;

public class PageMeta {
    boolean hasNext;
    boolean hasPrevPage;
    public int currentPage;
    long totalItemsCount; // total cartItems in total
    int pageSize; // max cartItems per page
    int currentItemsCount; // cartItems in this page
    int pageCount; // number of pages
    long offset;
    int nextPageNumber;
    int prevPageNumber;
    String nextPageUrl;
    String prevPageUrl;
    private Mono<Long> hook;

    public PageMeta() {
    }


    public static PageMeta build(Collection resources, String basePath, int page, int pageSize, Long totalItemsCount) {
        PageMeta pageMeta = new PageMeta();
        pageMeta.setOffset((page - 1) * pageSize);
        pageMeta.setPageSize(pageSize);
        pageMeta.setCurrentItemsCount(resources.size());
        pageMeta.setCurrentPage(page);


        pageMeta.setTotalItemsCount(totalItemsCount);
        pageMeta.setTotalPageCount((int) Math.ceil(pageMeta.getTotalItemsCount() / pageMeta.getPageSize()));
        pageMeta.setHasNextPage(pageMeta.getCurrentPageNumber() < pageMeta.getPageCount());
        pageMeta.setHasPrevPage(pageMeta.getCurrentPageNumber() > 1);
        if (pageMeta.hasNext) {
            pageMeta.setNextPageNumber(pageMeta.getCurrentPageNumber() + 1);
            pageMeta.setNextPageUrl(String.format("%s?page_size=%d&page=%d",
                    basePath, pageMeta.getPageSize(), pageMeta.getNextPageNumber()));
        } else {
            pageMeta.setNextPageNumber(pageMeta.getPageCount());
            pageMeta.setNextPageUrl(String.format("%s?page_size=%d&page=%d",
                    basePath, pageMeta.getPageSize(), pageMeta.getNextPageNumber()));
        }

        if (pageMeta.hasPrevPage) {
            pageMeta.setPrevPageNumber(pageMeta.getCurrentPageNumber() - 1);

            pageMeta.setPrevPageUrl(String.format("%s?page_size=%d&page=%d",
                    basePath, pageMeta.getPageSize(),
                    pageMeta.getPrevPageNumber()));
        } else {
            pageMeta.setPrevPageNumber(1);
            pageMeta.setPrevPageUrl(String.format("%s?page_size=%d&page=%d",
                    basePath, pageMeta.getPageSize(), pageMeta.getPrevPageNumber()));
        }

        return pageMeta;

    }

    private void setHook(Mono<Long> hook) {
        this.hook = hook;
    }

    public void setPrevPageUrl(String prevPageUrl) {
        this.prevPageUrl = prevPageUrl;
    }

    public int getPrevPageNumber() {
        return prevPageNumber;
    }

    private void setPrevPageNumber(int prevPageNumber) {
        this.prevPageNumber = prevPageNumber;
    }

    private void setHasNextPage(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getOffset() {
        return offset;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setCurrentItemsCount(int currentItemsCount) {
        this.currentItemsCount = currentItemsCount;
    }

    public int getCurrentItemsCount() {
        return currentItemsCount;
    }

    public void setTotalPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setNextPageNumber(int nextPageNumber) {
        this.nextPageNumber = nextPageNumber;
    }

    public int getNextPageNumber() {
        return nextPageNumber;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setHasPrevPage(boolean hasPrevPage) {
        this.hasPrevPage = hasPrevPage;
    }

    public boolean getHasPrevPage() {
        return hasPrevPage;
    }

    public void setTotalItemsCount(Long totalItemsCount) {
        this.totalItemsCount = totalItemsCount;
    }

    public Long getTotalItemsCount() {
        return totalItemsCount;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPageNumber() {
        return currentPage;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrevPage() {
        return hasPrevPage;
    }

    public String getPrevPageUrl() {
        return prevPageUrl;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public Mono<Long> getHook() {
        return hook;
    }
}
