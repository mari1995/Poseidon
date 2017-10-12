package frame.factory;

import java.util.ResourceBundle;

/**
 * 创建service和dao的实例对象
 *
 * Created by sumei on 17/9/11.
 */
public class BeanFactory {
	
	private static ResourceBundle bundle ;
	static{
		bundle = ResourceBundle.getBundle("instance");
	}
	
	public static <T> T getInstance(String key,Class<T> clazz){
		try {
			String className = bundle.getString(key);
			return (T) Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
}
