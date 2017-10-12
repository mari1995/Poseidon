package frame.entity;

import frame.annotation.Entity;

import java.util.List;

/**
 * 分页对象
 * Created by sumei on 17/9/7.
 */
@Entity
public class Page<T> {
    // 当前页
    private int page = 1;

    // 每页数据的总数
    private int pageSize = 20;

    // 页总数
    private int maxPage;

    // 总数据
    private int pageCount;

    // 查询到的结果
    private List<T> data;

    // 提交到servlet
    private String url;

    // url的参数
    private String queryString;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {

        int index = queryString.lastIndexOf("page");
        if ( -1 != index ) {
            queryString = queryString.substring(0,index - 1);

        }
        this.queryString = queryString;
    }


    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
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

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @Override
    public String toString() {
        return "Page{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", maxPage=" + maxPage +
                ", pageCount=" + pageCount +
                '}';
    }
}
