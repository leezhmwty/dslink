package cn.lizhm.jt808.iot.dslink;

import cn.lizhm.jt808.iot.dslink.provider.Jt808Prodiver;
import org.dsa.iot.dslink.DSLink;
import org.dsa.iot.dslink.DSLinkFactory;
import org.dsa.iot.dslink.DSLinkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: lizhm
 * @Date: 2018/1/19 16:44
 */
public class Jt808DsLink extends DSLinkHandler {
    static private final Logger LOGGER = LoggerFactory.getLogger(Jt808DsLink.class);

    private static Jt808DsLink instance = new Jt808DsLink();

    public static Jt808DsLink getInstance() {
        if (instance == null) {
            synchronized (Jt808DsLink.class) {
                instance = new Jt808DsLink();
            }
        }
        return instance;
    }

    private Jt808DsLink() {
    }

    private DSLink link;


    @Override
    public boolean isRequester() {
        return true;
    }

    @Override
    public boolean isResponder() {
        return true;
    }

    @Override
    public void onResponderInitialized(DSLink link) {

        LOGGER.info("Responser Initialized");
        //用来按路径获取节点
        /*this.nodeManager = link.getNodeManager();

        //序列化器
        Serializer copyser = new Serializer(nodeManager);
        //反序列化器
        Deserializer copydeser = new Deserializer(nodeManager);*/

        //获取根节点
//        Node superRoot = nodeManager.getNode("/").getNode();

    }

    @Override
    public void onRequesterConnected(DSLink link) {
        LOGGER.info("Requester initialized");
    }

    @Override
    public void onResponderConnected(DSLink link) {
        Jt808Prodiver jt808Prodiver = new Jt808Prodiver();
        jt808Prodiver.run(link);
        this.link = link;
        LOGGER.info("JT808 DSLink started");
    }

    public DSLink getLink() {
        return this.link;
    }

    public static void main(String[] args) {
        DSLinkFactory.start(args, getInstance());
    }
}
