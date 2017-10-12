package frame.typehandler;

/**
 * 整型类型转化器
 * String --->  Integer
 * Created by sumei on 17/9/7.
 */
public class IntegerTypeHandler implements RequestTypeHandler<Integer>{
    @Override
    public Integer parseType(String data ) {
        if ( null != data && !data.equals("")) {
            return Integer.valueOf(data);
        }
        return null;
    }
}
