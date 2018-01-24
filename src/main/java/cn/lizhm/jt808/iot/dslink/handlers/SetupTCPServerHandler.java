package cn.lizhm.jt808.iot.dslink.handlers;

import cn.lizhm.jt808.iot.dslink.collections.ServerPortsCache;
import cn.lizhm.jt808.iot.dslink.collections.TCPServersCache;
import cn.lizhm.jt808.iot.dslink.model.Jt808Constants;
import cn.lizhm.jt808.iot.dslink.provider.ActionProvider;
import cn.lizhm.jt808.iot.dslink.tcp.server.ControlTCPServer;
import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.node.NodeBuilder;
import org.dsa.iot.dslink.node.NodeManager;
import org.dsa.iot.dslink.node.actions.ActionResult;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.node.value.ValueType;
import org.dsa.iot.dslink.util.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 初始化Netty TCP-Server
 *
 * @author: lizhm
 * @date: 2018/1/17 15:07
 */
public class SetupTCPServerHandler implements Handler<ActionResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetupTCPServerHandler.class);

    public SetupTCPServerHandler(NodeManager manager) {
        this.manager = manager;
        this.root = manager.getSuperRoot();
    }

    private NodeManager manager;
    private Node root;
    private Node serverNode;
    private ControlTCPServer controlTCPServer = ControlTCPServer.getInstance();
    private ActionProvider actionProvider = new ActionProvider();
    private TCPServersCache tcpServersCache = TCPServersCache.getInstance();
    private ServerPortsCache serverPortsCache = ServerPortsCache.getInstance();

    @Override
    public void handle(ActionResult event) {
        String name = event.getParameter(Jt808Constants.SERVER_NAME, ValueType.STRING).getString();
        int port = event.getParameter(Jt808Constants.PORT, ValueType.NUMBER).getNumber().intValue();
        Node status = manager.getSuperRoot().getChild(Jt808Constants.STATUS, false);

        if (name == null || name.equals("")) {
            status.setValue(new Value("name can not be null"));
            return;
        } else if (tcpServersCache.getServers().containsKey(name)) {
            status.setValue(new Value("name already exist"));
            return;
        }

        if (port < Jt808Constants.MIN_PORT || port > Jt808Constants.MAX_PORT) {
            status.setValue(new Value("illegal port"));
            return;
        } else if (tcpServersCache.getServers().containsValue(port)) {
            status.setValue(new Value("port already exist"));
            return;
        }

        serverNode = root.createChild(name, true).build();
        serverNode.createChild(Jt808Constants.STATUS, false).setValue(new Value(Jt808Constants.BIND)).build();
        serverNode.setAttribute(Jt808Constants.PORT, new Value(port));
        serverNode.setAttribute(Jt808Constants.ACTION, new Value(true));

        LOGGER.info("创建tcp server: name={},port={}", name, port);
        // 调用Netty初始化tcp server代码
        controlTCPServer.startServer(port, name, serverNode.getPath());

        //添加port-name映射关系
        serverPortsCache.put(String.valueOf(port), name);

        NodeBuilder builder = serverNode.createChild(Jt808Constants.DELETE_TCP_SERVER, false);
        builder.setAction(actionProvider.getDeleteTCPServerHandlerAction(manager));
        builder.setSerializable(false);
        builder.build();

        {
            builder = serverNode.createChild(Jt808Constants.STOP_TCP_SERVER, false);
            builder.setAction(actionProvider.getStopTCPServerHandlerAction());
            builder.setSerializable(false);
            builder.build();
        }

        {
            builder = serverNode.createChild(Jt808Constants.START_TCP_SERVER, false);
            builder.setAction(actionProvider.getStartTCPServerHandlerAction());
            builder.setSerializable(false);
            builder.build();
        }

        {
            builder = serverNode.createChild(Jt808Constants.QUERY_ONLINE_TERMINAL, false);
            builder.setAction(actionProvider.getQueryOnlineTerminalHandlerAction());
            builder.setSerializable(false);
            builder.build();
        }
    }
}
