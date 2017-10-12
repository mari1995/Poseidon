package frame.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;

/**
 * 配置文件读取
 * 默认在（src 路径下）
 *
 * Created by sumei on 17/9/11.
 */

public class PropertiesUtil {


    private static Properties properties = new Properties();

    private static void getProperties() {
        // 项目的运行路径
        String classPath = PropertiesUtil.class.getResource("/").toString().replace("file:/", "");

        File classPathFile = new File(classPath);

        File[] listFiles = classPathFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".properties");
            }
        });

        try {
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    Properties prop = new Properties();
                    prop.load(new FileInputStream(file));

                    properties.putAll(prop);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取配置文件的的值
     *
     * @param key
     * @return
     */
    public static String getValue(String key) {
        getProperties();
        return properties.getProperty(key, null);
    }

}
