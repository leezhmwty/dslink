package cn.lizhm.jt808.iot.dslink.tcp.server;

import cn.lizhm.jt808.iot.dslink.collections.PathsCache;
import cn.lizhm.jt808.iot.dslink.collections.ServerPortsCache;
import cn.lizhm.jt808.iot.dslink.collections.TCPServersCache;
import cn.lizhm.jt808.netty.server.TCPServer;

/**
 * @author: lizhm
 * @date: 2018/1/19 16:44
 */
public class ControlTCPServer {

    private static ControlTCPServer instance = null;

    private ControlTCPServer() {
    }

    public static ControlTCPServer getInstance() {
        if (instance == null) {
            synchronized (ControlTCPServer.class) {
                instance = new ControlTCPServer();
            }
        }
        return instance;
    }

    private TCPServersCache tcpServersCache = TCPServersCache.getInstance();
    private ServerPortsCache serverPortsCache = ServerPortsCache.getInstance();
    private PathsCache pathsCache = PathsCache.getInstance();

    public void startServer(int port, String name, String serverPath) {
        //数据集中添加记录
        tcpServersCache.addServer(name, new TCPServer(port).startServer());
        serverPortsCache.put(String.valueOf(port), name);
        pathsCache.putServer(name, serverPath);
    }

    public void stopServer(String name) {
        TCPServer server = tcpServersCache.getServer(name);
        if (server != null) {
            server.stopServer();
        }
    }

    public void deleteServer(String name, String port) {
        TCPServer tcpServer = tcpServersCache.getServer(name);
        if (tcpServer != null && tcpServer.isRunning()) {
            tcpServer.stopServer();
        }
        //清理数据集
        tcpServersCache.remove(name);
        pathsCache.removeServer(name);
        serverPortsCache.remove(port);
    }

    public void restartServer(String name) {
        TCPServer server = tcpServersCache.getServer(name);
        if (server != null) {
            server.stopServer();
            server.startServer();
        }
    }
}
