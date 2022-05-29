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
//    private RegMessage regMessage;
//    private AuthMessage authMessage;
//    private FileRequestMessage fileRequestMessage;
    private Bootstrap bootstrap;
    private RegController regController;
    private Controller controller;
    private Channel channel;
    private NioEventLoopGroup group;
    private String[] token;




    public void start() {
        //Клиенту достаточно одного ThreadPool для обработки сообщений
        group = new NioEventLoopGroup(1);
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
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
//                                        RegMessage regMessage = regController.getRegMessage();
//                                    if (regMessage != null) {
//                                        System.out.println("out message: "+regMessage.getLogin());
//                                        System.out.println("out message: "+regMessage.getPassword());
//                                        ctx.writeAndFlush(regMessage);
//                                    }
//                                        AuthMessage authMessage = regController.getAuthMessage();
//                                    if (authMessage != null) {
//                                        System.out.println(authMessage.getLogin());
//                                        System.out.println(authMessage.getPassword());
//                                        ctx.writeAndFlush(authMessage);
//                                    }

//                                            final FileRequestMessage message = new FileRequestMessage();
//                                            message.setPath("g:\\GeekBrains\\Video\\04_Операционные системы\\07_Работа в Linux.MP4");
//                                            ctx.writeAndFlush(message);
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                                    if (msg instanceof TextMessage) {
                                        System.out.println("income TextMessage");
                                        TextMessage textMessage = (TextMessage) msg;
                                        String txt = textMessage.getText();
                                        System.out.println(txt);
                                        if (txt.startsWith("reg") || txt.startsWith("auth")) {
                                            regController.regResult(txt);
                                        }

                                        token = txt.split(" ", 3);
                                        if (token.length ==3) {
                                            controller.serverDirView(token[2]);
                                        }


                                    }

                                    if (msg instanceof FileDeleteMessage) {
                                        FileDeleteMessage fdm = (FileDeleteMessage) msg;
                                        if(fdm.isDeleted()) {
                                            controller.serverDirView(token[2]);
                                            fdm.setDeleted(false);
                                        };
                                    }


                                    if (msg instanceof FileContentMessage) {
                                        FileContentMessage fcm = (FileContentMessage) msg;

                                        try (final RandomAccessFile accessFile = new RandomAccessFile("g:\\GeekBrains\\300.mts", "rw")) {
                                            if (accessFile.length() != 0) {
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

            channel = bootstrap.connect("localhost",7000).sync().channel();
            System.out.println("application.Client started");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            group.shutdownGracefully();
        }
    }


    public RegController getRegController() {
        return regController;
    }

    public void setRegController(RegController regController) {
        this.regController = regController;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public Channel getChannel() {
        return channel;
    }

//    public Client (RegController regController, Controller controller) {
//        this.regController = regController;
//        this.controller = controller;

//    controller.setClient(this);
//    regController.setClient(this);


    public NioEventLoopGroup getGroup() {
        return group;
    }

}
