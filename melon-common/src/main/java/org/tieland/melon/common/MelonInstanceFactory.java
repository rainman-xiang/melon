package org.tieland.melon.common;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Melon实例工厂
 * @author zhouxiang
 * @date 2019/8/27 11:58
 */
public final class MelonInstanceFactory {

    private static MelonInstance instance;

    private MelonInstanceFactory(){
        //
    }

    /**
     * melon实例
     * @param config
     * @return
     */
    public static MelonInstance get(MelonConfig config){
        if(instance != null){
            return instance;
        }

        Lock lock = new ReentrantLock();

        try{
            lock.lock();
            instance = new MelonInstance(config.getGroup(), config.isGateway());
            return instance;
        }finally {
            lock.unlock();
        }
    }

}
