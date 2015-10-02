package util;

import jcmdparser.Parser;
import twitter4j.FilterQuery;
import twitter4j.Query;

import static java.lang.System.exit;

public class QueryBuilder {

    public static FilterQuery formFilterQuery (Parser jcp) {
        FilterQuery filterQuery = new FilterQuery();
        if (!"".equals(jcp.query) && !"any".equals(jcp.place)) {
            System.out.println("Twitter API doesn't support streaming with both location and query");
            exit(1);
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

    public static Query formQuery (Parser jcp) {
        String queryString = jcp.query;
        queryString += jcp.hideRetweets ? " +exclude:retweets" : "";
        Query query = new Query(queryString);
        query.setCount(jcp.limit);
        if (!"any".equals(jcp.place)) {
            query.setGeoCode(LocationSearch.getGeoCenter(jcp.place), 50, Query.Unit.km);
            // пришлось захардкодить радиус, потому что слишком сложно
            // получить его из бокса (градус широты переводится в километры
            // в зависимости от того, насколько место близко к экватору)
            // 1 градус == от 0 до 101 км :(
        }

        return query;
    }

    
}
