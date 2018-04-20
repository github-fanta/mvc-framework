import org.junit.Assert;
import org.junit.Test;
import util.StringUtil;

/**
 * Created by liq on 2018/3/30.
 */
public class stringUtilTest {

    @Test
    public void stringUtilTest(){
        String testStr = "name=liq";
        String[] strs = StringUtil.splitString("", "");
        Assert.assertEquals("", strs[0]);
        //Assert.assertEquals("", strs[1]);
    }


}
