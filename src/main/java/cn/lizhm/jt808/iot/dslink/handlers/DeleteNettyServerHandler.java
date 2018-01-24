package cn.lizhm.jt808.iot.dslink.handlers;

import cn.lizhm.jt808.iot.dslink.collections.PathsCache;
import cn.lizhm.jt808.iot.dslink.collections.ServerPortsCache;
import cn.lizhm.jt808.iot.dslink.collections.NettyServersCache;
import cn.lizhm.jt808.iot.dslink.model.Jt808Constants;
import cn.lizhm.jt808.iot.dslink.tcp.server.ControlNettyServer;
import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.node.NodeManager;
import org.dsa.iot.dslink.node.actions.ActionResult;
import org.dsa.iot.dslink.util.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 删除tcp-server节点，以及释放端口
 *
 * @author: lizhm
 * @date: 2018/1/19 15:29
 */
public class DeleteNettyServerHandler implements Handler<ActionResult> {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteNettyServerHandler.class);

    private NodeManager manager;
    private ControlNettyServer controlNettyServer = ControlNettyServer.getInstance();
    private PathsCache pathsCache = PathsCache.getInstance();
    private ServerPortsCache serverPortsCache = ServerPortsCache.getInstance();
    private NettyServersCache nettyServersCache = NettyServersCache.getInstance();

    public DeleteNettyServerHandler(NodeManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(ActionResult event) {
        LOG.info("Entering delete tcp server handle");
        //获取父节点
        String name = event.getNode().getParent().getName();
        String port = event.getNode().getAttribute(Jt808Constants.PORT).getNumber().toString();
        Node parent = manager.getSuperRoot();
        parent.removeChild(event.getNode().getParent(), false);

        //删除数据集中相应记录
        removeServerCache(name, port);

        LOG.info("Server {} deleted", event.getNode().getParent().getName());
        controlNettyServer.deleteServer(name, port);
    }

    private void removeServerCache(String name, String port) {
        nettyServersCache.remove(name);
        pathsCache.removeServer(name);
        serverPortsCache.remove(port);
    }
}
