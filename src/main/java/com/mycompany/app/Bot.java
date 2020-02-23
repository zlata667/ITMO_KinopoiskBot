package com.mycompany.app;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.util.*;

public class Bot extends TelegramLongPollingBot {

    private static final String BOT_NAME = "TestBot";
    private static final String BOT_TOKEN = "1066474013:AAHJeh_0KbJrc3aoym_sDPDmDKWzsUfDOiU";

    private String result = null;
    static Map<String, List<FilmOrPerson>> subscribeList = new HashMap<>();
    private Map<String, List<String>> subscribeNames = new HashMap<>();

    private InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    private List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
    private List<List<InlineKeyboardButton>> rowList = new ArrayList<>();


    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()){
            String message = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if (message.contains("/start")){
                sendMsg(chatId, "Привет!\n Для того, чтобы начать пользоваться ботом, введи имя актера или название фильма.");
                return;
            }

            if (message.contains("/subscribe")){
                String id = message.replace("/subscribe", "");
                checkSubscribe(chatId, id);
                return;
            }
            if (message.contains("/unsubscribe")){
                String id = message.replace("/unsubscribe", "");
                checkUnsubscribe(chatId, id);
            } else {
                try {
                    result = Request.search(message, chatId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendMsg(chatId, String.format("Результаты поиска:\n\n%s", result));
            }

        } else if (update.hasCallbackQuery()){
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            int mesId = update.getCallbackQuery().getMessage().getMessageId();

            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId).setMessageId(mesId);
            try {
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            String data = update.getCallbackQuery().getData();

            if (data.equals("no")){
                sendMsg(chatId, "Окей");
            } else if (data.contains("sub")){
                subscribe(chatId, data.replace("sub", ""));
            } else {
                unsubscribe(chatId, data.replace("un", ""));
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

    private void confirmSubscribe(String chatId, String id) {

        keyboardButtonsRow.clear();
        rowList.clear();

        keyboardButtonsRow.add(new InlineKeyboardButton().setText("Да").setCallbackData(id + "sub"));
        keyboardButtonsRow.add(new InlineKeyboardButton().setText("Нет").setCallbackData("no"));

        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId).setText("Вы хотите подписаться на " + getName(id, chatId) + "?")
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
        sendMessage.setChatId(chatId).setText("Вы хотите отписаться от " + getName(id, chatId) + "?")
                .setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void checkSubscribe(String chatId, String id){
        if (subscribeList.containsKey(chatId)) {
            for (FilmOrPerson fpObject : subscribeList.get(chatId)){
                if (fpObject.getId().equals(id)){
                    sendMsg(chatId, "Вы уже подписаны на " + getName(id, chatId));
                    return;
                }
            }
            confirmSubscribe(chatId, id);//передаем id для подтверждения

        } else {
            subscribeList.put(chatId, new ArrayList<>());
            subscribeNames.put(chatId, new ArrayList<>());
            confirmSubscribe(chatId, id);//передаем id для подтверждения
        }
    }

    private void checkUnsubscribe(String chatId, String id){
        for (FilmOrPerson fpObject : subscribeList.get(chatId)){
            if (fpObject.getId().equals(id)){
                confirmUnsubscribe(chatId, id);
                return;
            }
        }
        sendMsg(chatId, "Вы не подписаны на " + getName(id, chatId));
    }

    private void subscribe(String chatId, String id) {
        FilmOrPerson newFilmOrPerson = new FilmOrPerson();
        subscribeList.get(chatId).add(newFilmOrPerson);
        newFilmOrPerson.setId(id);
        subscribeNames.get(chatId).add(getName(id, chatId));
        sendMsg(chatId, "Вы подписались на " + getName(id, chatId));
        sendMsg(chatId, "Ваши подписки: " + subscribeNames.get(chatId).toString());

    }

    private void unsubscribe(String chatId, String id){
        List<FilmOrPerson> subscribesForChatId = subscribeList.get(chatId);
        for (FilmOrPerson sub : subscribesForChatId){
            if (sub.getId().equals(id)){
                subscribesForChatId.remove(sub);
                String name = getName(id, chatId);
                subscribeNames.get(chatId).remove(name);
                sendMsg(chatId, "Вы отписались от "+ getName(id, chatId));
                if (!subscribeNames.get(chatId).isEmpty()){
                    sendMsg(chatId, "Ваши подписки: " + subscribeNames.get(chatId).toString());
                }
            }
        }
    }

    private String getName(String id, String chatId){
        for (FilmOrPerson filmOrPerson : Request.filmOrPersonResultMap.get(chatId)){
            if (filmOrPerson.getId().equals(id)){
                if ("person".equals(filmOrPerson.getType())) {
                    return toNormalString(filmOrPerson.getName());
                }
                return toNormalString(filmOrPerson.getTitle());
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

    static String toNormalString(String str){
        str = str.replaceAll("&nbsp;"," ").replaceAll("&#38;","&")
                .replaceAll("&ndash;","-").replaceAll("&#237;", "i");
        return str;
    }
}
