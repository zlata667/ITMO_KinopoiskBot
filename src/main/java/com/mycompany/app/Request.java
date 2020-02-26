package com.mycompany.app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

    static Film searchFilmography(String personId) throws IOException {

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
        List<FilmDetails> filmography = film.getFilmography();

        if (filmography.isEmpty()) return null;

        return  film;
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

    private static void addNames(List<String> namesList, String actionType, FilmOrPerson elem,
                                 String objectType, int index, String id){
        switch (objectType){
            case "person":
                namesList.add(namesList.size() - index, elem.getName() + " \uD83D\uDC64 "
                        + actionType + id);
                break;
            case "film":
                namesList.add(elem.getTitle() + " (" + elem.getYear() + ") \uD83C\uDFAC ");
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


//    static String searchGenre(String chatId, String filmId) throws IOException {
//        String url = "https://widgets.kinopoisk.ru/discovery/api/trailers?params=" + filmId;
//
//        URL obj = new URL(url);
//        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
//        connectionSettings(connection);
//        connection.setRequestProperty("referer", "https://www.kinopoisk.ru/film/"+ filmId +"/");
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
//        Gson gson = new Gson();
//        Object son = gson.fromJson(response.toString(), Object.class);
//        Map<String, Map<String,Map<String,List<String>>>> id = (Map<String, Map<String,Map<String,List<String>>>>) son;
//        List<String> filmGenres = id.get(filmId).get("film").get("genres");
//
//        return filmGenres.get(0);//получаем жанр фильма
//    }
//
//
//    static List<Info> searchFilmsFromGenre(String genreTitle, String type) throws IOException {
//
//        String genreId = null;
//        String genreEngTitle = null;
//        getGenreIdAndEngTitle(genreTitle, genreId, genreEngTitle);
//
//        String url = "https://www.kinopoisk.ru/lists/navigator/api/films/?exclude_viewed=0&genre="+genreId+"&page=1&quick_filters=" + type + "&sort=votes";
//        URL obj = new URL(url);
//        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
//        connectionSettings(connection);
//        connection.setRequestProperty("referer", "https://www.kinopoisk.ru/lists/navigator/"+genreEngTitle+"/?quick_filters=" + type);
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
//        Gson gson = new Gson();
//        FGenre genres = gson.fromJson(response.toString(), FGenre.class);
//        List<Info> inf = genres.getItemsInfo();//список фильмов в виде объектов Info
//
//        return inf;
//
//    }
//
//    static String generateRandomFilmFromGenre(String chatId, List<Info> filmList){
//
//        Random random = new Random();
//        int i = random.nextInt(filmList.size());
//        Object film = filmList.get(i).getCoreData();
//        Map<String, String> filmDetails = (Map<String, String>) film;
//        String title = filmDetails.get("title");
//        String originalTitle = filmDetails.get("originalTitle");
//        String imageUrl = filmDetails.get("posterLink");
//
//        Map<String, Double> number = (Map<String, Double>)film;//для чисел. они почему все в этом типе оказались
//        int year = number.get("year").intValue();
//
//        Map<String,Map<String, Map<String,String>>> ratings = (Map<String,Map<String, Map<String,String>>>)film;
//        String rating = ratings.get("ratings").get("rating").get("value");
//        Map<String,List<Map<String,String>>> g = (Map<String,List<Map<String,String>>>) film;
//        List<String> genres = new ArrayList<String>();
//
//        for (int j = 0; j < g.get("genres").size(); j++){
//            genres.add(g.get("genres").get(j).get("name"));
//        }
//
//        String result;
//        if (originalTitle == null){
//            result = title + " (" + year
//                    + ")\nЖанр: " + genres.toString()
//                    + "\nРейтинг кинопоиска: " + rating + "\n" + imageUrl;
//        } else {
//            result = title + " (" + originalTitle + ", " + year
//                    + ")\nЖанр: " + genres.toString()
//                    + "\nРейтинг кинопоиска: " + rating + "\n" + imageUrl;
//        }
//
//        return result;
//    }


    private static void getGenreIdAndEngTitle(String genreTitle, String genreId, String genreEngTitle){

        switch (genreTitle) {//перенести в бд
            case "комедия":
                genreId = "6";
                genreEngTitle = "comedy";
                break;
            case "мелодрама":
                genreId = "7";
                genreEngTitle = "romance";
                break;
            case "игра":
                genreId = "27";
                genreEngTitle = "game-show";
                break;
            case "мультфильм":
                genreId = "14";
                genreEngTitle = "animation";
                break;
            case "фантастика":
                genreId = "2";
                genreEngTitle = "sci-fi";
                break;
            case "приключения":
                genreId = "10";
                genreEngTitle = "adventure";
                break;
            case "детектив":
                genreId = "17";
                genreEngTitle = "mystery";
                break;
            case "семейный":
                genreId = "11";
                genreEngTitle = "family";
                break;
            case "ток-шоу":
                genreId = "26";
                genreEngTitle = "talk-show";
                break;
            case "фэнтези":
                genreId = "5";
                genreEngTitle = "fantasy";
                break;
            case "драма":
                genreId = "8";
                genreEngTitle = "drama";
                break;
            case "музыка":
                genreId = "21";
                genreEngTitle = "music";
                break;
            case "короткометражка":
                genreId = "15";
                genreEngTitle = "short";
                break;
            case "реальное ТВ":
                genreId = "25";
                genreEngTitle = "reality-tv";
                break;
            case "криминал":
                genreId = "16";
                genreEngTitle = "crime";
                break;
            case "аниме":
                genreId = "1750";
                genreEngTitle = "anime";
                break;
            case "военный":
                genreId = "19";
                genreEngTitle = "war";
                break;
            case "боевик":
                genreId = "3";
                genreEngTitle = "action";
                break;
            case "биография":
                genreId = "22";
                genreEngTitle = "biography";
                break;
            case "история":
                genreId = "23";
                genreEngTitle = "history";
                break;
            case "документальный":
                genreId = "12";
                genreEngTitle = "documentary";
                break;
            case "вестерн":
                genreId = "13";
                genreEngTitle = "western";
                break;
            case "триллер":
                genreId = "4";
                genreEngTitle = "thriller";
                break;
            case "ужасы":
                genreId = "1";
                genreEngTitle = "horror";
                break;
            case "мюзикл":
                genreId = "9";
                genreEngTitle = "musical";
                break;
            case "детский":
                genreId = "456";
                genreEngTitle = "children";
                break;
            case "концерт":
                genreId = "1747";
                genreEngTitle = "concert";
                break;
            case "новости":
                genreId = "28";
                genreEngTitle = "news";
                break;
            case "спорт":
                genreId = "24";
                genreEngTitle = "sport";
                break;
            case "фильм-нуар":
                genreId = "18";
                genreEngTitle = "film-noir";
                break;
            case "церемония":
                genreId = "1751";
                genreEngTitle = "ceremony";
                break;
        }

    }
}
