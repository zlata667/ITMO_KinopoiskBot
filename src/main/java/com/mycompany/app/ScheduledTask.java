package com.mycompany.app;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import sun.net.www.http.HttpClient;
import sun.plugin2.message.Message;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;


public class ScheduledTask extends TimerTask {

    @Override
    public void run() {
        if (!Bot.subscribeList.isEmpty()) {
            for (Map.Entry<String, List<FilmOrPerson>> entry : Bot.subscribeList.entrySet()) {
                if (entry != null) {
                    String chatId = entry.getKey();
                    sendMessage(chatId, "Фильмы по вашим подпискам:");
                    for (FilmOrPerson filmOrPerson : entry.getValue()) {
                        if (filmOrPerson.getType().equals("person")){//если подписка на человека, то отправляем фильмографию
                            SendMessage sendMes = new SendMessage();
                            sendMes.enableMarkdown(true);
                            sendMes.enableWebPagePreview();
                            try {
                                String text = Request.searchFilmography(chatId, filmOrPerson.getId());
                                sendMessage(chatId, filmOrPerson.getName() + ":\n\n " + text);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }// реализовать если подписка на фильм
                    }
                }
            }
        }
    }

    private void sendMessage(String chatId, String message){
        //создаем новый экземпляр, т.к. метод execute не статик
        DefaultBotOptions options = ApiContext.getInstance(DefaultBotOptions.class);
        options.setProxyHost("51.158.104.249");
        options.setProxyPort(1080);
        //51.158.104.249:1080
        //49.12.0.103:10510
        //Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
        options.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
        Bot bot = new Bot(options);

        SendMessage sendMes = new SendMessage();
        sendMes.enableMarkdown(true);
        sendMes.enableWebPagePreview();
        sendMes.setChatId(chatId);
        try {
            sendMes.setText(message);
            bot.execute(sendMes);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



}
