package cn.lizhm.jt808.iot.dslink.handlers.terminal;

import cn.lizhm.jt808.iot.dslink.Jt808DsLink;
import cn.lizhm.jt808.iot.dslink.collections.PathsCache;
import cn.lizhm.jt808.iot.dslink.collections.ServerPortsCache;
import cn.lizhm.jt808.iot.dslink.model.Jt808Constants;
import cn.lizhm.jt808.iot.dslink.provider.ActionProvider;
import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.node.NodeBuilder;
import org.dsa.iot.dslink.node.NodeManager;
import org.dsa.iot.dslink.node.value.Value;

/**
 * @author: lizhm
 * @date: 2018/1/24 13:25
 */
public class AutoAddTerminalHandler {

    private ServerPortsCache serverPortsCache = ServerPortsCache.getInstance();
    private Jt808DsLink jt808DsLink = Jt808DsLink.getInstance();
    private PathsCache pathsCache = PathsCache.getInstance();
    private ActionProvider actionProvider = new ActionProvider();

    /**
     * 创建终端节点
     *
     * @param localAddress
     * @param terminalPhone
     */
    public void createTerminalIDNode(String localAddress, String terminalPhone) {
        //创建连接时获取本地端口，与serverName建立映射关系
        String port = localAddress.substring(localAddress.indexOf(":") + 1);
        //根据port获取连接的serverName
        String serverName = serverPortsCache.getServerNameByPort(port);

        //获取到serverName并创建子节点
        NodeManager manager = jt808DsLink.getLink().getNodeManager();
        Node node = manager.getNode(pathsCache.getPathByServerName(serverName)).getNode();
        if (node.getAttribute(Jt808Constants.ACTION) != null && node.getAttribute(Jt808Constants.ACTION).getBool
                () == true) {
            if (serverName != null && node.getName().equals(serverName)) {
                Node terminalNode = node.createChild(terminalPhone, false).setHasChildren(true).build();
                //添加标志位
                terminalNode.setAttribute(Jt808Constants.TERMINAL, new Value(true));

                terminalNode.createChild(Jt808Constants.STATUS, false).setValue(new Value(Jt808Constants
                        .CONN)).build();
                terminalNode.createChild(Jt808Constants.LOCATION_INFO, false).setValue(new Value("")).build();
                terminalNode.createChild(Jt808Constants.OTHER_INFO, false).setValue(new Value("")).build();
                terminalNode.createChild(Jt808Constants.AUTH_CODE, false).setValue(new Value
                        ("SDUERA324-DJDFF-3E4FDS43")).build();

                //添加Action
                NodeBuilder builder = terminalNode.createChild(Jt808Constants.MODIFY_HEART_BEAT_VAL, false);
                builder.setAction(actionProvider.getModifyHeartBeatIntervalHandlerAction());
                builder.setSerializable(false);
                builder.build();

                builder = terminalNode.createChild(Jt808Constants.QUERY_AUTH_CODE, false);
                builder.setAction(actionProvider.getQueryAuthCodeHandler());
                builder.setSerializable(false);
                builder.build();

                builder = terminalNode.createChild(Jt808Constants.REMOVE_TERMINAL, false);
                builder.setAction(actionProvider.getRemoveTerminalHandler(manager));
                builder.setSerializable(false);
                builder.build();

                pathsCache.putTerminal(terminalPhone, terminalNode.getPath());
            }
        }
    }

    /**
     * 删除终端节点
     *
     * @param localAddress
     * @param terminalPhone
     */
    public void removeTerminalNode(String localAddress, String terminalPhone) {
        String port = localAddress.substring(localAddress.indexOf(":") + 1);
        String serverPath = pathsCache.getPathByServerName(serverPortsCache.getServerNameByPort(port));

        NodeManager manager = jt808DsLink.getLink().getNodeManager();
        Node serverNode = manager.getNode(serverPath).getNode();
        serverNode.removeChild(terminalPhone, false);
    }

    /**
     * 终端断开连接更改节点状态
     *
     * @param terminalPhone
     */
    public void terminalDisconnect(String terminalPhone) {
        if (terminalPhone == null) {
            return;
        }
        String terminalPath = pathsCache.getPathByTerminalPhone(terminalPhone);

        NodeManager manager = jt808DsLink.getLink().getNodeManager();
        Node terminalNode = manager.getNode(terminalPath).getNode();
        terminalNode.getChild(Jt808Constants.STATUS, false).setValue(new Value(Jt808Constants.DISCONN));
    }

    /**
     * 检查是否已经创建终端节点
     * 是，则更新节点状态；否，创建节点
     * 终端鉴权时调用
     *
     * @param localAddress
     * @param terminalPhone
     */
    public void checkTerminalNode(String localAddress, String terminalPhone) {
        String terminalPath = pathsCache.getPathByTerminalPhone(terminalPhone);
        NodeManager manager = jt808DsLink.getLink().getNodeManager();
        Node terminalNode = manager.getNode(terminalPath).getNode();
        if (terminalNode == null) {
            createTerminalIDNode(localAddress, terminalPhone);
            return;
        }
        terminalNode.getChild(Jt808Constants.STATUS, false).setValue(new Value(Jt808Constants.CONN));
    }

    /**
     * 更新位置数据
     *
     * @param terminalPhone
     * @param location
     */
    public void updateLocation(String terminalPhone, String location) {
        String terminalPath = pathsCache.getPathByTerminalPhone(terminalPhone);

        NodeManager manager = jt808DsLink.getLink().getNodeManager();
        Node terminalNode = manager.getNode(terminalPath).getNode();
        terminalNode.getChild(Jt808Constants.LOCATION_INFO, true).setValue(new Value(location));
    }

    /**
     * 删除鉴权码
     *
     * @param terminalPhone
     */
    public void removeAuthCode(String terminalPhone) {
        String terminalPath = pathsCache.getPathByTerminalPhone(terminalPhone);

        NodeManager manager = jt808DsLink.getLink().getNodeManager();
        Node terminalNode = manager.getNode(terminalPath).getNode();
        terminalNode.removeChild(Jt808Constants.AUTH_CODE, false);
    }
}
