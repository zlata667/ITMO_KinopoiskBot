package com.mycompany.app;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GenreFilm {
    public static void main(String[] args) throws IOException {
        String url = "https://widgets.kinopoisk.ru/discovery/api/trailers?params=";
        System.out.println("Введите запрос");
        Scanner h = new Scanner(System.in);
        String name = h.nextLine();
        h.close();


        URL obj = new URL(url+name);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestProperty("x-requested-with", "XMLHttpRequest");
        connection.setRequestProperty("host", "www.kinopoisk.ru");
        connection.setRequestProperty("accept", "application/json");
        connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362");
        connection.setRequestProperty("content-type", "application/json");
        //connection.setRequestProperty("accept-encoding", "gzip, deflate, br");
        connection.setRequestProperty("accept-language", "ru-RU");
        connection.setRequestProperty("origin", "https://www.kinopoisk.ru");
        connection.setRequestProperty("referer", "https://www.kinopoisk.ru/film/"+name+"/");
        //connection.setRequestProperty("Cookie", "white_email_status=1:1:1584030270095; user-geo-region-id=2; user-geo-country-id=2; desktop_session_key=372e7d3ee830092f34b4070218096e2ca465ad0f350bb71a378cff9bb14413225d61d73c50de13aa7c70d05da3176c3afdc6c94fe3cc36fc3fd7f17658d06c4046beef4bb1493dba71d714567ff805b724dccdfc7ea62b6d0fc15f5c9657963e; desktop_session_key.sig=jacWYM5Ql7cYxJ6dawqu6fLegQc; _csrf_csrf_token=Z1XtHiv0fM1jL9L1Zk1ThglOBSn0li_l9o0Flsygtis; ymex=1584707519.oyu.3485097841531769285; vote_data_cookie=b8c3e8b7662c6727fa5db5e86782eacb; cycada=yTfDaYdppoJ/86ZXirUnbU8UPl0fxMkpljG3rW3A/Js=; user_country=ru; sso_status=sso.passport.yandex.ru:synchronized; _ym_visorc_52332406=b; yp=1584029409.oyu.3485097841531769285#1582201919.yu.3485097841531769285; tc=2; _ym_isad=2; ya_sess_id=3:1582115518.5.0.1532961562850:I180VA:4f.1|79754822.47592249.2.2:47592249|543174419.8465091.2.2:8465091|30:187434.489666.Wgk-g-i1xBXZ6_K7JxyIZg3C23c; _ym_d=1582124140; yandexuid=3485097841531769285; my_perpages=%5B%5D; mda=0; _ym_visorc_22663942=b; mda_exp_enabled=1; mykp_button=inbox; mobile=no; fuid01=5b4db0c25629e872.m6BL-MegarO4ukzRGdXX6UHvE0qRbHXTg8MfQKVCw8Ive1P_e46VyB_PebyJgoT1Rv-5nxUqPApkQ4l2_y_HYnz3ce1GcyFMdM81n1CZfvySF6ho98ZHJ2TqrcovRvwW; _ym_visorc_56177992=b; _ym_uid=15812540651041062841; i=4eVFt7BWv4WWWiBpti/oc9dEU2VH9yHKpqpB3NNAA5l1csmrlsw7hRR+cKht1iEsTYKjAJ0afgnAksYb9L5JbK1oX3E=; _ym_wasSynced=%7B%22time%22%3A1582115872945%2C%22params%22%3A%7B%22eu%22%3A0%7D%2C%22bkParams%22%3A%7B%7D%7D; yandex_login=larionovags; mda2_beacon=1582115518829; PHPSESSID=ungpo2gorjdvq64pdnna8bt8n4; yandex_gid=2; uid=15502969");

        /*Cache-Control: max-age=0
        Connection: Keep-Alive*/

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
        Object son = gson.fromJson(response.toString(), Object.class);
        Map<String, Map<String,Map<String,List<String>>>> id = (Map<String, Map<String,Map<String,List<String>>>>) son;
        List<String> film = id.get(name).get("film").get("genres");

    }
}
