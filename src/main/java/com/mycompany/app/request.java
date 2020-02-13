package com.mycompany.app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.*;
import java.net.URL;
import java.util.Scanner;

public class request {
    public static void main(String[] args) throws IOException {
        String url = "https://www.kinopoisk.ru/api/suggest/?query=john";
//        System.out.println("Введите запрос");
//        Scanner h= new Scanner(System.in);
//        String name = h.nextLine();
//        h.close();
        URL obj = null;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestProperty("x-requested-with", "XMLHttpRequest");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());
        //System.out.println("dyy");
       /* Document doc = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new ByteArrayInputStream(response.toString().getBytes()));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        String title = doc.getDocumentElement()
                .getElementsByTagName("title").item(0).getTextContent();

        System.out.println("title = " + title);
        System.out.println("dyy");*/
    }
}
