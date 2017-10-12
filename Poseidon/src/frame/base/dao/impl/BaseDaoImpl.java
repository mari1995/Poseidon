package frame.base.dao.impl;

import frame.base.DBSQL;
import frame.base.dao.IBaseDao;
import frame.entity.Page;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Created by sumei on 17/9/7.
 */
public class BaseDaoImpl<T> implements IBaseDao<T> {

    // 分页查询
    public Page<T> queryByPage(Page<T> page, Class<T> clazz, String sql , Object... params ) throws SQLException {
        // 查询总共的记录数
        String sqlCount = " SELECT count(1) " + sql.substring( sql.indexOf("FROM") );

        int pageCount = 0;
        ResultSet rs = new DBSQL(new StringBuffer(sqlCount))
                .addParams(params)
                .queryResultSet();

        if ( rs.next() ) {
            pageCount = rs.getInt(1);
        }

        // 计算分页的总页数
        int maxpage = pageCount % page.getPageSize() == 0 ? pageCount / page.getPageSize()
                : pageCount / page.getPageSize() + 1;

        // 封装总页数和总条数
        page.setMaxPage(maxpage);
        page.setPageCount(pageCount);

        // 封装参数
        List<Object> queryParam = new ArrayList<>();
        queryParam.addAll(Arrays.asList(params));

        // 开始查询的记录
        queryParam.add( (page.getPage() - 1 ) * page.getPageSize() );
        // 查询的记录数
        queryParam.add(page.getPageSize());

        // 查询
        sql += " limit ?,? ";
        List<T> data = new DBSQL<T>(new StringBuffer(sql))
                .addParams(queryParam.toArray())
                .addEntity(clazz)
                .queryAll();

        page.setData(data);

        return page;

    }
}
