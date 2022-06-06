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
    private String clientDirOnServer;

    public ClientHandler (FirstServerHandler firstServerHandler) {
        this.firstServerHandler = firstServerHandler;
    }


    //храним список активных клиентов
    public void subscribe () throws IOException {
        clients = new CopyOnWriteArrayList<>();
        clients.add(firstServerHandler);
        System.out.println(clients);

    }

    public void unsubscribe () {
        clients.remove(firstServerHandler);
    }

    public String createDirectory () throws IOException {
        String serverDir = "";
        //создаём папку с именем авторизованного пользователя, если её ещё нет
        clientDirOnServer = System.getProperty("user.dir")+firstServerHandler.getId();
       // String dir = "server/src/main/clients/"+firstServerHandler.getId();
        if (!Files.exists(Paths.get(clientDirOnServer))) {
            Path clientDir = Files.createDirectory(Paths.get(clientDirOnServer));
            System.out.println("Создана новая директория: "+ clientDirOnServer);
        }

        if (Files.exists(Paths.get(clientDirOnServer))) {
            serverDir = Paths.get(clientDirOnServer).toString();
        }
        return serverDir;


    }

    public boolean deleteFile () throws IOException {
        String name = firstServerHandler.getFileToDeleteName();
//        String fileDir = clientDirOnServer+"\\"+name;
        System.out.println("file to delete: "+name);
        if (Files.exists(Paths.get(name))) {
            Files.delete(Paths.get(name));
        }
        return true;
    }



}
