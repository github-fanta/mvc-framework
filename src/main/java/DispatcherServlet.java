import bean.*;
import helper.*;
import util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求转发器
 * Created by liq on 2018/3/30.
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        //初始化相关Helper类
        HelperLoader.init();
        //获取ServletContext对象（用于注册Servlet）
        ServletContext servletContext = servletConfig.getServletContext();
        //注册用于处理JSP的Servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath()+"*");
        //注册用于处理静态资源的默认Servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath()+"*");

        UploadHelper.init(servletContext);
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestMethod = request.getMethod().toLowerCase();
        String requestPath = request.getPathInfo();
        if (requestPath.equals("/favicon.ico")){
            return;
        }

        //封装参数
        Param param;
        if (UploadHelper.isMultipart(request)){
            //有上传文件时
            param = UploadHelper.createParam(request);
        }else{
            //只是表单参数时
            param = RequestHelper.createParam(request);
        }
        //先获取handler，再从中获取controllerClass(再从BeanHelper中获取controllerBean实例)和actionMethod
        Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
        if (handler != null){
            //拿到参数actionMethod
            Method actionMethod = handler.getActionMethod();
            //拿到参数controllerBean
            Class<?> controllerClass = handler.getControllerClass();
            Object controllerBean = BeanHelper.getBean(controllerClass);
            //得到处理器返回结果
            Object result;
            if (param.isEmpty()){
                result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
            }else{
                result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
            }
            if (result instanceof View){
                //返回的是JSP页面
                handleVieResult((View)result, request, response);
            }else if (result instanceof Data){
                //返回的是JSON数据
                handleDataResult((Data)result, response);
            }
        }

    }

    private void handleVieResult(View view, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String path = view.getPath();
        if (StringUtil.isNotEmpty(path)){
            if (path.startsWith("/")){
                //重定向
                response.sendRedirect(request.getContextPath()+path);
            }else{
                //请求转发
                //将view中的model数据放入request域中
                Map<String, Object> model = view.getModel();
                for(Map.Entry<String, Object> entry : model.entrySet()){
                    request.setAttribute(entry.getKey(),entry.getValue());
                }
                request.getRequestDispatcher(ConfigHelper.getAppAssetPath()+path).forward(request, response);
            }
        }
    }

    private void handleDataResult(Data data, HttpServletResponse response) throws IOException {
        Object model = data.getModel();
        if (model != null){
            //设置响应头
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            //向浏览器写出json
            PrintWriter writer = response.getWriter();
            String json = JsonUtil.toJson(model);
            writer.write(json);
            writer.flush();
            writer.close();
        }
    }

}
