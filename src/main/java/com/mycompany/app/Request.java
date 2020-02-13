package com.mycompany.app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.*;
import java.net.URL;


public class Request {
    public static void main(String[] args) throws IOException {
        String url = "https://www.kinopoisk.ru/api/suggest/?query=john";
//        System.out.println("Введите запрос");
//        Scanner h= new Scanner(System.in);
//        String name = h.nextLine();
//        h.close();


        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestProperty("x-requested-with", "XMLHttpRequest");
        connection.setRequestProperty("authority", "www.kinopoisk.ru");
        connection.setRequestProperty("accept", "application/json");
        connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
        connection.setRequestProperty("content-type", "application/json");
        connection.setRequestProperty("sec-fetch-site", "same-origin");
        connection.setRequestProperty("sec-fetch-mode", "cors");
        //connection.setRequestProperty("accept-encoding", "gzip, deflate, br");
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

        System.out.println(response.toString());

    }
}
