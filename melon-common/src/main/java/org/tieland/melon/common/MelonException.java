package org.tieland.melon.common;

/**
 * @author zhouxiang
 * @date 2019/8/27 11:32
 */
public class MelonException extends RuntimeException {

    public MelonException(String message){
        super(message);
    }

    public MelonException(String message, Throwable cause){
        super(message, cause);
    }

    public MelonException(Throwable cause){
        super(cause);
    }

}
