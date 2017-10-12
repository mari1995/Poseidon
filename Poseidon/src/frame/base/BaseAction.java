package frame.base;

import frame.annotation.AField;
import frame.annotation.Entity;
import frame.entity.Page;
import frame.typehandler.RequestTypeHandler;
import frame.util.PropertiesUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * servlet的父类，
 * 负责调用各个servlet中的方法，
 * 并将表单元素封装到各个实体类中
 * @author sumei
 *
 */
public class BaseAction extends HttpServlet{

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
        if ( null != method ) {
			// 获取当前类中所有的方法
			Method[] methods = this.getClass().getDeclaredMethods();
			
			for (Method m : methods) {
			
				int modifier = m.getModifiers();

                // 判断函数的修饰类型
				if ( !Modifier.isStatic(modifier) && ( Modifier.isPublic(modifier) || Modifier.isProtected(modifier)) ) {
					
					if ( m.getName().equals(method)){
						try {

                            m.setAccessible(true);

                            // 遍历所有参数列表
                            List<Object> params = parseParams(request,response,m);

						    // 调用指定方法
                            Object result = m.invoke(this, params.toArray());

                            System.out.println("result:" + result);

                            if ( !"-1".equals(result) && result != null && result.getClass() == String.class ) {
                                String str = (String) result;
                                if ( str.startsWith("redirect:")) {
                                    response.sendRedirect(request.getContextPath()+str.split(":")[1]);
                                    return;
                                } else {
                                    request.getRequestDispatcher(str).forward(request,response);
                                    return;
                                }
                            }
//                            m.invoke(this,request,response);
						} catch (Exception e) {
							e.printStackTrace();
						}
					    return;
					}
				}


			}
		}

		throw new ServletException(" not found the " + method + " function ");
	}

    /**
     * 对request请求的数据进行封装，
     * 并封装到实体类中，
     * 默认实体类中的成员变量名与request中的参数名一致
     *
     * @param request
     * @param clazz   确认返回值类型
     * @param <T>
     * @return
     * @throws ServletException
     * @throws IOException
     */
    protected <T> T parseRequest(HttpServletRequest request, Class<T> clazz) throws ServletException, IOException {
        T t = null;

        try {
            t = clazz.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }



        // 获得实体类中的所有属性
        Field[] fields = clazz.getDeclaredFields();

        for ( Field f : fields ) {
            try {
                int modifier = f.getModifiers();

                // 属性修饰符
                if ( !Modifier.isStatic(modifier) && Modifier.isPrivate(modifier) ) {
                    // 授权
                    f.setAccessible(true);

                    String fieldName = null;
                    AField anno = f.getAnnotation(AField.class);
                    if ( anno != null ) {
                        fieldName = anno.name();
                    } else {
                        fieldName = f.getName();
                    }

                    System.out.println("field name is : " + fieldName);
                    String value = request.getParameter(fieldName);


                    if (null != value) {
                        try {
                            Object val = value;

                            // 获取配置文件中的转化器class路径
                            String handlerpath = PropertiesUtil.getValue(f.getType().getName());
                            System.out.println("handlerpath : " + handlerpath);

                            // 对数据进行格式化
                            if ( null != handlerpath ){
                                RequestTypeHandler<T> handler =
                                        (RequestTypeHandler<T>) Class.forName(handlerpath).newInstance();

                                val = handler.parseType(value);
                                System.out.println("val: " + val);
                            }

                            f.set(t, val);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                   }

                }
            } catch (SecurityException e) {
                System.err.println( f.getName() + "设置异常！");
            }

        }
        return t;
    }

    /**
     * 解析方法的参数
     * @param request
     * @param response
     * @param method
     * @return
     */
    private List<Object> parseParams(HttpServletRequest request,HttpServletResponse response,Method method) throws ServletException, IOException {
        // 获得方法的参数
        Class<?>[] paramClazzs = method.getParameterTypes();

        List<Object> params = new ArrayList<>();

        // 遍历形参列表
        for ( Class cls : paramClazzs ) {
            System.out.println(cls.getName());

            if ( cls == HttpServletRequest.class ) {
                params.add(request);

            } else if ( cls == HttpServletResponse.class ) {
                params.add(response);

            } else if ( cls == HttpSession.class ) {
                params.add(request.getSession());

            }  else if ( cls == PrintWriter.class ) {
                params.add(response.getWriter());

            } else if ( null != cls.getAnnotation(Entity.class) ){
                params.add(parseRequest(request,cls));

            } else if ( cls == Page.class ) {
                Page pageEntity = new Page<>();
                String page = request.getParameter("page");
                if ( page != null ) {
                    pageEntity.setPage(Integer.parseInt(page));

                }

                String requestURI = request.getRequestURI();
                pageEntity.setUrl(requestURI);

                String queryString = request.getQueryString();
                pageEntity.setQueryString(queryString);

                params.add(pageEntity);
            } else {
                params.add(null);
            }
        }

        return params;
    }
}
