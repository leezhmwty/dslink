package cn.lizhm.jt808.iot.dslink.collections;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: lizhm
 * @date: 2018/1/24 11:15
 */
public class PathsCache {

    /**
     * key:serverName,value:NodePath
     */
    private static ConcurrentHashMap<String, String> serverPaths = null;
    /**
     * key:terminalPhone,value:NodePath
     */
    private static ConcurrentHashMap<String, String> terminalPaths = null;
    private static PathsCache instance = null;

    public static PathsCache getInstance() {
        if (instance == null) {
            synchronized (PathsCache.class) {
                instance = new PathsCache();
            }
        }
        return instance;
    }

    private PathsCache() {
        serverPaths = new ConcurrentHashMap<>();
        terminalPaths = new ConcurrentHashMap<>();
    }

    public void putServer(String serverName, String nodePath) {
        serverPaths.put(serverName, nodePath);
    }

    public String getPathByServerName(String serverName) {
        return serverPaths.get(serverName);
    }

    public void removeServer(String serverName) {
        serverPaths.remove(serverName);
    }

    public void putTerminal(String terminalPhone, String nodePath) {
        terminalPaths.put(terminalPhone, nodePath);
    }

    public String getPathByTerminalPhone(String terminalPhone) {
        return terminalPaths.get(terminalPhone);
    }

    public void removeTerminal(String terminalPhone) {
        terminalPaths.remove(terminalPhone);
    }
}
