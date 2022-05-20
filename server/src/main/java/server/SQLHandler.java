package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLHandler {



    //регистрация нового пользователя
    public static boolean registration(String login, String password) {
        Connection connection = DatabaseConnection.getConnection();

        try {
            connection.setAutoCommit(false);

            PreparedStatement psRegistration = connection.prepareStatement("INSERT INTO users (login, password) VALUES (?, ?);");

            psRegistration.setString(1, login);     //записываем в запрос логин
            psRegistration.setString(2, password);  //записываем в запрос пароль

            psRegistration.executeUpdate();                     //выполняем запрос на обновление таблицы
            connection.commit();

            return true;                                        //при удачной регистрации возвращаем "1"

        } catch (SQLException e) {
            DatabaseConnection.rollback(connection);
            e.printStackTrace();
            return false;                                       //в случае получения исключения возвращаем "0"
        } finally {
            DatabaseConnection.close(connection);
        }

    }



}



