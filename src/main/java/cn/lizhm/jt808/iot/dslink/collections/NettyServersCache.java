package cn.lizhm.jt808.iot.dslink.collections;

import cn.lizhm.jt808.netty.server.TCPServer;
import org.msgpack.core.Preconditions;

import java.util.concurrent.ConcurrentHashMap;

/**
 * TCPServer对象缓存
 *
 * @author: lizhm
 * @date: 2018/1/19 16:14
 */
public class NettyServersCache {

    private static NettyServersCache nettyServersCache = null;

    public ConcurrentHashMap<String, TCPServer> getServers() {
        return servers;
    }

    /**
     * key:serverName,value:TCPServer
     */
    private static ConcurrentHashMap<String, TCPServer> servers = null;

    private NettyServersCache() {
        servers = new ConcurrentHashMap<>();
    }

    public static NettyServersCache getInstance() {
        if (nettyServersCache == null) {
            synchronized (NettyServersCache.class) {
                nettyServersCache = new NettyServersCache();
            }
        }
        return nettyServersCache;
    }

    public boolean addServer(String serverId, TCPServer tcpServer) {
        Preconditions.checkArgument(serverId != null, "tcp服务id不能为空");
        Preconditions.checkArgument(tcpServer != null, "TCPServer对象不能为空");
        return servers.put(serverId, tcpServer) != null;
    }

    public boolean remove(String serverId) {
        Preconditions.checkArgument(serverId != null, "tcp服务id不能为空");
        return servers.remove(serverId) != null;
    }

    public TCPServer getServer(String serverId) {
        Preconditions.checkArgument(serverId != null, "tcp服务id不能为空");
        return servers.get(serverId);
    }
}
