package org.tron.p2p.connection.message;

import org.apache.commons.lang3.ArrayUtils;
import org.tron.p2p.connection.message.handshake.HelloMessage;
import org.tron.p2p.connection.message.keepalive.PongMessage;
import org.tron.p2p.exception.P2pException;

public abstract class Message {

  protected MessageType type;
  protected byte[] data;


  public Message(MessageType type, byte[] data) {
    this.type = type;
    this.data = data;
  }

  public byte[] getSendData() {
    return ArrayUtils.add(this.data, 0, type.getType());
  }

  public byte[] getData() {
    return this.data;
  }

  public MessageType getType() {
    return this.type;
  }

  public abstract boolean valid();

  public static Message parse(byte[] encode) throws Exception {
    byte type = encode[0];
    byte[] data = ArrayUtils.subarray(encode, 1, encode.length);
    Message message = null;
    switch (MessageType.fromByte(type)) {
      case KEEP_ALIVE_PING:
        break;
      case KEEP_ALIVE_PONG:
        break;
      case HANDSHAKE_HELLO:
        message = new HelloMessage(data);
        break;
      default:
        throw new P2pException(P2pException.TypeEnum.NO_SUCH_MESSAGE, "type=" + type);
    }
    if (!message.valid()) {
      throw new P2pException(P2pException.TypeEnum.BAD_MESSAGE, "type=" + type);
    }
    return message;
  }

}
