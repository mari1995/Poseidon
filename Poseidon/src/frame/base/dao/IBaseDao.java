package frame.base.dao;

import frame.entity.Page;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by sumei on 17/10/12.
 */
public interface IBaseDao<T> {

    // 分页查询
    Page<T> queryByPage(Page<T> page, Class<T> clazz, String sql , Object... params ) throws SQLException;
}
