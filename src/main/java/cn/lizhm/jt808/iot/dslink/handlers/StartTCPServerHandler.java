package cn.lizhm.jt808.iot.dslink.handlers;

import cn.lizhm.jt808.iot.dslink.collections.TCPServersCache;
import cn.lizhm.jt808.iot.dslink.model.Jt808Constants;
import cn.lizhm.jt808.netty.server.TCPServer;
import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.node.actions.ActionResult;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.util.handler.Handler;

/**
 * @author: lizhm
 * @date: 2018/1/23 10:00
 */
public class StartTCPServerHandler implements Handler<ActionResult> {

    @Override
    public void handle(ActionResult event) {
        String serverName = event.getNode().getParent().getName();
        Node status = event.getNode().getChild(serverName, false);
        TCPServer tcpServer = TCPServersCache.getInstance().getServer(serverName);
        if (tcpServer.isRunning()) {
            status.setValue(new Value(serverName + " already bind"));
            return;
        }
        tcpServer.startServer();
        status.setValue(new Value(Jt808Constants.BIND));
    }
}
