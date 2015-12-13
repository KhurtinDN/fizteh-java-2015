package util;

import jcmdparser.Parser;
import org.junit.Test;
import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.Query;

import static junit.framework.Assert.assertEquals;

/**
 * Created by lokotochek on 13.12.15.
 */
public class QueryBuilderTest {

    @Test
    public void testFormFilterQuery() throws Exception {
        Parser jcp = new Parser();
        jcp.stream = true;
        jcp.query = "lol";
        jcp.hideRetweets = true;
        FilterQuery query = QueryBuilder.formFilterQuery(jcp);
        String correctQuery = "FilterQuery{count=0, follow=null, " +
                "track=[lol +exclude:retweets], locations=null, " +
                "language=null, filter_level=null}";
        assertEquals(correctQuery, query.toString());
        jcp.query = "";
        jcp.place = "Moscow";
        query = QueryBuilder.formFilterQuery(jcp);
        correctQuery = "FilterQuery{count=0, follow=null, track=null," +
                " locations=[[D@d70c109, [D@17ed40e0], " +
                "language=null, filter_level=null}";
        assertEquals(correctQuery, query.toString());
    }

    @Test
    public void testFormQuery() throws Exception {
        Parser jcp = new Parser();
        jcp.query = "hello mello";
        jcp.hideRetweets = true;
        jcp.limit = 15;
        jcp.place = "Moscow";

        Query query = QueryBuilder.formQuery(jcp);

        assertEquals("hello mello +exclude:retweets", query.getQuery());
        assertEquals(15, query.getCount());

        GeoLocation moscowLocation = LocationSearch.getGeoCenter("Moscow");
        String correctLocation = moscowLocation.getLatitude()
                + "," + moscowLocation.getLongitude() + "," + "50.0km";
        assertEquals(correctLocation, query.getGeocode());
    }
}