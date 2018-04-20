package helper;

import bean.CodecUtil;
import bean.FormParam;
import bean.Param;
import util.ArrayUtil;
import util.StreamUtil;
import util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

/**
 * Created by liq on 2018/4/20.
 */
public final class RequestHelper {
    /**
     *创建请求对象
     */
    public static Param createParam(HttpServletRequest request) throws IOException {
        ArrayList<FormParam> formParamList = new ArrayList<FormParam>();
        formParamList.addAll(parseParameterNames(request));
        formParamList.addAll(parseInputStream(request));
        return new Param(formParamList);
    }

    private static Collection<? extends FormParam> parseParameterNames(HttpServletRequest request) {
        ArrayList<FormParam> formParamList = new ArrayList<FormParam>();
        Enumeration<String> paramNames = request.getParameterNames();
        while(paramNames.hasMoreElements()){
            String fieldName = paramNames.nextElement();
            String[] fieldValues = request.getParameterValues(fieldName);
            if (ArrayUtil.isNotEmpty(fieldValues)){
                Object fieldValue;
                if (fieldValues.length == 1){
                    fieldValue = fieldValues[0];
                }else{
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<fieldValues.length; i++){
                        sb.append(fieldValues[i]);
                        if (i != fieldValues.length - 1){
                            sb.append(StringUtil.SEPARATOR);
                        }
                    }
                    fieldValue = sb.toString();
                }
                formParamList.add(new FormParam(fieldName, fieldValue));
            }
        }
        return formParamList;
    }

    private static Collection<? extends FormParam> parseInputStream(HttpServletRequest request) throws IOException {

        ArrayList<FormParam> formParamList = new ArrayList<FormParam>();
        String body = CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if (StringUtil.isNotEmpty(body)) {
            String[] kvs = body.split("&");
            if (ArrayUtil.isNotEmpty(kvs)) {
                for (String kv : kvs) {
                    String[] s = StringUtil.splitString(kv, "=");
                    if (ArrayUtil.isNotEmpty(s) && s.length == 2) {
                        String fieldName = s[0];
                        String fieldValue = s[1];
                        formParamList.add(new FormParam(fieldName, fieldValue));
                    }
                }
            }
        }
        return formParamList;
    }


}
