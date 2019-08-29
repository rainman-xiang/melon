package org.tieland.melon.ribbon;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.tieland.melon.common.MelonContext;
import org.tieland.melon.common.MelonInstance;
import org.tieland.melon.common.MelonOrigin;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author zhouxiang
 * @date 2019/8/27 9:27
 */
@Slf4j
public class MelonZoneAvoidanceRule extends ZoneAvoidanceRule {

    public MelonZoneAvoidanceRule(){
        super();
    }

    @Override
    public Server choose(Object key) {
        List<Server> serverList = this.getPredicate().getEligibleServers(this.getLoadBalancer().getAllServers());
        if(CollectionUtils.isEmpty(serverList)){
            log.warn(" no server exist. ");
            return null;
        }

        //分组server
        Multimap<String, Server> groupServers = groupServers(serverList);
        MelonContextMesh contextMesh = MelonContextMeshHolder.get();
        MelonContext context = contextMesh.getContext();
        MelonInstance instance = contextMesh.getInstance();
        //当请求来源不是gateway时，只可选择本组
        if(MelonOrigin.GATEWAY != context.getOrigin()){
            log.debug(" request is not from gateway. ");
            log.debug(" only select local group:{} servers. ", instance.getGroup());
            List<Server> localGroupServers = (List<Server>)groupServers.get(instance.getGroup());
            return selectServer(localGroupServers, key);
        }

        //未开启gray
        if(!context.isGrayOn()){
            log.debug(" gray is off. select any server. ");
            return selectServer(serverList, key);
        }

        //gray开启时，优先选择指定group
        if(StringUtils.isNotBlank(context.getGroup())){
            log.debug(" optimal group:{} ", context.getGroup());
            List<Server> optimalServers = (List<Server>)groupServers.get(context.getGroup());
            if(CollectionUtils.isNotEmpty(optimalServers)){
                log.debug(" optimal group:{} server is selected. ", context.getGroup());
                return selectServer(optimalServers, key);
            }

            log.debug(" optimal group:{} servers is empty. ", context.getGroup());
            if(ArrayUtils.isNotEmpty(context.getReserveGroups())){
                log.debug(" reserve group servers will be selected. ");
                List<Server> reserveServers = new ArrayList<Server>();
                for(String reserveGroup:context.getReserveGroups()){
                    log.debug(" reserve group:{} is candidate. ", reserveGroup);
                    List<Server> servers = (List<Server>)groupServers.get(reserveGroup);
                    if(CollectionUtils.isNotEmpty(servers)){
                        reserveServers.addAll(servers);
                    }
                }

                return selectServer(reserveServers, key);
            }
        }else{
            //未指定group时，如果没有reserveGroups和blackGroups，则任意选择
            if(ArrayUtils.isEmpty(context.getBlackGroups())){
                log.debug(" no group, no reserve groups, no black groups. select any server. ");
                return selectServer(serverList, key);
            }

            //未指定group时，存在blackGroups，随机选择排除blackGroups后的任意server
            log.debug(" black groups exist. ");
            Set<String> allGroups = groupServers.keySet();
            for(String blackGroup:context.getBlackGroups()){
                log.debug(" remove black group:{} ", blackGroup);
                allGroups.remove(blackGroup);
            }

            List<Server> candidateServers = new ArrayList<Server>();
            for(String group:allGroups){
                log.debug(" group:{} is candidate. ", group);
                List<Server> servers = (List<Server>)groupServers.get(group);
                if(CollectionUtils.isNotEmpty(servers)){
                    candidateServers.addAll(servers);
                }
            }

            return selectServer(candidateServers, key);
        }

        log.warn(" no server is selected. ");
        return null;
    }

    /**
     * 选择server
     * @param availableServers
     * @param key
     * @return
     */
    private Server selectServer(List<Server> availableServers, Object key){
        if(CollectionUtils.isEmpty(availableServers)){
            return null;
        }

        Optional<Server> server = this.getPredicate().chooseRoundRobinAfterFiltering(availableServers, key);
        if(server.isPresent()){
            log.debug(" server:{} is selected. ", server.get().getId());
        }
        return server.isPresent() ? server.get() : null;
    }


    /**
     * 根据group组队
     * @param serverList
     * @return
     */
    private Multimap<String, Server> groupServers(List<Server> serverList){
        Multimap<String, Server> serverMultimap = ArrayListMultimap.create();
        for(Server server:serverList){
            DiscoveryEnabledServer discoveryEnabledServer = (DiscoveryEnabledServer)server;
            String groupName = discoveryEnabledServer.getInstanceInfo().getAppGroupName();
            serverMultimap.put(StringUtils.lowerCase(groupName), server);
        }

        return serverMultimap;
    }

}
