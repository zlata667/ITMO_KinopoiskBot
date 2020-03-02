package com.mycompany.app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mysql.fabric.jdbc.FabricMySQLDriver;

import javax.management.Query;
import javax.xml.crypto.Data;

class Request {
    static Map<String, List<FilmOrPerson>> filmOrPersonResultMap = new HashMap<>();

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
        System.out.println(result);

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
        return printResult(filmOrPersonResultMap.get(chatId), chatId)
                + "\n\nДля того, чтобы подписаться на получение фильмов, нажмите subscribe рядом с выбранной личностью.";
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

    static Film searchFilmography(String personId) throws IOException {

        String url = "https://www.kinopoisk.ru/api/person-filmography/?id=" + personId; //на вход id актера
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connectionSettings(connection);
        connection.setRequestProperty("referer", "https://www.kinopoisk.ru/name/" + personId + "/"); // тут в заголовке используется id актера

        StringBuilder response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Gson gson = new Gson();
            Film film = gson.fromJson(response.toString(), Film.class);
            return  film;

        } catch (Exception e){
            return null;
        }

    }

    static String generateRandomFilmFromFilmography(Film filmInf){

        List<FilmDetails> filmography = filmInf.getFilmography();
        if (filmography.isEmpty()) return null;

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

    static List<Info> searchFilmsFromGenre(String genreTitle, String type) throws IOException {

        String genreId = null;
        String genreEngTitle = null;

        Connection conn;
        try {

            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/wssmTKXCex", "wssmTKXCex", "WhxmR8YpfY");

            ResultSet resultSet;
            String query = "select * from Genres where rusTitle = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, genreTitle);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            genreId = resultSet.getString("id");
            genreEngTitle = resultSet.getString("engTitle");
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        String url = "https://www.kinopoisk.ru/lists/navigator/api/films/?exclude_viewed=0&genre="+genreId+"&page=1&quick_filters=" + type + "&sort=votes";
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connectionSettings(connection);
        connection.setRequestProperty("referer", "https://www.kinopoisk.ru/lists/navigator/"+genreEngTitle+"/?quick_filters=" + type);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Gson gson = new Gson();
        FGenre genres = gson.fromJson(response.toString(), FGenre.class);
        List<Info> inf = genres.getItemsInfo();//список фильмов в виде объектов Info



        return inf;

    }

    private static void addNames(List<String> namesList, String actionType, FilmOrPerson elem,
                                 String objectType, int index, String id){
        switch (objectType){
            case "person":
                namesList.add(namesList.size() - index, elem.getName() + " \uD83D\uDC64 "
                        + actionType + id);
                break;
            case "film":
                namesList.add(elem.getTitle() + " (" + elem.getYear() + ") \uD83C\uDFAC " + "https://www.kinopoisk.ru/" + elem.getUrl());
                break;
            case "tvSeries":
                namesList.add(elem.getTitle() + " (" + elem.getYearsRange() + ")" + " \uD83C\uDFAC ");
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
