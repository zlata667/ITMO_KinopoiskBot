package com.mycompany.app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class Request {
    static List<FilmOrPerson> filmOrPersonList;

    static String search(String request) throws IOException {

        request = URLEncoder.encode(request);// кодируем на случай, если поиск на русском языке

        Gson gson = new Gson();
        String url = "https://www.kinopoisk.ru/api/suggest/?query=" + request;
        URL urlObj = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

        //headers
        connection.setRequestProperty("x-requested-with", "XMLHttpRequest");
        connection.setRequestProperty("authority", "www.kinopoisk.ru");
        connection.setRequestProperty("accept", "application/json");
        connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
        connection.setRequestProperty("content-type", "application/json");
        connection.setRequestProperty("sec-fetch-site", "same-origin");
        connection.setRequestProperty("sec-fetch-mode", "cors");
        connection.setRequestProperty("accept-language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
        connection.setRequestProperty("referer", "https://www.kinopoisk.ru/");

        connection.setRequestMethod("GET");

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

        Type type = new TypeToken<List<FilmOrPerson>>(){}.getType();

        filmOrPersonList = gson.fromJson(result, type);

        List<String> namesAndTitles = new ArrayList<>();

        int i = 0;
        for (FilmOrPerson obj : filmOrPersonList) {
            switch (obj.getType()) {
                case "person":
                    namesAndTitles.add(namesAndTitles.size() - i,
                            obj.getName() + " \uD83D\uDC64" + " /subscribe" + obj.getId());
                    break;
                case "film":
                    namesAndTitles.add(obj.getTitle() + " (" + obj.getYear() + ") \uD83C\uDFAC /subscribe" + obj.getId());
                    break;
                case "tvSeries":
                    namesAndTitles.add(obj.getTitle() + " (" + obj.getYearsRange() + ")" + " \uD83C\uDFAC" + " /subscribe" + obj.getId());
                    break;
            }
            i++;
        }



        String resultList = String.join("\n", namesAndTitles);
        resultList = resultList.replaceAll("&nbsp;"," ");
        resultList = resultList.replaceAll("&#38;","&");
        resultList = resultList.replaceAll("&ndash;","-");

        return resultList;
    }
}
