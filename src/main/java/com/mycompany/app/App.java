package com.mycompany.app;

import org.apache.log4j.BasicConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.*;

public class App{

    public static void main( String[] args )
    {
//
//        BasicConfigurator.configure();
//        ApiContextInitializer.init();
//        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
//        try {
//            telegramBotsApi.registerBot(new Bot());
//        } catch (TelegramApiRequestException e) {
//            e.printStackTrace();
//        }

        try {
            BasicConfigurator.configure();
            ApiContextInitializer.init();
            TelegramBotsApi telegram = new TelegramBotsApi();

            DefaultBotOptions options = ApiContext.getInstance(DefaultBotOptions.class);
            options.setProxyHost("51.158.104.249");
            options.setProxyPort(1080);
            //51.158.104.249:1080
            //49.12.0.103:10510
            //Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
            options.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

            telegram.registerBot(new Bot(options));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

}
