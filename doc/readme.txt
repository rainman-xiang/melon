前提假设：
1.把服务实例像网格一样进行切分
2.每个服务有多个实例组成，这些按组划分

gateway->ms-1(A)->ms-2(A)-ms-3(A)....
gateway->ms-1(A)->ms-2(A)-ms-3(A)....
gateway->ms-1(B)->ms-2(B)-ms-3(B)....

灰度相当于在同个服务集群中，不同组之间的实例是不一样的
需要在gateway中根据灰度规则匹配选择相应组，自定义扩展ribbon规则

MelonSettings:
    mode: 灰度开关
    primaryGroups: 常规组（不匹配灰度）
    forbiddenGroups: 禁用组（不给流量）
    graySettingsList: 灰度规则
如果常规组规模很大，不建议设置该值
