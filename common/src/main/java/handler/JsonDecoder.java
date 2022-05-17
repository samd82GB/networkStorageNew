package handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import message.Message;

import java.util.List;

public class JsonDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();



    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final byte [] bytes = ByteBufUtil.getBytes(msg);
        Message message = OBJECT_MAPPER.readValue(bytes, Message.class);
        out.add(message);
    }
/*
    @Override
    protected void decode(ChannelHandlerContext ctx, String s, List<Object> out) throws Exception {
        System.out.println("incoming string:" + s);
        Message message = OBJECT_MAPPER.readValue(s.getBytes(), Message.class);
        out.add(message);
    }*/
}
