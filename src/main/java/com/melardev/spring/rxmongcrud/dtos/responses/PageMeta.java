package com.melardev.spring.rxmongcrud.dtos.responses;


import java.util.Collection;

public class PageMeta {
    private boolean hasNextPage;
    private boolean hasPrevPage;
    private int currentPage;
    private long totalItemsCount; // total cartItems in total
    private int requestedPageSize; // max cartItems per page
    private int currentItemsCount; // cartItems in this page
    private int numberOfPages; // number of pages
    private long offset;
    private int nextPageNumber;
    private int prevPageNumber;
    private String nextPageUrl;
    private String prevPageUrl;


    public PageMeta() {
    }


    public static PageMeta build(Collection resources, String basePath, int page, int pageSize, Long totalItemsCount) {
        PageMeta pageMeta = new PageMeta();
        pageMeta.setOffset((page - 1) * pageSize);
        pageMeta.setRequestedPageSize(pageSize);
        pageMeta.setCurrentItemsCount(resources.size());
        pageMeta.setCurrentPage(page);


        pageMeta.setTotalItemsCount(totalItemsCount);
        pageMeta.setTotalPageCount((int) Math.ceil(pageMeta.getTotalItemsCount() / pageMeta.getRequestedPageSize()));
        pageMeta.setHasNextPage(pageMeta.getCurrentPageNumber() < pageMeta.getNumberOfPages());
        pageMeta.setHasPrevPage(pageMeta.getCurrentPageNumber() > 1);
        if (pageMeta.hasNextPage) {
            pageMeta.setNextPageNumber(pageMeta.getCurrentPageNumber() + 1);
            pageMeta.setNextPageUrl(String.format("%s?page_size=%d&page=%d",
                    basePath, pageMeta.getRequestedPageSize(), pageMeta.getNextPageNumber()));
        } else {
            pageMeta.setNextPageNumber(pageMeta.getNumberOfPages());
            pageMeta.setNextPageUrl(String.format("%s?page_size=%d&page=%d",
                    basePath, pageMeta.getRequestedPageSize(), pageMeta.getNextPageNumber()));
        }

        if (pageMeta.hasPrevPage) {
            pageMeta.setPrevPageNumber(pageMeta.getCurrentPageNumber() - 1);

            pageMeta.setPrevPageUrl(String.format("%s?page_size=%d&page=%d",
                    basePath, pageMeta.getRequestedPageSize(),
                    pageMeta.getPrevPageNumber()));
        } else {
            pageMeta.setPrevPageNumber(1);
            pageMeta.setPrevPageUrl(String.format("%s?page_size=%d&page=%d",
                    basePath, pageMeta.getRequestedPageSize(), pageMeta.getPrevPageNumber()));
        }

        return pageMeta;

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
        this.hasNextPage = hasNext;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getOffset() {
        return offset;
    }

    public void setRequestedPageSize(int requestedPageSize) {
        this.requestedPageSize = requestedPageSize;
    }

    public int getRequestedPageSize() {
        return requestedPageSize;
    }

    public void setCurrentItemsCount(int currentItemsCount) {
        this.currentItemsCount = currentItemsCount;
    }

    public int getCurrentItemsCount() {
        return currentItemsCount;
    }

    public void setTotalPageCount(int pageCount) {
        this.numberOfPages = pageCount;
    }

    public int getNumberOfPages() {
        return numberOfPages;
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

    public boolean isHasNextPage() {
        return hasNextPage;
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

}
