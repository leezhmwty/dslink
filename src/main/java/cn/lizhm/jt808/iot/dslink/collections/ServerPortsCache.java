package cn.lizhm.jt808.iot.dslink.collections;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: lizhm
 * @date: 2018/1/24 10:21
 */
public class ServerPortsCache {

    private static ConcurrentHashMap<String, String> serverPorts = null;
    private static ServerPortsCache instance = null;

    public static ServerPortsCache getInstance() {
        if (instance == null) {
            synchronized (ServerPortsCache.class) {
                instance = new ServerPortsCache();
            }
        }
        return instance;
    }

    private ServerPortsCache() {
        serverPorts = new ConcurrentHashMap<>();
    }

    public void put(String port, String serverName) {
        serverPorts.put(port, serverName);
    }

    public String getServerNameByPort(String port) {
        return serverPorts.get(port);
    }

    public void remove(String port) {
        serverPorts.remove(port);
    }
}
