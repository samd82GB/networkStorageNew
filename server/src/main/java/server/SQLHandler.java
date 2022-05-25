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

    //получение id зарегистрированного пользователя
    public static String getIdByLoginAndPassword(String login, String password) {
        Connection connection = DatabaseConnection.getConnection();
        String id = "";
        try {
            PreparedStatement psGetId = connection.prepareStatement("SELECT id FROM users WHERE login = ? AND password = ?;");
            psGetId.setString(1, login);      //записываем в запрос логин
            psGetId.setString(2, password);   //записываем в запрос пароль
            ResultSet rs = psGetId.executeQuery();         //запрос на получение id
            if (rs.next()) {
                id = rs.getString(1);           //id возвращается в первой колонке результсета
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DatabaseConnection.close(connection);
        }
        return id;                                //возвращаем id из базы данных зарегистрированных пользователей
    }


}



