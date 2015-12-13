package ru.mipt.diht.students.andreyzharkov.twitterStream;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Андрей on 14.10.2015.
 */
public class Translator {
    //нормально переводит только с английского на русский, но не наоборот
    //ибо какая-то проблема с кодировкой
    private static int tryingTimes = 0;
    private static String yandexAPIkey = null;

    public static String translate(String lang, String input) throws IOException {
        tryingTimes++;
        if (yandexAPIkey == null) {
            BufferedReader reader = new BufferedReader(new FileReader("key.txt"));
            yandexAPIkey = reader.readLine();
        }

        if (input.equals("Dolgoprudnyy")) {
            return "Долгопрудный"; //костыль. yandex translated Doldoprudnyy as "Долгопрудном", что не ищется
        }

        String urlStr = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=";
        urlStr += yandexAPIkey + "&text=" + input + "&lang=" + lang;
        URL urlObj = new URL(urlStr);
        HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();

        try (InputStream response = connection.getInputStream()) {
            String json = new java.util.Scanner(response).nextLine();
            int start = json.indexOf("[");
            int end = json.indexOf("]");
            String translated;
            if (lang.equals("en-ru")) {
                String translatedIncorrect = json.substring(start + 2, end - 1);
                byte[] b = translatedIncorrect.getBytes();
                translated = new String(b, "UTF-8");
                //System.out.println(translated);
            } else {
                translated = json.substring(start + 2, end - 1);
            }

            if (translated.equals(input)) {
                if (tryingTimes > 2) {
                    tryingTimes = 0;
                    return input;
                }
                return translate("en-ru", input); //метод из программы вызывается с lang="ru-en"
            }
            tryingTimes = 0;
            return translated;
        }
    }
}
