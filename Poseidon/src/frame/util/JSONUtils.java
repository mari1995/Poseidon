package frame.util;


import frame.annotation.NoJson;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;


/**
 * json解析工具
 * Created by sumei on 17/9/7.
 */
public class JSONUtils {
	/**
	 * 传入一个任意对象，返回对应的json字符串
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static String parseObject( Object obj )  {
		//获得对象的Class 对象
		Class c = obj.getClass();
		//获得当前类中所有属性
		Field [] fields = c.getDeclaredFields();
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");
		for (Field field : fields) {
			NoJson nojs = field.getAnnotation(NoJson.class);
			//静态属性不生成Json
			if ( Modifier.isStatic( field.getModifiers() ) && nojs != null) {
				continue;
			}
			field.setAccessible(true); //授权
			if ( sb.length() != 1 ) {
				sb.append(",");
			}

			//属性的key
			sb.append("\""+ field.getName() + "\":" );
			//属性的value
			Object value = null;

			try {
				value = field.get(obj);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			//获取格式化注释
			/*Formatter fmt = field.getAnnotation(Formatter.class);
			try {
				if ( fmt != null ) {
					//获得指定的转换器
					TypeHandler hander = fmt.value().newInstance();
					value = hander.to_String(field.get(obj));
				}else {
					value = field.get(obj);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			sb.append( parseValue(value) );
		}
		sb.append("}");
		return sb.toString();
	}
	
	/**
	 * 传入任意一个集合，返回对应的 json字符串
	 * @return
	 */
	public static String parseCollection (Collection<?> collection) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		
		for (Object object : collection) {
			if ( sb.length() != 1 ) {
				sb.append(",");
			}
			sb.append( parseValue(object) );
		}
		sb.append("]");
		return sb.toString();
	}
	/**
	 * 传入一个map ，返回对应的json字符串
	 * @param map
	 * @return
	 */
	public static String parseMap (Map<?,?> map) {
		StringBuilder sb = new StringBuilder();
		sb.append("{") ;
		for (Entry e : map.entrySet()) {
			if ( sb.length() != 1 ) {
				sb.append(",");
			}
			sb.append("\"" + e.getKey() + "\":");
			sb.append(parseValue(e.getValue())) ;
		}
		
		sb.append("}");
		return sb.toString();
	}
	/**
	 * 传入一个数组 返回相对应的 josn 字符串
	 * @return
	 */
	public static<T> String parseArray ( T array ) {
		StringBuilder sb = new StringBuilder();
		Class arrayClass = array.getClass(); //获得数组的Class对象
		Class type = arrayClass.getComponentType(); //获取 数组中每个元素的 Class 对象
		int len = Array.getLength( array );
		sb.append("[");
		
		if ( type == null || len == 0 ) {
			return null;
		}
		for ( int i = 0 ; i < len ; i++ ) {
			if ( i!= 0) {
				sb.append(",");
			}
			sb.append(parseValue( Array.get(array, i) ));
		}
		
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * 将任何类型的值转换成String
	 * @return
	 */
	public static String parseValue( Object value ) {
		if ( value != null ) {
			//数值类型
			if ( value instanceof Number){
				return String.valueOf( value );
			}else if ( value instanceof String || value instanceof Character || value instanceof Boolean) {
				return "\"" +  value + "\"" ;
			}else if ( value instanceof Collection ) {
				return parseCollection( (Collection)value );
			}else if ( value instanceof Map ) {
				return parseMap((Map) value );
			}else if ( value.getClass().isArray() ) {
				return parseArray(value);
			}else if( value instanceof Object ) {
				return parseObject(value);
			}
		}
		return null;
	}

}
