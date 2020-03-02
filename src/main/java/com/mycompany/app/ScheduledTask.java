package com.mycompany.app;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.util.*;

public class ScheduledTask extends TimerTask {
    @Override
    public void run() {
        if (!Bot.subscribeList.isEmpty()) {
            for (Map.Entry<String, List<FilmOrPerson>> entry : Bot.subscribeList.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    String chatId = entry.getKey();
                    sendMessage(chatId, "Фильмы по вашим подпискам:");
                    for (FilmOrPerson filmOrPerson : entry.getValue()) {
                        try {
                            String text = Request.generateRandomFilmFromFilmography(
                                    Objects.requireNonNull(Request.searchFilmography(filmOrPerson.getId())));
                            sendMessage(chatId, filmOrPerson.getName() + ":\n\n " + text);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
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
