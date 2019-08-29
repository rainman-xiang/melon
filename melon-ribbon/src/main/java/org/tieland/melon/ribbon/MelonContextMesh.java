package org.tieland.melon.ribbon;

import org.tieland.melon.common.MelonContext;
import org.tieland.melon.common.MelonInstance;

/**
 * @author zhouxiang
 * @date 2019/8/27 9:19
 */
public final class MelonContextMesh {

    private MelonContext context;

    private MelonInstance instance;

    public MelonContextMesh(MelonContext context, MelonInstance instance){
        this.context = context;
        this.instance = instance;
    }

    public MelonContext getContext() {
        return context;
    }

    public MelonInstance getInstance() {
        return instance;
    }
}
