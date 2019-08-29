package org.tieland.melon.ribbon;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;

/**
 * @author zhouxiang
 * @date 2019/8/27 9:16
 */
public final class MelonContextMeshHolder {

    private static HystrixRequestVariableDefault<MelonContextMesh> MELON_CONTEXT_HOLDER = new HystrixRequestVariableDefault<MelonContextMesh>();

    public static void set(MelonContextMesh mesh){
        MELON_CONTEXT_HOLDER.set(mesh);
    }

    public static MelonContextMesh get(){
        return MELON_CONTEXT_HOLDER.get();
    }

}
