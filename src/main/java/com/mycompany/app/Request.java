package com.mycompany.app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class Request {

    static Map<String, List<FilmOrPerson>> filmOrPersonResultMap = new HashMap<>();

    static String search(String request, String chatId) throws IOException {

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

    private static String printResult(List<FilmOrPerson> resultList, String chatId){

        Map<String, List<String>> namesAndTitles = new HashMap<>();
        List<FilmOrPerson> subscribesFotChatId = Bot.subscribeList.get(chatId); // подписки конкретного пользователя;

        if (Bot.subscribeList.containsKey(chatId)){
            namesAndTitles.put(chatId, new ArrayList<>());
            for (int i = 0; i < resultList.size(); i++) {
                int count = 0;
                for (FilmOrPerson filmOrPerson : subscribesFotChatId) {
                    if (resultList.get(i).getId().equals(filmOrPerson.getId())) {
                        FilmOrPerson res = resultList.get(i);
                        addNames(namesAndTitles.get(chatId), "/unsubscribe", res, res.getType(), i);
                        count++;
                    }
                }
                if (count == 0){
                    FilmOrPerson res = resultList.get(i);
                    addNames(namesAndTitles.get(chatId), "/subscribe", res, res.getType(), i);
                }

            }
        } else{
            namesAndTitles.put(chatId, new ArrayList<>());
            for (int j = 0; j < resultList.size(); j++) {
                FilmOrPerson res = resultList.get(j);
                addNames(namesAndTitles.get(chatId), "/subscribe", res, res.getType(), j);
            }
        }
        return Bot.toNormalString(String.join("\n", namesAndTitles.get(chatId)));
    }

    private static void addNames(List<String> namesList, String actionType, FilmOrPerson elem,
                                 String objectType, int index){
        switch (objectType){
            case "person":
                namesList.add(namesList.size() - index, elem.getName() + " \uD83D\uDC64 "
                        + actionType + elem.getId());
                break;
            case "film":
                namesList.add(elem.getTitle() + " (" + elem.getYear() + ") \uD83C\uDFAC " + actionType + elem.getId());
                break;
            case "tvSeries":
                namesList.add(elem.getTitle() + " (" + elem.getYearsRange() + ")" + " \uD83C\uDFAC " + actionType + elem.getId());
                break;
        }
    }

}
