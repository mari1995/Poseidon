package frame.typehandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期格式转化器
 * String --->  Date
 * Created by sumei on 17/9/7.
 */
public class DateTypeHandler implements RequestTypeHandler<Date>{

    @Override
    public Date parseType(String data){
        if ( null != data ) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                return sdf.parse(data);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
