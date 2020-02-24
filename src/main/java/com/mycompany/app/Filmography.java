package com.mycompany.app;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;

class Filmography {
//    static String searchFilms(String chatId, String personId) throws IOException {
//
//        String url = "https://www.kinopoisk.ru/api/person-filmography/?id=" + personId; //на вход id актера
//
//        URL obj = new URL(url);
//        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
//
//        connection.setRequestProperty("x-requested-with", "XMLHttpRequest");
//        connection.setRequestProperty("authority", "www.kinopoisk.ru");
//        connection.setRequestProperty("accept", "application/json");
//        connection.setRequestProperty("user-agent",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
//        connection.setRequestProperty("content-type", "application/json");
//        connection.setRequestProperty("sec-fetch-site", "same-origin");
//        connection.setRequestProperty("sec-fetch-mode", "cors");
//        connection.setRequestProperty("accept-language", "ru-RU");
//        connection.setRequestProperty("referer", "https://www.kinopoisk.ru/name/"+personId+"/"); // тут в заголовке используется id актера
//
//        connection.setRequestMethod("GET");
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        String inputLine;
//        StringBuilder response = new StringBuilder();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//
//        System.out.println(response.toString());
//        Gson gson = new Gson();
//        Film film = gson.fromJson(response.toString(), Film.class);
//        List<FilmDetails> Details = film.getFilmography();// попадают и те фильмы+сериалы, где и грал, и те, что срежисированы
//
//        Integer size = Details.size();
//        Random random = new Random();//рандомное число
//        Integer i = random.nextInt(size);
//        Integer id = Details.get(i).getId();
//        Integer idT = Details.get(i).getTrailerId();
//        String title = Details.get(i).getTitle();
//        String year = Details.get(i).getYear();
//        String role = Details.get(i).getContextData().get("role"); // тут или актер, или режиссер, или...
//        String roleD = Details.get(i).getContextData().get("roleDetails"); // имя персонажа или пояснение от кинопоиска
//        Object ratclass = Details.get(i).getRatings().get("kp");
//        Map<String,String> rat = (Map<String, String>) ratclass;
//        String ratings = rat.get("value");//рейтинг фильма
//
//        System.out.println(title + year);
//
//
//        return title;
//
//
//    }
}
