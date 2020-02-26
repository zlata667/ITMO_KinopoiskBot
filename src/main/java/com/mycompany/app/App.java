package com.mycompany.app;

import org.apache.log4j.BasicConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import sun.plugin2.message.Message;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class App{

    static Bot bot;

    public static void main( String[] args ) {
        try {
            BasicConfigurator.configure();
            ApiContextInitializer.init();
            TelegramBotsApi telegram = new TelegramBotsApi();

            DefaultBotOptions options = ApiContext.getInstance(DefaultBotOptions.class);
            options.setProxyHost("185.151.245.84");
            options.setProxyPort(1080);
            //47.254.155.40:9000 ---
            //185.151.245.84:1080
            //188.191.33.34:39880 ---
            //51.158.104.249:1080 ---
            //49.12.0.103:10510 ---
            //213.136.89.190:53054
            //Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
            options.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
            bot = new Bot(options);
            telegram.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

        Timer time = new Timer();
        ScheduledTask task = new ScheduledTask();
        time.schedule(task, 0, 3 * 60000);

    }


}
