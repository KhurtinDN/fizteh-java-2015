package ru.mipt.diht.students;

import javax.net.ssl.HttpsURLConnection;
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

    public static String translate(String lang, String input) throws IOException {
        tryingTimes++;

        if (input.equals("Dolgoprudnyy")) {
            return "Долгопрудный"; //костыль. yandex translated Doldoprudnyy as "Долгопрудном", что не ищется
        }

        String urlStr = "https://translate.yandex.net/api/v1.5/tr.json/translate?key="
                + "trnsl.1.1.20151014T174735Z.84c9a75c05df4e5e.cb5a00eacf7657d7969a3c2a0e1421712d67bdb5";
        urlStr += "&text=" + input + "&lang=" + lang;
        URL urlObj = new URL(urlStr);
        HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();

        InputStream response = connection.getInputStream();
        String json = new java.util.Scanner(response).nextLine();
        int start = json.indexOf("[");
        int end = json.indexOf("]");
        String translated;
        if (lang == "en-ru") {
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
