package helper;

/**
 * 配置项常量
 * Created by liq on 2018/3/24.
 */
public interface ConfigConstant {

    String CONFIG_FILE = "mvc.properties";

    String JDBC_DRIVER = "mvc.framework.jdbc.driver";
    String JDBC_URL = "mvc.framework.jdbc.url";
    String JDBC_USERNAME = "mvc.framework.jdbc.username";
    String JDBC_PASSWORD = "mvc.framework.jdbc.password";

    String APP_BASE_PACKAGE = "mvc.framework.app.base_package";
    String APP_JSP_PATH = "mvc.framework.app.jsp_path";
    String APP_ASSET_PATH = "mvc.framework.app.asset_path";

    String APP_UPLOAD_LIMIT = "mvc.framework.app.upload_limit";
}
