package ru.findprofy.imagesearch;


public class Query {
    private int page;
    private String query;

    public int getPage() {
        return page;
    }

    public Query nextPage(){
        ++page;
        return this;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
