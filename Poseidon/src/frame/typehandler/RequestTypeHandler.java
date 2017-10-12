package frame.typehandler;

/**
 * request请求转化器接口
 * Created by sumei on 17/9/7.
 */
public interface RequestTypeHandler<T> {
    T parseType(String data);
}
