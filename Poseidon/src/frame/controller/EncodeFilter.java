package frame.controller;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 编码过滤器
 * @author sumei
 *
 *
 */
public class EncodeFilter implements Filter{


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // POST提交有效
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        // 代理模式
        HttpServletRequest proxy =
                (HttpServletRequest) Proxy.newProxyInstance(req.getClass().getClassLoader()
                        , new Class[]{HttpServletRequest.class} , new InvocationHandler() {

                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                Object returnValue = null;
                                String methodName = method.getName();

                                if ("getParameter".equals(methodName)) {

                                    String value = req.getParameter(args[0].toString());

                                    String methodSubmit = req.getMethod();

                                    if ("GET".equalsIgnoreCase(methodSubmit)) {
                                        if (value != null && !"".equals(value.trim())){
                                            // 处理GET中文
                                            value = new    String(value.getBytes("ISO8859-1"),"UTF-8");
                                        }
                                    }
                                    return value;
                                }
                                else {
                                    // 执行request对象的其他方法
                                    returnValue = method.invoke(req, args);
                                }

                                return returnValue;
                            }
                        });

        chain.doFilter(proxy, response);
    }

    @Override
    public void destroy() {

    }

}
