package ru.mipt.diht.students.andreyzharkov.twitterStream;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by Андрей on 13.12.2015.
 */
public class TranslatorTest extends TestCase {
    @Test
    public final void testEnRu() {
        try {
            assertThat("bad translation", Translator.translate("en-ru", "ball").equals("мяч"));
            assertThat("bad translation", Translator.translate("en-ru", "snow").equals("снег"));
            assertThat("bad translation", Translator.translate("en-ru", "brain").equals("мозг"));
            assertThat("bad translation", Translator.translate("en-ru", "Lobnya").equals("Лобня"));
            assertThat("bad translation", Translator.translate("en-ru", "Vladivostok").equals("Владивосток"));
        } catch(IOException ex){
            System.out.println("It seems like you haven't yandex API key");
        }
    }
/*
    @Test
    public final void testRuEn() throws IOException{
        assertThat("bad 1translation"+Translator.translate("ru-en", "мяч"),
                Translator.translate("ru-en", "мяч").equals("the ball"));
        assertThat("bad 2translation", Translator.translate("ru-en", "снег").equals("snow"));
        assertThat("bad 3translation", Translator.translate("ru-en", "мозг").equals("the brain"));
        assertThat("bad 4translation", Translator.translate("ru-en", "Лобня").equals("Lobnya"));
        assertThat("bad 5translation", Translator.translate("ru-en", "Владивосток").equals("Vladivostok"));
    }*/
}
