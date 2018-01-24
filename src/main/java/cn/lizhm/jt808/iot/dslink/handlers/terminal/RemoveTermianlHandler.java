package cn.lizhm.jt808.iot.dslink.handlers.terminal;

import cn.lizhm.jt808.iot.dslink.collections.PathsCache;
import cn.lizhm.jt808.iot.dslink.model.Jt808Constants;
import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.node.NodeManager;
import org.dsa.iot.dslink.node.actions.ActionResult;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.util.handler.Handler;

/**
 * @author: lizhm
 * @date: 2018/1/24 14:55
 */
public class RemoveTermianlHandler implements Handler<ActionResult> {

    private NodeManager manager;
    private PathsCache pathsCache = PathsCache.getInstance();

    public RemoveTermianlHandler(NodeManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(ActionResult event) {
        String terminal = event.getNode().getParent().getName();
        String terminalPath = pathsCache.getPathByTerminalPhone(terminal);
        String serverPath = terminalPath.substring(0, terminalPath.lastIndexOf("/"));

        Node terminalNode = manager.getNode(terminalPath).getNode();
        Node status = terminalNode.getChild(Jt808Constants.STATUS, false);
        if (Jt808Constants.CONN.equals(status.getValue().getString())) {
            status.setValue(new Value("can not remove online-terminal"));
            return;
        }

        Node serverNode = manager.getNode(serverPath).getNode();
        serverNode.removeChild(terminal, false);
    }
}
