package cn.lizhm.jt808.iot.dslink.provider;

import cn.lizhm.jt808.iot.dslink.handlers.*;
import cn.lizhm.jt808.iot.dslink.handlers.terminal.ModifyHeartBeatIntervalHandler;
import cn.lizhm.jt808.iot.dslink.handlers.terminal.QueryAuthCodeHandler;
import cn.lizhm.jt808.iot.dslink.handlers.terminal.RemoveTermianlHandler;
import cn.lizhm.jt808.iot.dslink.model.Jt808Constants;
import org.dsa.iot.dslink.node.NodeManager;
import org.dsa.iot.dslink.node.Permission;
import org.dsa.iot.dslink.node.actions.Action;
import org.dsa.iot.dslink.node.actions.Parameter;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.node.value.ValueType;

/**
 * action工厂
 *
 * @author: lizhm
 * @date: 2018/1/18 9:17
 */
public class ActionProvider {

    public Action getSetupTcpServerAction(NodeManager manager) {
        Action act = new Action(Permission.READ, new SetupNettyServerHandler(manager));
        act.addParameter(new Parameter(Jt808Constants.SERVER_NAME, ValueType.STRING));
        act.addParameter(new Parameter(Jt808Constants.PORT, ValueType.NUMBER, new Value(502)));
        return act;
    }

    public Action getDeleteTCPServerHandlerAction(NodeManager server) {
        return new Action(Permission.READ, new DeleteNettyServerHandler(server));
    }

    public Action getStopTCPServerHandlerAction() {
        return new Action(Permission.READ, new StopNettyServerHandler());
    }

    public Action getStartTCPServerHandlerAction() {
        return new Action(Permission.READ, new StartNettyServerHandler());
    }

    public Action getQueryOnlineTerminalHandlerAction() {
        return new Action(Permission.READ, new QueryOnlineTerminalHandler());
    }

    public Action getModifyHeartBeatIntervalHandlerAction() {
        return new Action(Permission.READ, new ModifyHeartBeatIntervalHandler());
    }

    public Action getQueryAuthCodeHandler() {
        return new Action(Permission.READ, new QueryAuthCodeHandler());
    }

    public Action getRemoveTerminalHandler(NodeManager manager) {
        return new Action(Permission.READ, new RemoveTermianlHandler(manager));
    }
}
