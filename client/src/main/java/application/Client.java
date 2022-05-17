package application;

import handler.JsonDecoder;
import handler.JsonEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import message.*;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Client {
    public static void main(String[] args) {
        new Client().start();
    }

    public void start() {
        //Клиенту достаточно одного ThreadPool для обработки сообщений
        final NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                    new LengthFieldPrepender(3),
                                    new JsonDecoder(),
                                    new JsonEncoder(),
                                    new SimpleChannelInboundHandler<Message>() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            final FileRequestMessage message = new FileRequestMessage();
                                            message.setPath("g:\\GeekBrains\\Video\\04_Операционные системы\\07_Работа в Linux.MP4");
                                            ctx.writeAndFlush(message);
                                        }

                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                                            if (msg instanceof FileContentMessage){
                                                FileContentMessage fcm = (FileContentMessage)msg;

                                                try (final RandomAccessFile accessFile = new RandomAccessFile("g:\\GeekBrains\\300.mts","rw")) {
                                                   if (accessFile.length()!=0) {
                                                       System.out.println("Получено %: " + fcm.getStartPosition() * 100 / accessFile.length());
                                                   }
                                                    accessFile.seek(fcm.getStartPosition());
                                                    accessFile.write(fcm.getContent());
                                                if (fcm.isLast()) {
                                                    ctx.close();
                                                    System.out.println("Получен последний байт");

                                                }

                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }


                                        }
                                    }
                            );
                        }
                    });

            System.out.println("application.Client started");


            Channel channel = bootstrap.connect("localhost",9000).sync().channel();
            channel.closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
