package cn.lizhm.jt808.iot.dslink.model;

/**
 * @author: lizhm
 * @date: 2018/1/22 13:50
 */
public class Jt808Constants {

    /**
     * boolean
     */
    public static final String ACTION = "action";
    public static final String TERMINAL = "terminal";

    /**
     * Node
     */
    public static final String STATUS = "status";
    public static final String PORT = "port";
    public static final String SERVER_NAME = "name";
    public static final String LOCATION_INFO = "localtion";
    public static final String OTHER_INFO = "other";
    public static final String AUTH_CODE = "auth code";

    /**
     * Action
     */
    public static final String SETUP_TCP_SERVER = "setup tcp server";
    public static final String DELETE_TCP_SERVER = "delete tcp server";
    public static final String STOP_TCP_SERVER = "stop tcp server";
    public static final String START_TCP_SERVER = "start tcp server";
    public static final String MODIFY_HEART_BEAT_VAL = "modify heart beat val";
    public static final String QUERY_AUTH_CODE = "query auth code";
    public static final String QUERY_ONLINE_TERMINAL = "query online terminal";
    public static final String REMOVE_TERMINAL = "remove";

    /**
     * Status
     */
    public static final String BIND = "bind";
    public static final String UNBIND = "unbind";
    public static final String READY = "ready";
    public static final String CONN = "connect";
    public static final String DISCONN = "disconnect";

    /**
     * Port
     */
    public static final int MIN_PORT = 1;
    public static final int MAX_PORT = 65535;
}
