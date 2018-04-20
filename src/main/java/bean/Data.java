package bean;

/**
 * 返回数据对象
 * Created by liq on 2018/3/30.
 */
public class Data {

    /**
     * 模型数据，框架会将其写入HttpServletResponse对象中，直接输出至浏览器。
     */
    private Object model;

    public Data(Object model) {
        this.model = model;
    }

    public Object getModel() {
        return model;
    }
}
