package ru.netology.domain.data;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import ru.netology.mode.User;

import java.sql.DriverManager;

public class DataHelper {

    private static final String vasyaPassEncrypted = "$2a$10$CNrT4S.Oo27u9x/iujHEvuqwsMTeYAn7xa5H0qAq/dhe1P1Yttr7m";
    private static final String petyaPassEncrypted = "$2a$10$QqnfLHmNwAKFTpaesao90ud169TVhznCueMzmObXFgkoZXQIPCcSe";
    private static final String vasyaCardNumber1 = "5559 0000 0000 0001";
    private static final String vasyaCardNumber2 = "5559 0000 0000 0002";
    private static final String pass = "qwerty123";

    private DataHelper() {
    }

    public static User getAuthInfo() {
        return requestUser();
    }

    @SneakyThrows
    public static User getAuthInfo(String findUser) {
        var runner = new QueryRunner();
        var sqlRequestUser = "SELECT * FROM users WHERE login = ?;";

        try (var conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/app", "app", "pass")) {
            return runner.query(conn, sqlRequestUser, findUser, new BeanHandler<>(User.class));
        }
    }

    public static String getVerificationCodeFor(User authInfo) {
        return requestCode(authInfo);
    }

    //запрос пользователя из БД
    @SneakyThrows
    private static User requestUser() {
        var runner = new QueryRunner();
        var sqlRequestUser = "SELECT * FROM users WHERE login = ?;";
        String vasyaId = "vasya";

        try (var conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/app", "app", "pass")) {
            return runner.query(conn, sqlRequestUser, vasyaId, new BeanHandler<>(User.class));
        }
    }

    //запрос кода верификации из БД
    @SneakyThrows
    private static String requestCode(User user) {
        //ожидание создания кода верификации
        Thread.sleep(500);
        var runner = new QueryRunner();
        var sqlRequestSortByTime = "SELECT code FROM auth_codes WHERE user_id = ? ORDER BY created DESC";

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );

        ) {
            return runner.query(conn, sqlRequestSortByTime, user.getId(), new ScalarHandler<>());
        }
    }

    //вычистка данных за SUT
    @SneakyThrows
    public static void clearSUTData() {
        var runner = new QueryRunner();
        var sqlDeleteAllAuthCodes = "DELETE FROM auth_codes;";
        var sqlDeleteAllCards = "DELETE FROM cards;";
        var sqlDeleteAllUsers = "DELETE FROM users;";

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );
        ) {
            runner.update(conn, sqlDeleteAllAuthCodes);
            runner.update(conn, sqlDeleteAllCards);
            runner.update(conn, sqlDeleteAllUsers);
        }
    }

    //сброс тестовых данных всех таблиц к исходным значениям
    @SneakyThrows
    public static void resetSUTData() {
        var runner = new QueryRunner();
        var sqlInsertUsers = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";
        var sqlInsertCards = "INSERT INTO cards(id, user_id, number, balance_in_kopecks) VALUES (?, ?, ?, ?);";

        String vasyaId = getId();
        String vasyaLogin = "vasya";
        String vasyaPass = vasyaPassEncrypted;
        String vasyaCard1Id = getId();
        String CardNumber1 = vasyaCardNumber1;
        String vasyaCard2Id = getId();
        String CardNumber2 = vasyaCardNumber2;
        String petyaId = getId();
        String petyaLogin = "petya";
        String petyaPass = petyaPassEncrypted;

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );

        ) {
            runner.update(conn, sqlInsertUsers, vasyaId, vasyaLogin, vasyaPass);
            runner.update(conn, sqlInsertUsers, petyaId, petyaLogin, petyaPass);

            runner.update(conn, sqlInsertCards, vasyaCard1Id, vasyaId, CardNumber1, "1000000");
            runner.update(conn, sqlInsertCards, vasyaCard2Id, vasyaId, CardNumber2, "1000000");
        }
    }

    //создание Faker пользователя
    @SneakyThrows
    public static User CreateUser() {
        var runner = new QueryRunner();
        var sqlAddUser = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";
        var sqlSelectUser = "SELECT * FROM users WHERE id = ?;";
        String userId = getId();
        String userLogin = getLogin();

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );

        ) {
            runner.update(conn, sqlAddUser,
                    userId,
                    userLogin,
                    //Пароль qwerty123:
                    vasyaPassEncrypted);
            return runner.query(conn, sqlSelectUser, userId,  new BeanHandler<>(User.class));
        }
    }

    private static String getId() {
        return new Faker().internet().uuid();
    }

    private static String getLogin() {
        return new Faker().name().firstName();
    }

    public static String getValidPass() {
        return pass;
    }

    public static String getRandPass() {
        return new Faker().internet().password();
    }
}