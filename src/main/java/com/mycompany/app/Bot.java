package com.mycompany.app;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.*;

public class Bot extends TelegramLongPollingBot {

    private static final String BOT_NAME = "Kinopoisk_Bot";
    private static final String BOT_TOKEN = "983197607:AAF2W3iyYyoFOSh8zm4BvDALGtmHyz7C8H4";

    private String result = null;
    private Map<String, List<String>> subscribeList = new HashMap<String, List<String>>();
    private List<String> subscribeNames = new ArrayList<>();

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()){
            String message = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if (message.contains("/subscribe")){
                String id = message.replace("/subscribe", "");
                check(chatId, id);

            }else {
                try {
                    result = Request.search(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendMsg(chatId, String.format("Результаты поиска:\n\n%s", result));
            }

        } else if (update.hasCallbackQuery()){
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            String data = update.getCallbackQuery().getData();
            if (data.equals("no")){
                sendMsg(chatId, "Окей");
            } else{
                subscribe(chatId, data);
            }
        }
    }

    private synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMes = new SendMessage();
        sendMes.enableMarkdown(true);
        sendMes.setChatId(chatId);
        sendMes.setText(s);
        try {
            execute(sendMes);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void confirm(String chatId, String id) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        keyboardButtonsRow.add(new InlineKeyboardButton().setText("Да").setCallbackData(id));
        keyboardButtonsRow.add(new InlineKeyboardButton().setText("Нет").setCallbackData("no"));

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId).setText("Вы хотите подписаться на " + getName(id) + "?")
                .setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void check(String chatId, String id){
        if (subscribeList.containsKey(chatId)) {
            if (subscribeList.get(chatId).toString().contains(id)) {
                sendMsg(chatId, "Вы уже подписаны на " + getName(id));
            } else {
                confirm(chatId, id);//передаем id для подтверждения
            }

        } else {
            subscribeList.put(chatId, new ArrayList<>());
            confirm(chatId, id);//передаем id для подтверждения
        }
    }

    private void subscribe(String chatId, String id) {

        subscribeList.get(chatId).add(id);
        subscribeNames.add(getName(id));
        sendMsg(chatId, "Вы подписались на " + getName(id));
        sendMsg(chatId, "Ваши подписки: " + subscribeNames);
    }

    private String getName(String id){

        for (FilmOrPerson filmOrPerson : Request.filmOrPersonList){
            if (filmOrPerson.getId().equals(id)){
                if ("person".equals(filmOrPerson.getType())) {
                    return filmOrPerson.getName()
                            .replaceAll("&#38;", "&")
                            .replaceAll("&ndash;", "-")
                            .replaceAll("&nbsp;", " ");
                }
                return filmOrPerson.getTitle()
                        .replaceAll("&#38;", "&")
                        .replaceAll("&ndash;", "-")
                        .replaceAll("&nbsp;", " ");
            }
        }
        return null;
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
