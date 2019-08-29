package org.tieland.melon;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.junit.Test;
import org.tieland.melon.core.GraySettings;
import org.tieland.melon.core.MelonMode;
import org.tieland.melon.core.MelonSettings;
import org.tieland.melon.core.PathGrayCondition;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouxiang
 * @date 2019/8/28 11:51
 */
public class GrayTest {

    @Test
    public void test(){

        MelonSettings melonSettings = new MelonSettings();
        melonSettings.setMode(MelonMode.GRAY);
        melonSettings.setForbiddenGroups(null);
        melonSettings.setPrimaryGroups(null);

        GraySettings settings1 = new GraySettings();
        settings1.setGrayNo("1");
        settings1.setRule("zuul-path-gray-rule");
        settings1.setGroups(new String[]{"1", "2"});
        settings1.setConditionClass(PathGrayCondition.class);
        PathGrayCondition condition = new PathGrayCondition();
        condition.setUri("/a/**");
        condition.setMethod("GET");
        Map<String, String> values = new HashMap<>();
        values.put("a", "1");
        condition.setValues(values);
        settings1.setJson(JSONObject.toJSONString(condition));
        melonSettings.setGraySettingsList(Lists.newArrayList(settings1));

        String json = JSONObject.toJSONString(melonSettings);
        MelonSettings melonSettings2 = JSONObject.parseObject(json, MelonSettings.class);
        System.out.println("====");
    }

    @Test
    public void test2(){
        Atest atest = new Atest();
        atest.startAsync().awaitRunning();

        try {
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class Atest extends AbstractScheduledService {

        @Override
        protected void runOneIteration() throws Exception {
            System.out.println(" ====== ");
        }

        @Override
        protected Scheduler scheduler() {
            return Scheduler.newFixedRateSchedule(1, 10, TimeUnit.SECONDS);
        }
    }

}
