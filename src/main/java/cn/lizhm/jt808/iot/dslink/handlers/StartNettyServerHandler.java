package cn.lizhm.jt808.iot.dslink.handlers;

import cn.lizhm.jt808.iot.dslink.collections.NettyServersCache;
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
public class StartNettyServerHandler implements Handler<ActionResult> {

    @Override
    public void handle(ActionResult event) {
        Node server = event.getNode().getParent();
        String serverName = event.getNode().getParent().getName();
        Node status = server.getChild(Jt808Constants.STATUS, false);
        TCPServer tcpServer = NettyServersCache.getInstance().getServer(serverName);
        if (tcpServer.isRunning()) {
            status.setValue(new Value(serverName + " already bind"));
            return;
        }
        tcpServer.startServer();
        status.setValue(new Value(Jt808Constants.BIND));
    }
}
