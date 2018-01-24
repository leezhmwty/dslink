package cn.lizhm.jt808.iot.dslink.handlers;

import cn.lizhm.jt808.iot.dslink.model.Jt808Constants;
import cn.lizhm.jt808.iot.dslink.tcp.server.ControlNettyServer;
import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.node.actions.ActionResult;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.util.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: lizhm
 * @date: 2018/1/22 11:00
 */
public class StopNettyServerHandler implements Handler<ActionResult> {

    private static final Logger LOG = LoggerFactory.getLogger(StopNettyServerHandler.class);
    private ControlNettyServer controlNettyServer = ControlNettyServer.getInstance();

    @Override
    public void handle(ActionResult event) {
        Node server = event.getNode().getParent();
        Node status = event.getNode().getChild(Jt808Constants.STATUS, false);
        status.setValue(new Value(Jt808Constants.UNBIND));

        String serverName = event.getNode().getParent().getName();
        String port = server.getAttribute(Jt808Constants.PORT).getNumber().toString();
        LOG.info("{},port={} will be unbind", serverName, port);

        controlNettyServer.stopServer(serverName);
        LOG.info("{},port={} unbind", serverName, port);
    }
}
