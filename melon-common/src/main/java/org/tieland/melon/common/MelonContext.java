package org.tieland.melon.common;

import lombok.ToString;

/**
 * @author zhouxiang
 * @date 2019/8/27 9:07
 */
@ToString
public final class MelonContext {

    /**
     * 被选中group
     */
    private String group;

    /**
     * 请求来源
     */
    private MelonOrigin origin;

    /**
     * 是否灰度
     */
    private boolean grayOn;

    /**
     * 备选group
     */
    private String[] reserveGroups;

    /**
     * 黑名单group
     */
    private String[] blackGroups;

    private MelonContext(final String group, final MelonOrigin origin,
                         final Boolean grayOn, final String[] reserveGroups,
                         final String[] blackGroups){
        this.group = group;
        this.origin = origin;
        this.grayOn = grayOn;
        this.reserveGroups = reserveGroups;
        this.blackGroups = blackGroups;
    }

    public String getGroup() {
        return group;
    }

    public MelonOrigin getOrigin() {
        return origin;
    }

    public boolean isGrayOn() {
        return grayOn;
    }

    public String[] getReserveGroups() {
        return reserveGroups;
    }

    public String[] getBlackGroups() {
        return blackGroups;
    }

    /**
     * Builder
     */
    public static class Builder {

        private String group;

        private MelonOrigin origin;

        private boolean grayOn;

        private String[] reserveGroups;

        private String[] blackGroups;

        public Builder group(String group){
            this.group = group;
            return this;
        }

        public Builder origin(MelonOrigin origin){
            this.origin = origin;
            return this;
        }

        public Builder grayOn(boolean grayOn){
            this.grayOn = grayOn;
            return this;
        }

        public Builder reserveGroups(String[] reserveGroups){
            this.reserveGroups = reserveGroups;
            return this;
        }

        public Builder blackGroups(String[] blackGroups){
            this.blackGroups = blackGroups;
            return this;
        }

        public MelonContext build(){
            if(origin == null){
                throw new MelonException(" origin must not be null. ");
            }
            return new MelonContext(this.group, this.origin, this.grayOn, this.reserveGroups, this.blackGroups);
        }
    }

}
