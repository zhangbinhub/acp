import io.github.zhangbinhub.acp.core.client.socket.base.ISocketClientHandle;
import io.github.zhangbinhub.acp.core.client.socket.tcp.TcpClient;
import io.github.zhangbinhub.acp.core.client.socket.udp.UdpClient;
import io.netty.handler.timeout.IdleStateEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by zhang on 2016/6/1.
 * 客户端测试demo
 */
public class TestClient {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1; i++) {
            final int x = i + 1;
            new Thread(() -> {
                for (int j = 0; j < 1; j++) {
                    int flag = 1;
                    if (flag == 1) {
                        new TestHttp().doTest();
                    } else if (flag == 2) {
                        UdpClient client = new UdpClient("127.0.0.1", 9999, 60000);
                        client.setServerCharset("gbk");
                        client.setSocketHandle(new ISocketClientHandle() {
                            @Override
                            public void receiveMsg(@NotNull String recvStr) {
                                System.out.println("啊udp：" + recvStr);
                            }

                            @NotNull
                            @Override
                            public String userEventTriggered(@NotNull IdleStateEvent evt) throws Exception {
                                return null;
                            }
                        });
                        client.doSend("你是猪");
                    } else if (flag == 3) {
                        TcpClient client = new TcpClient("169.254.175.124", 9999, 60000, 600000);
                        client.setServerCharset("gbk");
//                        client.setKeepAlive(true);
                        client.setSocketHandle(new ISocketClientHandle() {
                            @Override
                            public void receiveMsg(String recvStr) {
                                System.out.println("啊：" + recvStr);
                            }

                            @Override
                            public String userEventTriggered(IdleStateEvent evt) throws Exception {
                                return null;
                            }
                        });
//                        client.setNeedRead(false);
                        client.doSend("你是猪");
//                        client.doClose();
//                        try {
//                            Thread.sleep(10000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        TcpClient client2 = new TcpClient("169.254.175.124", 9999, 60000, 600000);
//                        client2.setServerCharset("gbk");
//                        client2.setSocketHandle(new ISocketClientHandle() {
//                            @Override
//                            public void receiveMsg(String recvStr) {
//                                System.out.println("啊：" + recvStr);
//                                client2.doClose();
//                            }
//
//                            @Override
//                            public String userEventTriggered(IdleStateEvent evt) throws Exception {
//                                return null;
//                            }
//                        });
//                        client2.doSend("第二个猪");
                    }
                }
            }).start();
        }
    }
}
