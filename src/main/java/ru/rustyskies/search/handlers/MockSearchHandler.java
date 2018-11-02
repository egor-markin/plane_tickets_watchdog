package ru.rustyskies.search.handlers;

import lombok.extern.log4j.Log4j;
import ru.rustyskies.model.SearchConf;
import ru.rustyskies.model.SearchResult;
import ru.rustyskies.model.Trip;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Log4j
public class MockSearchHandler extends SearchHandler {

    private final static int FAIL_RATE = -1; // Percent of failed searches

    public MockSearchHandler(SearchConf searchConf) {
        super(searchConf);
    }

    @Override
    protected SearchResult search(SearchConf searchConf) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // 20% of all trip searches are unsuccessful
        if (random.nextInt(100) <= FAIL_RATE) {
            return new SearchResult(searchConf.searchGroupId, null);
        } else {
            Trip trip = new Trip();
            trip.flight1 = searchConf.flights.get(0);
            trip.flight2 = searchConf.flights.get(1);

            trip.price = random.nextInt(10000, 20000);
            trip.priceCurrency = "RUB";

            trip.searchTime = random.nextInt(10, 60); // In seconds

            trip.momondoSearchUrl = MomondoUrlGenerator.getUrl(searchConf);

            trip.travelTime1 = random.nextInt(3600, 6 * 3600); // In seconds
            trip.travelTime2 = random.nextInt(3600, 6 * 3600); // In seconds

            trip.airlines1 = UUID.randomUUID().toString();
            trip.airlines2 = UUID.randomUUID().toString();

            trip.stops1 = random.nextInt(0, 3);
            trip.stops2 = random.nextInt(0, 3);

            return new SearchResult(searchConf.searchGroupId, Collections.singletonList(trip));
        }
    }
}
