package pers.acp.test.application.test;

import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter;
import io.github.zhangbinhub.acp.boot.socket.base.ISocketServerHandle;
import io.github.zhangbinhub.acp.core.CommonTools;
import io.netty.handler.timeout.IdleStateEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import pers.acp.test.application.repo.primary.TableRepo;

/**
 * @author zhangbin by 2018-1-31 11:59
 * @since JDK 11
 */
@Component("TestTcpHandle")
public class TestTcpHandle implements ISocketServerHandle {

    private final LogAdapter log;

    private final TableRepo tableRepo;

    public TestTcpHandle(LogAdapter log, TableRepo tableRepo) {
        this.log = log;
        this.tableRepo = tableRepo;
    }

    @NotNull
    @Override
    public String doResponse(@NotNull String recvStr) {
        log.info("收到报文：" + recvStr);
        String response = CommonTools.objectToJson(tableRepo.findAll()).toString();
        log.info("返回：" + response);
        return response;
    }

    @Override
    public String userEventTriggered(IdleStateEvent evt) throws Exception {
        return null;
    }
}
