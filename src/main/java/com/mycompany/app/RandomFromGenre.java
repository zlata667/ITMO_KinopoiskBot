package com.mycompany.app;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

class RandomFromGenre {

    private static InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

    private static List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

    static void printGenres(String chatId){
        List<String> genres = new ArrayList<>();
        rowList.clear();
        setGenres(genres);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        for (int i = 0; i < genres.size(); i++) {
            String genre = genres.get(i);
            if (i%3==0){
                rowList.add(keyboardButtonsRow);
                keyboardButtonsRow = new ArrayList<>();
            }
            keyboardButtonsRow.add(new InlineKeyboardButton().setText(genre).setCallbackData(genre.toLowerCase()));
        }

        inlineKeyboardMarkup.setKeyboard(rowList);


        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId).setText("Выберите жанр:")
                .setReplyMarkup(inlineKeyboardMarkup);
        try {
            App.bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    static String generateRandomFilmFromGenre(List<Info> filmList){

        Random random = new Random();
        int i = random.nextInt(filmList.size());
        Object film = filmList.get(i).getCoreData();
        Map<String, String> filmDetails = (Map<String, String>) film;
        String title = filmDetails.get("title");
        String originalTitle = filmDetails.get("originalTitle");
        String imageUrl = filmDetails.get("posterLink");

        Map<String, Double> number = (Map<String, Double>)film;
        int year = number.get("year").intValue();

        Map<String,Map<String, Map<String,Double>>> ratings = (Map<String,Map<String, Map<String,Double>>>)film;
        Double rating = ratings.get("ratings").get("rating").get("value");
        Map<String,List<Map<String,String>>> g = (Map<String,List<Map<String,String>>>) film;
        List<String> genres = new ArrayList<String>();

        for (int j = 0; j < g.get("genres").size(); j++){
            genres.add(g.get("genres").get(j).get("name"));
        }

        String result;
        if (originalTitle == null){
            result = title + " (" + year
                    + ")\nЖанр: " + genres.toString()
                    + "\nРейтинг кинопоиска: " + rating + "\n" + imageUrl;
        } else {
            result = title + " (" + originalTitle + ", " + year
                    + ")\nЖанр: " + genres.toString()
                    + "\nРейтинг кинопоиска: " + rating + "\n" + imageUrl;
        }

        return result;

    }

    private static void setGenres(List<String> genres) {
        //надо занести это в бд
        genres.add("Комедия");
        genres.add("Мелодрама");
        genres.add("Игра");
        genres.add("Мультфильм");
        genres.add("Фантастика");
        genres.add("Приключения");
        genres.add("Детектив");
        genres.add("Семейный");
        genres.add("Ток-шоу");
        genres.add("Фэнтези");
        genres.add("Драма");
        genres.add("Музыка");
        genres.add("Короткометражка");
        genres.add("Криминал");
        genres.add("Аниме");
        genres.add("Военный");
        genres.add("Боевик");
        genres.add("Биография");
        genres.add("История");
        genres.add("Документальный");
        genres.add("Триллер");
        genres.add("Вестерн");
        genres.add("Ужасы");
        genres.add("Мюзикл");
        genres.add("Детский");
        genres.add("Концерт");
        genres.add("Новости");
        genres.add("Спорт");
        genres.add("Фильм-нуар");
        genres.add("Церемония");
    }
}
