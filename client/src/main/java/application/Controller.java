package application;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import message.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {
    @FXML
    public Label sendProgress;
    @FXML
    private ListView clientListView;
    @FXML
    private ListView serverListView;
    @FXML
    private TextField clientTextField;
    @FXML
    private TextField serverTextField;
    @FXML
    private Button clRenewButton;
    @FXML
    private Button clSendButton;
    @FXML
    private Button clCreateDirButton;
    @FXML
    private Button srvRenewButton;
    @FXML
    private Button srvRcvButton;
    @FXML
    private Button srvCreateDirButton;
    @FXML
    private Button srvDelButton;
    @FXML
    private Button regButton;


    private Stage regStage;
    private Stage pathStage;
    private Stage mainStage;
    private RegController regController;
    private NewPath newPathController;
    private Client client;
    private Channel channel;
    private FXMLLoader fxmlLoader = new FXMLLoader();
    private FXMLLoader pathFxmlLoader = new FXMLLoader();
    private Parent fxmlReg;
    private Parent fxmlPath;
    private Stage stage;
    private ObservableList<String> clientsFilesList = FXCollections.observableArrayList();
    private ObservableList<String> serverFilesList = FXCollections.observableArrayList();
    private String serverDirectory;
    private String clientDirectory;
    private RandomAccessFile accessFile;
    private String fileName;
    private String  curDir;
    private boolean curDirIsServer;








    private String newDirPath;

    @FXML
        private void initialize() throws IOException {
            fxmlLoader.setLocation(getClass().getResource("/application/login.fxml"));
            fxmlReg = fxmlLoader.load();
            regController = fxmlLoader.getController();

            pathFxmlLoader.setLocation(getClass().getResource("/application/newPath.fxml"));
            fxmlPath = pathFxmlLoader.load();
            newPathController = pathFxmlLoader.getController();


            startClient();
            //завершаем работу приложения нажатием на крестик главного окна и закрываем канал и группу клиента
            Platform.runLater(() -> {
                    stage = (Stage) regButton.getScene().getWindow();

                //обработка нажатия крестика на окне
                stage.setOnCloseRequest(event -> { //нажали крестик
                    System.out.println("Goodbye!"); //попрощались
                    if (channel != null) { //Если канал открыт, то закрываем его
                        try {
                            channel.close();
                            client.getGroup().shutdownGracefully();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            });
        }

    public void tryEnter(ActionEvent actionEvent) throws IOException {
        showRegWindow();


    }

    private void showRegWindow() {
         if (regStage == null) {
             regStage = new Stage();

             regStage.setTitle("Вход/регистрация");
             regStage.setScene(new Scene(fxmlReg,320,200));
             regStage.initModality(Modality.WINDOW_MODAL);
             regStage.initOwner(mainStage);
         }
            regStage.show();
            regController.setController(this);
       }

    private void showNewPathWindow() {
        if (pathStage == null) {
            pathStage = new Stage();
            pathStage.setTitle("Введите название папки");
            pathStage.setScene(new Scene(fxmlPath,300,100));
            pathStage.initModality(Modality.WINDOW_MODAL);
            pathStage.initOwner(mainStage);
        }
        newPathController.setController(this);
        pathStage.show();

    }

    public void startClient () {

        client = new Client();
        client.start();
        channel = client.getChannel();
        client.setRegController(regController);
        client.setController(this);
        regController.setChannel(channel);
}
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
        }

    public Stage getMainStage() {
        return mainStage;
    }

    //считываем рабочую директорию клиента и названия всех файлов в ней для передачи в ListView
    public void clientDirView () {
        /*
        * используя System.getProperty нужно задавать ключ
        * "user.dir" - рабочая директория пользователя
        * "user.home" - домашняя директория пользователя
        * "java.home" - директория где установлена java
        * */
        clientTextField.clear();
        clientsFilesList.clear(); //список файлов для клиента
        clientListView.setItems(clientsFilesList);

        clientDirectory = System.getProperty("user.dir");
        clientTextField.appendText(clientDirectory); //директория для клиента

        //используем ObserveList для возможности передать данные листа в ListView
        clientsFilesList.addAll(listFiles(clientDirectory)); //список файлов для клиента

        clientListView.setItems(clientsFilesList);

    }

    public void serverDirView (String dir) {
        serverDirectory = dir;
        Platform.runLater(() -> {
            serverTextField.clear();
            serverFilesList.clear();
            serverListView.setItems(serverFilesList);
            serverTextField.appendText(serverDirectory);
            serverFilesList.addAll(listFiles(serverDirectory));
            serverListView.setItems(serverFilesList);
        });
    }

    //считываем только имена файлов из директории и записываем их в лист файлов
    public List<String> listFiles(String dir) {
            return Stream.of(new File(dir).listFiles())
//                    .filter(file -> !file.isDirectory())
                    .map(File::getName)
                    .collect(Collectors.toList());
    }


    //действие кнопки Обновить на клиенте
    public void clRenew(ActionEvent actionEvent) {

        clientDirView();
    }
    //действие кнопки Отправить на клиенте
    public void clSend(ActionEvent actionEvent) throws IOException {
        getSendFile();
    }

    public void getSendFile () throws IOException {
        //собираем путь до файла, который нужно передать
        fileName=(String)clientListView.getSelectionModel().getSelectedItem(); //получаем имя выделенного файла
        String fileDirectory = clientDirectory+ "\\" +fileName;
        System.out.println(fileDirectory);

        //создаём объект доступа к файлу

        if (accessFile == null) { //если объект доступа к файлу пустой
            final File file = new File(fileDirectory); //задаём файл с адресом
            accessFile = new RandomAccessFile(file, "r"); //создаём объект доступа к файлу в режиме чтения

            sendFile(); //вызываем метод отправки файла
        }

    }
    //действие кнопки Создать папку на клиенте
    public void clCreateDir(ActionEvent actionEvent) {
        curDir = clientDirectory;
        curDirIsServer = false;
        showNewPathWindow();
    }

    //действие кнопки Обновить на сервере
    public void srvRenew(ActionEvent actionEvent) {
        serverDirView(serverDirectory);
    }
    //действие кнопки Получить на сервере
    public void srvRcv(ActionEvent actionEvent) throws IOException {
        /*
        * выделяем файл в окне клиента и нажимаем Получить на сервере
        * копируем директорию файла
        * отправляем его на сервер*/
        getSendFile();
    }

    //действие кнопки Создать папку на сервере
    public void srvCreateDir(ActionEvent actionEvent) throws IOException {
        curDir = serverDirectory;
        curDirIsServer = true;
        showNewPathWindow();


    }

    public void makeNewDir (String curDir) throws IOException {
            newDirPath = newPathController.getNewDir();
            System.out.println(newDirPath);
            String newDir = curDir + "\\" + newDirPath;
            System.out.println(newDir);
            Files.createDirectory(Paths.get(newDir));
            if (curDirIsServer) {
                serverDirView(curDir);
            } else {
                clientDirView();
            }


    }

    //действие кнопки Удалить на сервере
    public void srvDelete(ActionEvent actionEvent) {
        FileDeleteMessage fileDeleteMessage = new FileDeleteMessage();
        String serverFileName=(String)serverListView.getSelectionModel().getSelectedItem();
        fileDeleteMessage.setFileName(serverFileName);
        channel.writeAndFlush(fileDeleteMessage);
    }

    public Button getRegButton() {
        return regButton;
    }
    private void sendFile() throws IOException {
        if (accessFile != null) { //если объект доступа к файлу не нулевой
            final byte[] fileContent; //создаём массив байт для передачи пакета
            final long available = accessFile.length()-accessFile.getFilePointer(); //вычисляем остаток байт для передачи
            if (available > 64 * 1024) {    //если остаток больше заданного размера пакета
                fileContent = new byte[64 * 1024]; //размер пакета будет равен заданному размеру
            } else {
                fileContent = new byte [(int)available]; //иначе размер пакета равен остатку байт
            }

            final FileContentMessage message = new FileContentMessage(); //создаём объект сообщения типа содержимого сообщения
            message.setFileName(fileName);
            message.setStartPosition(accessFile.getFilePointer()); //задаём место чтения в сообщении
            accessFile.read(fileContent); //читаем из файла в пакет заданного размера
            message.setContent(fileContent); //записываем считанный пакет в содержимое сообщения
            final boolean last = accessFile.getFilePointer()==accessFile.length();
            message.setLast(last); //устанавливаем флаг последнего байта если файл считан до длины файла

            channel.writeAndFlush(message) //ставим в очередь задачу передачи
                    .addListener(new ChannelFutureListener() { //добавляем листенер, который при окончании операции вызывает метод
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!last) {

                                long length = accessFile.length();
                                if (length>0) {
                                    Platform.runLater(() -> {
                                        sendProgress.setVisible(true);
                                        sendProgress.setText(message.getStartPosition() * 100 / length + "%");

                                    });
                                }
                                sendFile(); //если байт не последний, то снова вызываем метод отправки файла

                            }
                        }
                    });
            if (last) {
                serverDirView(serverDirectory);

                accessFile.close();
                accessFile = null;
                Platform.runLater(() -> {
                    sendProgress.setVisible(false);
                });
            }


        }
    }
    public void setNewDirPath(String newDirPath) {
        this.newDirPath = newDirPath;
    }
    public String getCurDir() {
        return curDir;
    }

    public void setCurDir(String curDir) {
        this.curDir = curDir;
    }


}


