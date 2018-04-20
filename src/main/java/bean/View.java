package bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回视图对象
 * Created by liq on 2018/3/30.
 */
public class View {

    /**
     * 视图路径
     */
    private String path;
    /**
     * 模型数据
     */
    private Map<String, Object> model = new HashMap<String, Object>();

    public View(String path, Map<String, Object> model) {
        this.path = path;
        this.model = model;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public View addModel(String key, Object value){
        this.model.put(key, value);
        return this;
    }
}
