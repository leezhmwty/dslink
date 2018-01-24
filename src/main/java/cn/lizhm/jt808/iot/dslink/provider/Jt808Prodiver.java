package cn.lizhm.jt808.iot.dslink.provider;

import cn.lizhm.jt808.iot.dslink.collections.PathsCache;
import cn.lizhm.jt808.iot.dslink.model.Jt808Constants;
import cn.lizhm.jt808.iot.dslink.tcp.server.ControlNettyServer;
import org.dsa.iot.dslink.DSLink;
import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.node.NodeBuilder;
import org.dsa.iot.dslink.node.NodeManager;
import org.dsa.iot.dslink.node.actions.Action;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.node.value.ValueType;

import java.util.Map;
import java.util.Set;

/**
 * @author: lizhm
 * @date: 2018/1/22 10:21
 */
public class Jt808Prodiver {

    private ActionProvider actionProvider = new ActionProvider();
    private NodeManager manager;
    private ControlNettyServer controlNettyServer = ControlNettyServer.getInstance();
    private PathsCache pathsCache = PathsCache.getInstance();


    /**
     * Jt808-dslink初始化
     *
     * @param link
     */
    public void run(DSLink link) {

        manager = link.getNodeManager();
        Node root = manager.getNode("/").getNode();

        Node status = root.createChild(Jt808Constants.STATUS, false).build();
        status.setValueType(ValueType.STRING);
        status.setValue(new Value(Jt808Constants.READY));

        //添加Action,并确定Action内部是否需要再添加Action
        //创建tcp server
        Action act = actionProvider.getSetupTcpServerAction(manager);
        NodeBuilder builder = root.createChild(Jt808Constants.SETUP_TCP_SERVER, true);
        builder.setAction(act).setSerializable(false).setHasChildren(true);
        builder.build();

        configureAction(root);
    }

    private void configureAction(Node superRoot) {
        Map<String, Node> childs = superRoot.getChildren();
        for (Map.Entry<String, Node> entry : childs.entrySet()) {
            Node node = entry.getValue();

            if (node.getAttribute(Jt808Constants.ACTION) != null && node.getAttribute(Jt808Constants.ACTION).getBool
                    () == true) {

                //重启时初始化服务端并重建数据集
                String serverName = node.getName();
                int port = node.getAttribute(Jt808Constants.PORT).getNumber().intValue();
                controlNettyServer.startServer(port, serverName, node.getPath());

                //初始化时在节点上注册action
                NodeBuilder builder = node.createChild(Jt808Constants.DELETE_TCP_SERVER, false);
                builder.setAction(actionProvider.getDeleteTCPServerHandlerAction(manager));
                builder.setSerializable(false);
                builder.build();

                {
                    builder = node.createChild(Jt808Constants.STOP_TCP_SERVER, false);
                    builder.setAction(actionProvider.getStopTCPServerHandlerAction());
                    builder.setSerializable(false);
                    builder.build();
                }

                {
                    builder = node.createChild(Jt808Constants.START_TCP_SERVER, false);
                    builder.setAction(actionProvider.getStartTCPServerHandlerAction());
                    builder.setSerializable(false);
                    builder.build();
                }

                {
                    builder = node.createChild(Jt808Constants.QUERY_ONLINE_TERMINAL, false);
                    builder.setAction(actionProvider.getQueryOnlineTerminalHandlerAction());
                    builder.setSerializable(false);
                    builder.build();
                }

                //初始化terminal节点
                Map<String, Node> terminals = node.getChildren();
                Set<Map.Entry<String, Node>> entries = terminals.entrySet();
                for (Map.Entry<String, Node> terminal : entries) {
                    Node terminalNode = terminal.getValue();
                    if (terminalNode.getAttribute(Jt808Constants.TERMINAL) != null && terminalNode.getAttribute(Jt808Constants
                            .TERMINAL).getBool
                            () == true) {
                        terminalNode.getChild(Jt808Constants.STATUS, false).setValue(new Value(Jt808Constants
                                .DISCONN));
                        terminalNode.getChild(Jt808Constants.LOCATION_INFO, false).setValue(new Value
                                (""));
                        terminalNode.getChild(Jt808Constants.OTHER_INFO, false).setValue(new Value(""));

                        //添加Action
                        NodeBuilder terminalBuilder = terminalNode.createChild(Jt808Constants.MODIFY_HEART_BEAT_VAL, false);
                        terminalBuilder.setAction(actionProvider.getModifyHeartBeatIntervalHandlerAction());
                        terminalBuilder.setSerializable(false);
                        terminalBuilder.build();

                        {
                            terminalBuilder = terminalNode.createChild(Jt808Constants.QUERY_AUTH_CODE, false);
                            terminalBuilder.setAction(actionProvider.getQueryAuthCodeHandler());
                            terminalBuilder.setSerializable(false);
                            terminalBuilder.build();
                        }

                        {
                            builder = terminalNode.createChild(Jt808Constants.REMOVE_TERMINAL, false);
                            builder.setAction(actionProvider.getRemoveTerminalHandler(manager));
                            builder.setSerializable(false);
                            builder.build();
                        }

                        {
                            builder = terminalNode.createChild(Jt808Constants.REMOVE_TERMINAL, false);
                            builder.setAction(actionProvider.getRemoveTerminalHandler(manager));
                            builder.setSerializable(false);
                            builder.build();
                        }
                        //重建数据集
                        pathsCache.putTerminal(terminal.getKey(), terminalNode.getPath());
                    }
                }
            }
        }
    }
}
