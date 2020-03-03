package com.mycompany.app;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ScheduledTask extends TimerTask {
    @Override
    public void run() {
        try {
            Connection conn = Bot.connectDB();
            String sql = "select * from Subscribes";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String chatId = String.valueOf(resultSet.getInt("chatId"));
                sendMessage(chatId, "Фильм с участием " + resultSet.getString("personName"));
                String text = Request.generateRandomFilmFromFilmography(
                        Objects.requireNonNull(Request.searchFilmography(String.valueOf(resultSet
                                .getInt("personId")))));
                sendMessage(chatId, text);
            }
            conn.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String chatId, String message){

        SendMessage sendMes = new SendMessage();
        sendMes.enableMarkdown(true);
        sendMes.enableWebPagePreview();
        sendMes.setChatId(chatId);
        try {
            sendMes.setText(message);
            App.bot.execute(sendMes);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



}
