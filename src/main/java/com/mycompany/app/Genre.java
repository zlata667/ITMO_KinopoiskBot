package com.mycompany.app;

import com.google.gson.Gson;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

class FGenre{
    public List<Info> getItemsInfo() {
        return itemsInfo;
    }

    private List<Info> itemsInfo;
}
class Info{
    public Map<String, Object> getCoreData() {
        return coreData;
    }

    private Map<String, Object> coreData;
}

public class Genre {
    private static String Input(){
        Scanner h = new Scanner(System.in);
        String genre = h.nextLine();
        h.close();
        return genre;
    }
    public static void main(String[] args) throws IOException {

        System.out.println("Введите жанр");
        String genre = Input();//сюда передается имя жанра
        System.out.println("Введите фильм или сериал");
        String filter = "с";//сюда фильм (ф) или сериал (с). с вводом из консоли были траблы, поэтому я этот параметр ручками меняю
        Integer idg;
        idg = 0;
        String ref = "";
        switch (genre){//в запросе участвует id жанра и его англ версия, которые из других запросов не вытащить, поэтому все лапками
            case "комедия":
                idg = 6;
                ref = "comedy";
                break;
            case "мелодрама":
                idg = 7;
                ref = "romance";
                break;
            case "игра":
                idg = 27;
                ref = "game-show";
                break;
            case "мультфильм":
                idg = 14;
                ref = "animation";
                break;
            case "фантастика":
                idg = 2;
                ref = "sci-fi";
                break;
            case "приключения":
                idg = 10;
                ref = "adventure";
                break;
            case "детектив":
                idg = 17;
                ref = "mystery";
                break;
            case "семейный":
                idg = 11;
                ref = "family";
                break;
            case "ток-шоу":
                idg = 26;
                ref = "talk-show";
                break;
            case "фэнтези":
                idg = 5;
                ref = "fantasy";
                break;
            case "драма":
                idg = 8;
                ref = "drama";
                break;
            case "музыка":
                idg = 21;
                ref = "music";
                break;
            case "короткометражка":
                idg = 15;
                ref = "short";
                break;
            case "реальное ТВ":
                idg = 25;
                ref = "reality-tv";
                break;
            case "криминал":
                idg = 16;
                ref = "crime";
                break;
            case "аниме":
                idg = 1750;
                ref = "anime";
                break;
            case "военный":
                idg = 19;
                ref = "war";
                break;
            case "боевик":
                idg = 3;
                ref = "action";
                break;
            case "биография":
                idg = 22;
                ref = "biography";
                break;
            case "история":
                idg = 23;
                ref = "history";
                break;
            case "документальный":
                idg = 12;
                ref = "documentary";
                break;
            case "вестерн":
                idg = 13;
                ref = "western";
                break;
            case "триллер":
                idg = 4;
                ref = "thriller";
                break;
            case "ужасы":
                idg = 1;
                ref = "horror";
                break;
            case "мюзикл":
                idg = 9;
                ref = "musical";
                break;
            case "детский":
                idg = 456;
                ref = "children";
                break;
            case "концерт":
                idg = 1747;
                ref = "concert";
                break;
            case "новости":
                idg = 28;
                ref = "news";
                break;
            case "спорт":
                idg = 24;
                ref = "sport";
                break;
            case "фильм-нуар":
                idg = 18;
                ref = "film-noir";
                break;
            case "церемония":
                idg = 1751;
                ref = "ceremony";
                break;
        }
        String f="";
        switch (filter){//то же самое но только фильтр поиска
            case "ф":
                f = "films";
                break;
            case "с":
                f = "serials";
                break;}

        String url = "https://www.kinopoisk.ru/lists/navigator/api/films/?exclude_viewed=0&genre="+idg+"&page=1&quick_filters="+f+"&sort=votes";
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestProperty("x-requested-with", "XMLHttpRequest");
        connection.setRequestProperty("host", "www.kinopoisk.ru");
        connection.setRequestProperty("accept", "application/json");
        connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362");
        connection.setRequestProperty("content-type", "application/json");
        //connection.setRequestProperty("accept-encoding", "gzip, deflate, br");
        connection.setRequestProperty("accept-language", "ru-RU");
        connection.setRequestProperty("referer", "https://www.kinopoisk.ru/lists/navigator/"+ref+"/?quick_filters="+f);//Внимание на структуру заголовка
        //connection.setRequestProperty("Cookie", "white_email_status=1:1:1584030270095; user-geo-region-id=2; user-geo-country-id=2; desktop_session_key=372e7d3ee830092f34b4070218096e2ca465ad0f350bb71a378cff9bb14413225d61d73c50de13aa7c70d05da3176c3afdc6c94fe3cc36fc3fd7f17658d06c4046beef4bb1493dba71d714567ff805b724dccdfc7ea62b6d0fc15f5c9657963e; desktop_session_key.sig=jacWYM5Ql7cYxJ6dawqu6fLegQc; _csrf_csrf_token=Z1XtHiv0fM1jL9L1Zk1ThglOBSn0li_l9o0Flsygtis; ymex=1584707519.oyu.3485097841531769285; vote_data_cookie=b8c3e8b7662c6727fa5db5e86782eacb; cycada=yTfDaYdppoJ/86ZXirUnbU8UPl0fxMkpljG3rW3A/Js=; user_country=ru; sso_status=sso.passport.yandex.ru:synchronized; _ym_visorc_52332406=b; yp=1584029409.oyu.3485097841531769285#1582201919.yu.3485097841531769285; tc=2; _ym_isad=2; ya_sess_id=3:1582115518.5.0.1532961562850:I180VA:4f.1|79754822.47592249.2.2:47592249|543174419.8465091.2.2:8465091|30:187434.489666.Wgk-g-i1xBXZ6_K7JxyIZg3C23c; _ym_d=1582124140; yandexuid=3485097841531769285; my_perpages=%5B%5D; mda=0; _ym_visorc_22663942=b; mda_exp_enabled=1; mykp_button=inbox; mobile=no; fuid01=5b4db0c25629e872.m6BL-MegarO4ukzRGdXX6UHvE0qRbHXTg8MfQKVCw8Ive1P_e46VyB_PebyJgoT1Rv-5nxUqPApkQ4l2_y_HYnz3ce1GcyFMdM81n1CZfvySF6ho98ZHJ2TqrcovRvwW; _ym_visorc_56177992=b; _ym_uid=15812540651041062841; i=4eVFt7BWv4WWWiBpti/oc9dEU2VH9yHKpqpB3NNAA5l1csmrlsw7hRR+cKht1iEsTYKjAJ0afgnAksYb9L5JbK1oX3E=; _ym_wasSynced=%7B%22time%22%3A1582115872945%2C%22params%22%3A%7B%22eu%22%3A0%7D%2C%22bkParams%22%3A%7B%7D%7D; yandex_login=larionovags; mda2_beacon=1582115518829; PHPSESSID=ungpo2gorjdvq64pdnna8bt8n4; yandex_gid=2; uid=15502969");


        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());
        Gson gson = new Gson();
        FGenre genres = gson.fromJson(response.toString(), FGenre.class);
        List<Info> inf = genres.getItemsInfo();//неадекватное получение данных, так как все мои оптимизационные варианты не сработали
        Integer size = inf.size();
        Random random = new Random();
        Integer rand = random.nextInt(size);//рандом
        Object Cdatas = inf.get(rand).getCoreData();//получение конкретного фильма
        Map<String, String> details = (Map<String, String>) Cdatas; //да тупо, но иначе он ругается у меня. Для строковых параметров
        String title = details.get("title");
        String originalTitle = details.get("originalTitle");
        String poster = details.get("posterLink");//постер
        String serialYear = details.get("serialYear");//для сериала
        Map<String, Double> number = (Map<String, Double>)Cdatas;//для чисел. они почему все в этом типе оказались
        Integer id = number.get("id").intValue();//добиваюсь адекватного числа
        Integer year = number.get("year").intValue();//для фильма, но есть и у сериалов
        Map<String,Map<String, Map<String,Double>>> ratings = (Map<String,Map<String, Map<String,Double>>>)Cdatas;
        Double r = ratings.get("ratings").get("rating").get("value");//получение рейтинга кинопоиска. могу вообще, и другие получить
        Map<String,List<Map<String,String>>> g = (Map<String,List<Map<String,String>>>) Cdatas;
        List<String> gen = new ArrayList<String>();
        for (int i = 0; i < g.get("genres").size(); i++){
            gen.add(g.get("genres").get(i).get("name"));}//получение жанров


    }
}
