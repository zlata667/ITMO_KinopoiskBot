package com.mycompany.app;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.sql.*;
import java.util.*;


public class Bot extends TelegramLongPollingBot {

    private static final String BOT_NAME = "Kinopoisk_Bot";
    private static final String BOT_TOKEN = "983197607:AAF2W3iyYyoFOSh8zm4BvDALGtmHyz7C8H4";
//    private static final String BOT_NAME = "TestBot";
 //   private static final String BOT_TOKEN = "1066474013:AAHJeh_0KbJrc3aoym_sDPDmDKWzsUfDOiU";

    private String result = null;
    static Map<String, List<FilmOrPerson>> subscribeList = new HashMap<>();
    private Map<String, List<String>> subscribeNames = new HashMap<>();

    private InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    private List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
    private List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

    Bot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()){
            String message = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if (message.startsWith("\"")){
                try {
                    result = Request.searchPersonOrFilm(message, chatId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendMsg(chatId, String.format("Результаты поиска:\n\n%s", result));
                return;
            }
            if (message.contains("/start")){
                sendMsg(chatId, "Привет!\n Для того, чтобы начать пользоваться ботом, введи имя актера или фильма.");
                return;
            }
            if (message.contains("/info")){
                sendMsg(chatId, "Это KinopoiskBot. \n" +
                        "Для вас он может найти человека или фильм, у которых есть страница на сайте kinopoisk.ru. Для того, чтобы начать поиск, просто напишите ему имя актера или название фильма.\n" +
                        "Если вы хотите получить случайный фильм или сериал по выбранному жанру, напишите ему /random.\n" +
                        "Также вы можете подписаться на любимого актера или режиссера и периодически получать фильмы с его участием в сообщения.");
                return;
            }
            if (message.contains("/random")){
                RandomFromGenre.printGenres(chatId);
                return;
            }

            if (message.contains("/subscribe")){
                String id = message.replace("/subscribe", "");
                try {
                    checkSubscribe(chatId, id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            if (message.contains("/unsubscribe")){
                String id = message.replace("/unsubscribe", "");
                checkUnsubscribe(chatId, id);
            } else {
                try {
                    result = Request.searchPersonOrFilm(message, chatId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendMsg(chatId, "Результаты поиска:\n\n" + result);
            }

            return;
        }
        if (update.hasCallbackQuery()){
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            int mesId = update.getCallbackQuery().getMessage().getMessageId();

            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId).setMessageId(mesId);

            String data = update.getCallbackQuery().getData();
            if (data.equals("no")){
                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                return;
            }
            if (data.contains("sub")){
                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                try {
                    subscribe(chatId, data.replace("sub", ""));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            if (data.contains("un")) {
                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                unsubscribe(chatId, data.replace("un", ""));
            } else{
                try {
                    String film = RandomFromGenre.generateRandomFilmFromGenre(Request.searchFilmsFromGenre(data,"films"));
                    sendMsg(chatId, "Фильм жанра " + data + ":\n" + toNormalString(film));

                    String series = RandomFromGenre.generateRandomFilmFromGenre(Request.searchFilmsFromGenre(data,"serials"));
                    sendMsg(chatId, "Сериал жанра " + data + ":\n" + toNormalString(series));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMes = new SendMessage();
        sendMes.enableMarkdown(true);
        sendMes.enableWebPagePreview();
        sendMes.setChatId(chatId);
        sendMes.setText(s + "\n/info");
        try {
            execute(sendMes);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void confirmSubscribe(String chatId, String id) throws IOException {

        keyboardButtonsRow.clear();
        rowList.clear();

        keyboardButtonsRow.add(new InlineKeyboardButton().setText("Да").setCallbackData(id + "sub"));
        keyboardButtonsRow.add(new InlineKeyboardButton().setText("Нет").setCallbackData("no"));

        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId).setText("Вы хотите подписаться на получение фильмов с " + getName(id) + "?")
                .setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();

        }

    }

    private void confirmUnsubscribe(String chatId, String id) {

        keyboardButtonsRow.clear();
        rowList.clear();

        keyboardButtonsRow.add(new InlineKeyboardButton().setText("Да").setCallbackData(id + "un"));
        keyboardButtonsRow.add(new InlineKeyboardButton().setText("Нет").setCallbackData("no"));

        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId).setText("Вы хотите отписаться от " + getName(id) + "?")
                .setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void checkSubscribe(String chatId, String id) throws IOException {

        if (Request.searchFilmography(id) == null){
            sendMsg(chatId,
                    "Вы ввели несуществующий id. Попробуйте воспользоваться поиском" +
                            " - введите имя актера или название фильма.");
        }
        if (Request.searchFilmography(id).getFilmography().isEmpty()){
            sendMsg(chatId, "К сожалению, фильмография " + getName(id)
                    + " на кинопоиске в данный момент пуста.");
            return;
        }

        try {
            Connection conn = connectDB();

            String sql = "select * from Subscribes where exists(select * from Subscribes where chatId = ? and personId = ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(chatId));
            preparedStatement.setInt(2, Integer.parseInt(id));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                sendMsg(chatId, "Вы уже подписаны на " + getName(id));
            } else confirmSubscribe(chatId, id);
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void checkUnsubscribe(String chatId, String id){
        if (getName(id) == null){
            sendMsg(chatId, "Не найдено");
            return;
        }

        try {
            Connection conn = connectDB();

            String sql = "select * from Subscribes where exists(select * from Subscribes where chatId = ? and personId = ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(chatId));
            preparedStatement.setInt(2, Integer.parseInt(id));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                confirmUnsubscribe(chatId, id);
            } else sendMsg(chatId, "Вы не подписаны на " + getName(id));

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private void subscribe(String chatId, String id) throws IOException {

        try {
            Connection conn = connectDB();

            String sql = "insert into Subscribes values (null, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(chatId));
            preparedStatement.setInt(2, Integer.parseInt(id));
            preparedStatement.setString(3, getName(id));
            preparedStatement.executeUpdate();

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        sendMsg(chatId, "Вы подписались на получение фильмов с участием " + getName(id));
        sendMsg(chatId, "Ваши подписки: " + getListOfNamesFromSubscribes(chatId, id));
    }

    private void unsubscribe(String chatId, String id){

        try {
            Connection conn = connectDB();

            String sql = "delete from Subscribes where chatId = ? and personId = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(chatId));
            preparedStatement.setInt(2, Integer.parseInt(id));
            preparedStatement.executeUpdate();

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        sendMsg(chatId, "Вы отписались от "+ getName(id));
        sendMsg(chatId, "Ваши подписки: " + getListOfNamesFromSubscribes(chatId, id));

    }

    private String getName(String id) {
        try {
            return Request.searchFilmography(id).getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String toNormalString(String str){
        str = str.replaceAll("&nbsp;"," ").replaceAll("&#38;","&")
                .replaceAll("&ndash;","-").replaceAll("&#237;", "i")
                .replaceAll("&eacute;", "e");
        return str;
    }

    private static Connection connectDB() throws SQLException {
        Connection conn;
        Driver driver = new com.mysql.cj.jdbc.Driver();
        DriverManager.registerDriver(driver);
        conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/wssmTKXCex", "wssmTKXCex", "WhxmR8YpfY");

        return conn;
    }

    private static String getListOfNamesFromSubscribes(String chatId, String id){
        List<String> names = new ArrayList<>();
        try {
            Connection conn = connectDB();
            String sql = "select personName from Subscribes where chatId = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(chatId));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                names.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return names.toString();
    }
}
