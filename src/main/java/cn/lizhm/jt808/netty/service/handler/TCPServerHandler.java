package cn.lizhm.jt808.netty.service.handler;

import cn.lizhm.jt808.iot.dslink.handlers.terminal.AutoAddTerminalHandler;
import cn.lizhm.jt808.netty.common.TPMSConsts;
import cn.lizhm.jt808.netty.server.SessionManager;
import cn.lizhm.jt808.netty.service.TerminalMsgProcessService;
import cn.lizhm.jt808.netty.service.codec.MsgDecoder;
import cn.lizhm.jt808.netty.vo.PackageData;
import cn.lizhm.jt808.netty.vo.Session;
import cn.lizhm.jt808.netty.vo.req.LocationInfoUploadMsg;
import cn.lizhm.jt808.netty.vo.req.TerminalAuthenticationMsg;
import cn.lizhm.jt808.netty.vo.req.TerminalRegisterMsg;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPServerHandler extends ChannelInboundHandlerAdapter { // (1)

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SessionManager sessionManager;
    private final MsgDecoder decoder;
    private TerminalMsgProcessService msgProcessService;
    private AutoAddTerminalHandler handler = new AutoAddTerminalHandler();

    public TCPServerHandler() {
        this.sessionManager = SessionManager.getInstance();
        this.decoder = new MsgDecoder();
        this.msgProcessService = new TerminalMsgProcessService();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException { // (2)
        try {
            ByteBuf buf = (ByteBuf) msg;
            if (buf.readableBytes() <= 0) {
                // ReferenceCountUtil.safeRelease(msg);
                return;
            }

            byte[] bs = new byte[buf.readableBytes()];
            buf.readBytes(bs);

            // 字节数据转换为针对于808消息结构的实体类
            PackageData pkg = this.decoder.bytes2PackageData(bs);
            // 引用channel,以便回送数据给硬件
            pkg.setChannel(ctx.channel());
            this.processPackageData(pkg, ctx);
        } finally {
            release(msg);
        }
    }

    /**
     * 处理业务逻辑
     *
     * @param packageData
     */
    private void processPackageData(PackageData packageData, ChannelHandlerContext ctx) {
        final PackageData.MsgHeader header = packageData.getMsgHeader();

        // 1. 终端心跳-消息体为空 ==> 平台通用应答
        if (TPMSConsts.msg_id_terminal_heart_beat == header.getMsgId()) {
            logger.info(">>>>>[终端心跳],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            try {
                this.msgProcessService.processTerminalHeartBeatMsg(packageData);
                logger.info("<<<<<[终端心跳],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                logger.error("<<<<<[终端心跳]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }

        // 5. 终端鉴权 ==> 平台通用应答
        else if (TPMSConsts.msg_id_terminal_authentication == header.getMsgId()) {
            logger.info(">>>>>[终端鉴权],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());

            //鉴权时检查terminalID节点
            //检查是否已经存在terminalID节点，并修改连接状态
            handler.checkTerminalNode(ctx.channel().localAddress().toString(), header.getTerminalPhone());

            try {
                TerminalAuthenticationMsg authenticationMsg = new TerminalAuthenticationMsg(packageData);
                this.msgProcessService.processAuthMsg(authenticationMsg);
                logger.info("<<<<<[终端鉴权],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                logger.error("<<<<<[终端鉴权]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 6. 终端注册 ==> 终端注册应答
        else if (TPMSConsts.msg_id_terminal_register == header.getMsgId()) {

            //注册时创建terminalID节点
            handler.createTerminalIDNode(ctx.channel().localAddress().toString(), header.getTerminalPhone());

            logger.info(">>>>>[终端注册],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            try {
                TerminalRegisterMsg msg = this.decoder.toTerminalRegisterMsg(packageData);
                this.msgProcessService.processRegisterMsg(msg);
                logger.info("<<<<<[终端注册],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                logger.error("<<<<<[终端注册]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 7. 终端注销(终端注销数据消息体为空) ==> 平台通用应答
        else if (TPMSConsts.msg_id_terminal_log_out == header.getMsgId()) {

            //todo 删除鉴权码
            logger.info(">>>>>[终端注销],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            try {
                this.msgProcessService.processTerminalLogoutMsg(packageData);
                logger.info("<<<<<[终端注销],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                logger.error("<<<<<[终端注销]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 3. 位置信息汇报 ==> 平台通用应答
        else if (TPMSConsts.msg_id_terminal_location_info_upload == header.getMsgId()) {

            logger.info(">>>>>[位置信息],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            try {
                //位置数据
                LocationInfoUploadMsg locationInfoUploadMsg = this.decoder.toLocationInfoUploadMsg(packageData);
                //todo 更新location节点值
                handler.updateLocation(header.getTerminalPhone(), JSONObject.toJSONString(locationInfoUploadMsg));

                System.out.println(locationInfoUploadMsg);
                this.msgProcessService.processLocationInfoUploadMsg(locationInfoUploadMsg);
                logger.info("<<<<<[位置信息],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                logger.error("<<<<<[位置信息]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 其他情况
        else {
            logger.error(">>>>>>[未知消息类型],phone={},msgId={},package={}", header.getTerminalPhone(), header.getMsgId(),
                    packageData);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        logger.error("发生异常:{}", cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Session session = Session.buildSession(ctx.channel());
        sessionManager.put(session.getId(), session);
        logger.debug("终端连接:{}", session);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final String sessionId = ctx.channel().id().asLongText();
        Session session = sessionManager.findBySessionId(sessionId);

        //修改节点状态为disconnect
        handler.terminalDisconnect(session.getTerminalPhone());

        this.sessionManager.removeBySessionId(sessionId);
        logger.debug("终端断开连接:{}", session);
        ctx.channel().close();
        // ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                Session session = this.sessionManager.removeBySessionId(Session.buildId(ctx.channel()));
                logger.error("服务器主动断开连接:{}", session);
                ctx.close();
            }
        }
    }

    private void release(Object msg) {
        try {
            ReferenceCountUtil.release(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}