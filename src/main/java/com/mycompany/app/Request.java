package com.mycompany.app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class Request {

    static Map<String, List<FilmOrPerson>> filmOrPersonResultMap = new HashMap<>();

    static String searchFilmography(String chatId, String personId) throws IOException {

        String url = "https://www.kinopoisk.ru/api/person-filmography/?id=" + personId; //на вход id актера

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connectionSettings(connection);
        connection.setRequestProperty("referer", "https://www.kinopoisk.ru/name/" + personId + "/"); // тут в заголовке используется id актера

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Gson gson = new Gson();
        Film film = gson.fromJson(response.toString(), Film.class);
        List<FilmDetails> filmDetails = film.getFilmography();

        if (filmDetails.isEmpty()) return null;

        return generateRandomFilm(chatId, filmDetails);//возвращает список фильмов с деталями
    }

    static String generateRandomFilm(String chatId, List<FilmDetails> filmography){

        Random random = new Random();
        int i = random.nextInt(filmography.size());
        FilmDetails film = filmography.get(i);
        int filmId = film.getId();
        String filmTitle = film.getTitle();
        String originalTitle = film.getOriginalTitle();
        String filmYear = film.getYear();
        String genre = film.getFirstGenre();
        String filmUrl = "www.kinopoisk.ru/film/" + filmId + "/";
        List<String> filmCountries = film.getCountries();

        Map<String, String> ratingKP = ((Map<String, String>) film.getRatings().get("kp"));
        String ratingValue = ratingKP.get("value");
        if (ratingValue == null){
            ratingValue = "Отсутствует";
        }
        String result;
        if (originalTitle == null){
            result = filmTitle + " (" + filmYear
                    + ")\nЖанр: " + genre
                    + "\nСтрана: " + filmCountries.toString()
                    + "\nРейтинг кинопоиска: " + ratingValue + "\n" + filmUrl;
        } else {
            result = filmTitle + " (" + originalTitle + ", " + filmYear
                    + ")\nЖанр: " + genre
                    + "\nСтрана: " + filmCountries.toString()
                    + "\nРейтинг кинопоиска: " + ratingValue + "\n" + filmUrl;
        }

        return result;
    }

    static String searchPersonOrFilm(String request, String chatId) throws IOException {

        request = URLEncoder.encode(request);// кодируем на случай, если поиск на русском языке

        Gson gson = new Gson();
        String url = "https://www.kinopoisk.ru/api/suggest/?query=" + request;
        URL urlObj = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

        connectionSettings(connection);
        connection.setRequestProperty("referer", "https://www.kinopoisk.ru/");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String result = response.toString();

        if (result.length() == 2) {
            return "Нет результатов";
        }

        List<FilmOrPerson> filmOrPersonList = gson.fromJson(result, new TypeToken<List<FilmOrPerson>>(){}.getType());

        if (filmOrPersonResultMap.containsKey(chatId)){
            filmOrPersonResultMap.get(chatId).clear();
            filmOrPersonResultMap.get(chatId).addAll(filmOrPersonList);
        } else{
            filmOrPersonResultMap.put(chatId, new ArrayList<>());
            filmOrPersonResultMap.get(chatId).addAll(filmOrPersonList);
        }
        return printResult(filmOrPersonResultMap.get(chatId), chatId);
    }

    private static String printResult(List<FilmOrPerson> resultList, String chatId) throws IOException {

        Map<String, List<String>> namesAndTitles = new HashMap<>();
        List<FilmOrPerson> subscribesFotChatId = Bot.subscribeList.get(chatId); // подписки конкретного пользователя;

        if (Bot.subscribeList.containsKey(chatId)){
            namesAndTitles.put(chatId, new ArrayList<>());
            for (int i = 0; i < resultList.size(); i++) {
                int count = 0;
                for (FilmOrPerson filmOrPerson : subscribesFotChatId) {

                    if (resultList.get(i).getId().equals(filmOrPerson.getId())) {
                        FilmOrPerson res = resultList.get(i);
                        addNames(namesAndTitles.get(chatId), "/unsubscribe", res, res.getType(), i, res.getId());
                        count++;
                    }
                }
                if (count == 0){
                    FilmOrPerson res = resultList.get(i);
                    addNames(namesAndTitles.get(chatId), "/subscribe", res, res.getType(), i, res.getId());
                }

            }
        } else{
            namesAndTitles.put(chatId, new ArrayList<>());
            for (int j = 0; j < resultList.size(); j++) {
                FilmOrPerson res = resultList.get(j);
                addNames(namesAndTitles.get(chatId), "/subscribe", res, res.getType(), j, res.getId());
            }
        }
        return Bot.toNormalString(String.join("\n", namesAndTitles.get(chatId)));
    }

    private static void addNames(List<String> namesList, String actionType, FilmOrPerson elem,
                                 String objectType, int index, String id){
        switch (objectType){
            case "person":
                namesList.add(namesList.size() - index, elem.getName() + " \uD83D\uDC64 "
                        + actionType + id);
                break;
            case "film":
                namesList.add(elem.getTitle() + " (" + elem.getYear() + ") \uD83C\uDFAC " + actionType + id);
                break;
            case "tvSeries":
                namesList.add(elem.getTitle() + " (" + elem.getYearsRange() + ")" + " \uD83C\uDFAC " + actionType + id);
                break;
        }
    }

    private static void connectionSettings(HttpURLConnection connection) throws ProtocolException {
        connection.setRequestProperty("x-requested-with", "XMLHttpRequest");
        connection.setRequestProperty("authority", "www.kinopoisk.ru");
        connection.setRequestProperty("accept", "application/json");
        connection.setRequestProperty("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
        connection.setRequestProperty("content-type", "application/json");
        connection.setRequestProperty("sec-fetch-site", "same-origin");
        connection.setRequestProperty("sec-fetch-mode", "cors");
        connection.setRequestProperty("accept-language", "ru-RU");

        connection.setRequestMethod("GET");
    }

}
