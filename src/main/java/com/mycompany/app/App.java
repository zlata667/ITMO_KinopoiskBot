package com.mycompany.app;

import org.apache.log4j.BasicConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
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
            options.setProxyHost("195.201.15.253");
            options.setProxyPort(1080);
            //195.201.15.253:1080
            //178.170.168.212:1080
            //Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
            options.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
            bot = new Bot(options);
            telegram.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

        Timer time = new Timer();
        ScheduledTask task = new ScheduledTask();
        time.schedule(task, 0, 2 * 60000);

    }


}
