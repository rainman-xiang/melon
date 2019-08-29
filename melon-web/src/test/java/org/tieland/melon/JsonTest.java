package org.tieland.melon;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

/**
 * @author zhouxiang
 * @date 2019/8/23 11:37
 */
public class JsonTest {

    @Test
    public void test(){

        String test = "{\n" +
                "  \"a\":[\"1\",\"2\", \"3\"]\n" +
                "}";
        JSONArray array = JSONObject.parseObject(test).getJSONArray("a");
        array.forEach(i-> System.out.println(i));
    }

}
