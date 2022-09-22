package org.tron.p2p.connection.socket;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.tron.p2p.connection.Channel;
import org.tron.p2p.connection.ChannelManager;
import org.tron.p2p.discover.NodeManager;

@Slf4j(topic = "net")
public class P2pChannelInitializer extends ChannelInitializer<NioSocketChannel> {

//  private ApplicationContext ctx;

  private Channel channel;
  private NodeManager nodeManager;
  private ChannelManager channelManager;

  private String remoteId;

  private boolean peerDiscoveryMode = false;

  public P2pChannelInitializer(String remoteId, ChannelManager channelManager,
      NodeManager nodeManager) {
    this.remoteId = remoteId;
    this.channelManager = channelManager;
    this.nodeManager = nodeManager;
  }

  @Override
  public void initChannel(NioSocketChannel ch) {
    try {
      //final Channel channel = ctx.getBean(Channel.class);

      channel = new Channel();
      channel.init(ch.pipeline(), remoteId, peerDiscoveryMode, channelManager, nodeManager);

      // limit the size of receiving buffer to 1024
      ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(256 * 1024));
      ch.config().setOption(ChannelOption.SO_RCVBUF, 256 * 1024);
      ch.config().setOption(ChannelOption.SO_BACKLOG, 1024);

      // be aware of channel closing
      ch.closeFuture().addListener((ChannelFutureListener) future -> {
        log.info("Close channel:" + channel);
        if (!peerDiscoveryMode) {
          channelManager.notifyDisconnect(channel);
        }
      });

    } catch (Exception e) {
      log.error("Unexpected error: ", e);
    }
  }

  public void setPeerDiscoveryMode(boolean peerDiscoveryMode) {
    this.peerDiscoveryMode = peerDiscoveryMode;
  }
}
