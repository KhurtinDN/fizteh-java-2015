package util;

import jcmdparser.Parser;
import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.Query;

import static java.lang.System.exit;

public class QueryBuilder {

    public static FilterQuery formFilterQuery (Parser jcp) throws APIException {
        FilterQuery filterQuery = new FilterQuery();
        if (!"".equals(jcp.query) && !"any".equals(jcp.place)) {
            String msg = ("Twitter API doesn't support streaming with both location and query");
            throw new APIException(msg);
        }
        if (!"".equals(jcp.query)) {
            String queryString = jcp.query;
            queryString += jcp.hideRetweets ? " +exclude:retweets" : "";
            String[] jcpQueryArray = {queryString};
            filterQuery.track(jcpQueryArray);
        }
        if (!"any".equals(jcp.place)) {
            filterQuery.locations(LocationSearch.getGeoBox(jcp.place));
        }
        return filterQuery;
    }

    public static Query formQuery (Parser jcp) throws APIException {
        String queryString = jcp.query;
        queryString += jcp.hideRetweets ? " +exclude:retweets" : "";
        Query query = new Query(queryString);
        query.setCount(jcp.limit);
        if (!"any".equals(jcp.place)) {
            GeoLocation myLocation = LocationSearch.getGeoCenter(jcp.place);
            query.setGeoCode(myLocation, 50, Query.Unit.km);
            // пришлось захардкодить радиус, потому что слишком сложно
            // получить его из бокса (градус широты переводится в километры
            // в зависимости от того, насколько место близко к экватору)
            // 1 градус == от 0 до 101 км ;(
        }
        return query;
    }

    
}
