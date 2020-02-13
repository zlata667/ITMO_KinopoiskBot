package com.mycompany.app;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class Bot extends TelegramLongPollingBot {

    private static final String BOT_NAME = "ITMO_FirstBot";
    private static final String BOT_TOKEN = "983197607:AAF2W3iyYyoFOSh8zm4BvDALGtmHyz7C8H4";


    String result = null;
    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        try {
            result = Request.search(message);// организовать чтение json
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMsg(update.getMessage().getChatId().toString(), result);
    }

    public synchronized void sendMsg(String chatId, String s) {

        SendMessage sendMes = new SendMessage();
        sendMes.enableMarkdown(true);
        sendMes.setChatId(chatId);
        sendMes.setText(s);
        try {
            execute(sendMes);
        } catch (TelegramApiException e) {

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
