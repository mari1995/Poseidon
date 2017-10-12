package frame.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5加密工具
 * Created by sumei on 17/9/11.
 */
public class MD5Util {

    public static String getMD5(String str){
        try {
            // 生成md5加密计算
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 计算md5函数
            md.update(str.getBytes());

            return new BigInteger(1,md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
