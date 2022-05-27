package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler {
    private FirstServerHandler firstServerHandler;
    private List<FirstServerHandler> clients;

//    public ClientHandler (FirstServerHandler firstServerHandler) {
//        this.firstServerHandler = firstServerHandler;
//    }
    //храним список активных клиентов
    public void subscribe (FirstServerHandler firstServerHandler) throws IOException {
        clients = new CopyOnWriteArrayList<>();
        clients.add(firstServerHandler);
        System.out.println(clients);

    }

    public void unsubscribe (FirstServerHandler firstServerHandler) {
        clients.remove(firstServerHandler);
    }

    public void createDirectory (FirstServerHandler firstServerHandler) throws IOException {

        //создаём папку с именем авторизованного пользователя, если её ещё нет
        String dir = "server/src/main/clients/ "+firstServerHandler.getId();
        if (!Files.exists(Paths.get(dir))) {
            Path clientDir = Files.createDirectory(Paths.get(dir));
            System.out.println("Создана новая директория: "+ clientDir);
        }


    }


}
