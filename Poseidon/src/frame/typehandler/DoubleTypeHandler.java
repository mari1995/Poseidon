package frame.typehandler;

/**
 * 整型类型转化器
 * String --->  Integer
 * Created by sumei on 17/9/7.
 */
public class DoubleTypeHandler implements RequestTypeHandler<Double>{
    @Override
    public Double parseType(String data ) {
        if ( null != data ) {
            return Double.valueOf(data);
        }
        return null;
    }
}
