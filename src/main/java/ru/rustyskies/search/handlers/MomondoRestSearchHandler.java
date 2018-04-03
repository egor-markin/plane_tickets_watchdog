package ru.rustyskies.search.handlers;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import ru.rustyskies.model.Flight;
import ru.rustyskies.model.SearchConf;
import ru.rustyskies.model.SearchResult;
import ru.rustyskies.model.Trip;
import ru.rustyskies.utils.DateUtils;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Egor Markin
 * @since 21.02.2018
 */
@Log4j
public class MomondoRestSearchHandler extends SearchHandler {

    private static final String MOMONDO_URL = MomondoUrlGenerator.BASE_URL + "api/3.0/FlightSearch";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public MomondoRestSearchHandler(SearchConf searchTask) {
        super(searchTask);
    }

    @Override
    protected SearchResult search(SearchConf searchConf) {
        List<Trip> trips = new ArrayList<>();

        // http://www.json.org/javadoc/org/json/JSONObject.html
        JSONObject json = new JSONObject();
        try {
            json.put("AdultCount", 1);
            json.put("ChildAges", new JSONArray());
            json.put("TicketClass", "ECO");

            JSONArray segments = new JSONArray();

            JSONObject segment1 = new JSONObject();
            segment1.put("Origin", searchConf.srcAirport.code);
            segment1.put("Destination", searchConf.destAirport.code);
            segment1.put("Depart", dateTimeFormat.format(searchConf.toDate));
            segment1.put("Departure", dateFormat.format(searchConf.toDate));
            segments.put(segment1);

            if (searchConf.backDate != null) {
                JSONObject segment2 = new JSONObject();
                segment2.put("Origin", searchConf.destAirport.code);
                segment2.put("Destination", searchConf.srcAirport.code);
                segment2.put("Depart", dateTimeFormat.format(searchConf.backDate));
                segment2.put("Departure", dateFormat.format(searchConf.backDate));
                segments.put(segment2);
            }
            json.put("Segments", segments);

            json.put("Culture", "en-US");

            if (searchConf.backDate != null) {
                json.put("Mix", "Segments");
            } else {
                json.put("Mix", "None");
            }
            json.put("Market", "PE");
            json.put("DirectOnly", false);
            json.put("IncludeNearby", true);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Date startPoint = DateUtils.getCurrentDate();

        HttpPost httpPost = new HttpPost(MOMONDO_URL);
        httpPost.setEntity(new StringEntity(json.toString(), ContentType.APPLICATION_JSON));
//		httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:36.0) Gecko/20100101 Firefox/36.0");

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .build();

        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(createCookie("ak_bmsc", "0767959AE16D93CC8878570CF3B99799D9D4FC751F100000D917875A4E712955~plr0s7QZgQ1XetSTqXfPreewNvTGHOwbzO9sAdVQcsCVCY3f7A24JoXS33vo4WbxTgxtcjjnysyMIgJfBuATsMAR0g+9+DnDcfVCOkdixR2RQTN2aLdeX4WKwyNnxu7f5TyrfXhAAt8tU34/AW3p4n7VdkfYp3ufWPVe/kzSDIIjILTiBsTg6zkp+QMS2/uCCOulvYy1SxBnPQLvcOt/6wr5kTpGG1KMl3rviVXc/2IUBGTcwx3KlTigiNda8qCVZZ"));

        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

        // Executing POST request
        try (CloseableHttpResponse postResponse = httpclient.execute(httpPost, localContext)) {
//			logger.info("POST Headers:");
//			Header[] headers = postResponse.getAllHeaders();
//			for (Header h : headers) {
//				logger.info("  " + h.getName() + ": " + h.getValue());
//			}
//			logger.info("POST Status Line: " + postResponse.getStatusLine());
//			logger.info("POST Status Code: " + postResponse.getStatusLine().getStatusCode());

            if (postResponse.getStatusLine().getStatusCode() != 200) {
                log.error("POST response status line is " + postResponse.getStatusLine() + " for " + searchConf);
                return null;
            }

            HttpEntity postEntity = postResponse.getEntity();
            String postJsonResponse = IOUtils.toString(postEntity.getContent(), "UTF-8");
//			logger.info("postJsonResponse: " + postJsonResponse);
            EntityUtils.consume(postEntity);

            if (postEntity.getContent() == null || postJsonResponse == null || postJsonResponse.trim().equals("") || "null".equals(postJsonResponse.trim())) {
                log.error("POST response is empty for " + searchConf);
                return null;
            }

            // Analyzing POST json object
            JSONObject postJsonResponseObject;
            try {
                postJsonResponseObject = new JSONObject(new JSONTokener(postJsonResponse));
            } catch (JSONException e1) {
                log.error("Unable to parse POST response: \"" + postJsonResponse + "\" for " + searchConf, e1);
                return null;
            }
//			logger.info("postJsonResponseObject: " + postJsonResponseObject.toString(2));

            String searchId = (String) postJsonResponseObject.get("SearchId");
            Integer engineId = (Integer) postJsonResponseObject.get("EngineId");

//			logger.info("searchId: " + searchId);
//			logger.info("engineId: " + engineId);

            // Executing GET request
            HttpGet httpGet = new HttpGet(MOMONDO_URL + "/" + searchId + "/" + engineId + "/true?includeSponsoredResults=false");

//			logger.info("");
//			logger.info(">>> GET requests execution loop >>>");
//			logger.info("");

            MomondoJsonObjectsParser jsonObjectsParser = new MomondoJsonObjectsParser();

            boolean done = false;
            boolean error = false;
            while (!done && !error) {
                // Making a delay before each iteration
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                CloseableHttpResponse getResponse = httpclient.execute(httpGet, localContext);
                HttpEntity getEntity = getResponse.getEntity();

                // log.info("getEntity.Encoding: " + getEntity.getContentEncoding());
                // log.info("getEntity.ContentType: " + getEntity.getContentType());
                // log.info("getEntity.ContentLength: " + getEntity.getContentLength());

                String getJsonResponse = IOUtils.toString(getEntity.getContent(), "UTF-8");
                // log.info("getJsonResponse: " + getJsonResponse);

                // Analyzing GET json object
                JSONObject getJsonResponseObject = new JSONObject(new JSONTokener(getJsonResponse));
                // log.info("getJsonResponseObject: " + getJsonResponseObject.toString(2));

                done = getJsonResponseObject.getBoolean("Done");
//				log.info("Done: " + done);

                error = getJsonResponseObject.getBoolean("Error");
//				log.info("Error: " + error);

                if (error) {
                    log.error("ErrorMessage: " + getJsonResponseObject.getString("ErrorMessage"));
                    continue;
                }

//				int resultNumber = getJsonResponseObject.getInt("ResultNumber");
//				logger.info("ResultNumber: " + resultNumber);

                jsonObjectsParser.applyData(getJsonResponseObject);
                //jsonObjectsParser.printStatistics();

                EntityUtils.consume(getEntity);

//				logger.info("");
            }

            trips.addAll(jsonObjectsParser.getTrips());
        } catch (JSONException | IOException e1) {
            throw new RuntimeException(e1);
        }

        long searchTime = DateUtils.getDateDifference(startPoint);

        // Setting some general parameters
        for (Trip t : trips) {
            t.flight1 = new Flight(searchConf.srcAirport, searchConf.destAirport, searchConf.toDate);
            t.flight2 = searchConf.backDate != null ? new Flight(searchConf.destAirport, searchConf.srcAirport, searchConf.backDate) : null;
            t.searchTime = searchTime;
            t.momondoSearchUrl = MomondoUrlGenerator.getUrl(searchConf);
        }

        if (searchConf.onlyTopResult) {
            if (trips.size() > 0) {
                Trip topResult = trips.get(0);
                trips = Collections.singletonList(topResult);
            }
        }

        return new SearchResult(searchConf.searchGroupId, trips);
    }

    private Cookie createCookie(String name, String value) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(".momondo.ru");
        cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
        cookie.setPath("/");
        cookie.setExpiryDate(DateUtils.getNextDay(DateUtils.getCurrentDate()));
        return cookie;
    }

}
