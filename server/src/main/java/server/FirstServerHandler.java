package server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FirstServerHandler extends SimpleChannelInboundHandler<Message> {
    private int counter = 0;
    private RandomAccessFile accessFile;
    private SQLHandler sqlHandler;
    private ClientHandler clientHandler;
    private String serverDirectory;
    private String id;



    private String fileToDeleteName;


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("New active channel");
        clientHandler= new ClientHandler(this);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws IOException {

        //обработка сообщения о регистрации
        if (msg instanceof RegMessage) {
            RegMessage message = (RegMessage) msg;
            System.out.println("incoming login regMessage: " + message.getLogin());
            System.out.println("incoming password regMessage: " + message.getPassword());

            sqlHandler = new SQLHandler();
            boolean reg = sqlHandler.registration(message.getLogin(), message.getPassword());

            TextMessage textMessage = new TextMessage();
            if (reg) {

                textMessage.setText("regOK");
            } else {
                textMessage.setText("regError");
            }
            ctx.writeAndFlush(textMessage);

        }

        //обработка сообщения об авторизации
        if (msg instanceof AuthMessage) {
            AuthMessage message = (AuthMessage) msg;
            System.out.println("incoming login authMessage: " + message.getLogin());
            System.out.println("incoming password authMessage: " + message.getPassword());

            sqlHandler = new SQLHandler();
            id = sqlHandler.getIdByLoginAndPassword(message.getLogin(), message.getPassword());
            System.out.println(id);
            TextMessage textMessage = new TextMessage();
            if (!id.equals("")) {
                serverDirectory = clientHandler.createDirectory(); //создаём директорию нового пользователя на сервере
                clientHandler.subscribe();
                textMessage.setText(String.format("auth %s %s", id, serverDirectory ));
            } else {
                textMessage.setText("authError");
            }
            ctx.writeAndFlush(textMessage);


        }

        if (msg instanceof FileContentMessage) {
            FileContentMessage fcm = (FileContentMessage) msg;
            String fileDirectory = fcm.getFileName();

            try (final RandomAccessFile accessFile = new RandomAccessFile(fileDirectory, "rw")) {
                if (accessFile.length() != 0) {
                    System.out.println("Получено %: " + fcm.getStartPosition() * 100 / accessFile.length());
                }
                accessFile.seek(fcm.getStartPosition());
                accessFile.write(fcm.getContent());
                if (fcm.isLast()) {
                    System.out.println("Получен последний байт");

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (msg instanceof FileDeleteMessage) {
            FileDeleteMessage fileDeleteMessage = (FileDeleteMessage) msg;
            fileToDeleteName = fileDeleteMessage.getFileName();

            if (clientHandler.deleteFile()) {
             fileDeleteMessage.setDeleted(true);
            }
            ctx.writeAndFlush(fileDeleteMessage);
        }


        if (msg instanceof FileRequestMessage) { //получаем сообщение типа запроса на передачу
            FileRequestMessage frm = (FileRequestMessage) msg; //создаём объект сообщения типа запроса из сообщения
            System.out.println(frm.getPath());
            if (accessFile == null) { //если объект доступа к файлу пустой
                final File file = new File(frm.getPath()); //задаём файл с адресом
                accessFile = new RandomAccessFile(file, "r"); //создаём объект доступа к файлу в режиме чтения
                sendFile(ctx); //вызываем метод отправки файла
            }
        }
    }

    private void sendFile(ChannelHandlerContext ctx) throws IOException {
            if (accessFile != null) { //если объект доступа к файлу не нулевой
                final byte[] fileContent; //создаём массив байт для передачи пакета
                final long available = accessFile.length()-accessFile.getFilePointer(); //вычисляем остаток байт для передачи
                if (available > 64 * 1024) {    //если остаток больше заданного размера пакета
                    fileContent = new byte[64 * 1024]; //размер пакета будет равен заданному размеру
                 } else {
                    fileContent = new byte [(int)available]; //иначе размер пакета равен остатку байт
                }
                final FileContentMessage message = new FileContentMessage(); //создаём объект сообщения типа содержимого сообщения

                message.setStartPosition(accessFile.getFilePointer()); //задаём место чтения в сообщении
                accessFile.read(fileContent); //читаем из файла в пакет заданного размера
                message.setContent(fileContent); //записываем считанный пакет в содержимое сообщения
                final boolean last = accessFile.getFilePointer()==accessFile.length();
                message.setLast(last); //устанавливаем флаг последнего байта если файл считан до длины файла

                ctx.writeAndFlush(message) //ставим в очередь задачу передачи
                        .addListener(new ChannelFutureListener() { //добавляем листенер, который при окончании операции вызывает метод
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if (!last) {
                                    sendFile(ctx); //если байт не последний, то снова вызываем метод отправки файла
                                }
                            }
                        });
                if (last) {
                    accessFile.close();
                    accessFile = null;
                }
                System.out.println("Message sent "+ ++counter);
        }
        }





    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws IOException {
        System.out.println("client disconnect");
        if (accessFile != null) {
            accessFile.close();
        }
        clientHandler.unsubscribe();
    }

    public String getId() {
        return id;
    }

    public String getFileToDeleteName() {
        return fileToDeleteName;
    }

}
