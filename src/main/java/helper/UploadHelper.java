package helper;

import bean.FileParam;
import bean.FormParam;
import bean.Param;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CollectionUtil;
import util.FileUtil;
import util.StreamUtil;
import util.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liq on 2018/4/20.
 */
public final class UploadHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadHelper.class);

    /**
     * Apache Commons FileUpload 提供的 Servlet文件上传对象
     */
    private static ServletFileUpload servletFileUpload;

    /**
     * 初始化
     */
    public static void init(ServletContext servletContext){
        //ServletContext的临时目录，有了它，servlet重启时，容器不再需要维持临时目录的内容。属性关联的对象必须是File类型
        File repository = (File) servletContext.getAttribute(ServletContext.TEMPDIR);
        servletFileUpload = new ServletFileUpload(new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, repository));
        int uploadLimit = ConfigHelper.getAppUploadLimit();
        servletFileUpload.setFileSizeMax(uploadLimit * 1024 * 1024);
    }

    /**
     * 判断请求是否为multipart类型
     */
    public static boolean isMultipart(HttpServletRequest request){
        return ServletFileUpload.isMultipartContent(request);
    }
    /**
     * 创建请求对象
     */
    public static Param createParam(HttpServletRequest request) throws IOException {

        ArrayList<FormParam> formParamList = new ArrayList<FormParam>();
        ArrayList<FileParam> fileParamList = new ArrayList<FileParam>();
        Map<String, List<FileItem>> fileItemListMap = null;
        try {
            fileItemListMap = servletFileUpload.parseParameterMap(request);
            if (CollectionUtil.isNotEmpty(fileItemListMap)){
                for(Map.Entry<String, List<FileItem>> fileItemListEntry : fileItemListMap.entrySet()){
                    String fieldName = fileItemListEntry.getKey();
                    List<FileItem> fileItemList = fileItemListEntry.getValue();
                    if (CollectionUtil.isNotEmpty(fileItemList)){
                        for(FileItem fileItem : fileItemList){
                            if (fileItem.isFormField()){
                                //上传表单参数
                                String fieldValue = fileItem.getString("UTF-8");
                                formParamList.add(new FormParam(fieldName, fieldValue));
                            }else{
                                //上传文件参数
                                String fileName = FileUtil.getRealFileName(new String(fileItem.getName().getBytes(), "UTF-8"));
                                if (StringUtil.isNotEmpty(fileName)){
                                    long fileSize = fileItem.getSize();
                                    String contentType = fileItem.getContentType();
                                    InputStream inputStream = fileItem.getInputStream();
                                    fileParamList.add(new FileParam(fieldName, fileName, fileSize, contentType, inputStream));
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileUploadException e) {
            LOGGER.error("create param failure", e);
            throw new RuntimeException(e);
        }
        return new Param(formParamList, fileParamList);
    }


    /**
     * 上传文件
     */
    public static void uploadFile(String basePath, FileParam fileParam){

        try {
            String filePath = basePath + fileParam.getFileName();
            BufferedInputStream inputStream = new BufferedInputStream(fileParam.getIntputStream());
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
            StreamUtil.copyStream(inputStream, outputStream);
        } catch (Exception e) {
            LOGGER.error("upload file failure", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量上传文件
     */
    public static void uploadFile(String basePath, List<FileParam> fileParamList){
        try{
            if (CollectionUtil.isNotEmpty(fileParamList)){
                for(FileParam fileParam : fileParamList){
                    uploadFile(basePath, fileParam);
                }
            }
        }catch(Exception e){
            LOGGER.error("upload file failure", e);
            throw new RuntimeException(e);
        }
    }
}
