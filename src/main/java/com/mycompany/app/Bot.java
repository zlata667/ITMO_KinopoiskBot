package com.mycompany.app;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class Bot extends TelegramLongPollingBot {

    private static final String BOT_NAME = "Kinopoisk_Bot";
    private static final String BOT_TOKEN = "983197607:AAF2W3iyYyoFOSh8zm4BvDALGtmHyz7C8H4";

    private String result = null;

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        try {
            result = Request.search(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMsg(update.getMessage().getChatId().toString(), "Результаты поиска:\n\n" + result);
    }

    private synchronized void sendMsg(String chatId, String s) {

        SendMessage sendMes = new SendMessage();
        sendMes.enableMarkdown(true);
        sendMes.setChatId(chatId);
        sendMes.setText(s);
        try {
            execute(sendMes);
        } catch (TelegramApiException ignored) {

        }
    }


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
