package bean;

import java.io.InputStream;

/**
 * 封装上传文件参数
 * Created by liq on 2018/4/20.
 */
public class FileParam {

    private String fieldName;
    private String fileName;
    private long fileSize;
    private String contentType;
    private InputStream intputStream;

    public FileParam(String fieldName, String fileName, long fileSize, String contentType, InputStream intputStream) {
        this.fieldName = fieldName;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.intputStream = intputStream;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public InputStream getIntputStream() {
        return intputStream;
    }
}
