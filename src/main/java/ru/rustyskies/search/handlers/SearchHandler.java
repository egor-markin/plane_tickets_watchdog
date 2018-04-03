package ru.rustyskies.search.handlers;

import lombok.extern.log4j.Log4j;
import ru.rustyskies.model.SearchConf;
import ru.rustyskies.model.SearchResult;

import java.util.concurrent.Callable;

@Log4j
public abstract class SearchHandler implements Callable<SearchResult> {

    // Number of attempts to perform search for a flight before giving up
    private static final int MAX_NUMBER_OF_SEARCH_ATTEMPTS = 3;

    private final SearchConf searchConf;

    public SearchHandler(SearchConf searchConf) {
        this.searchConf = searchConf;

    }

    @Override
    public SearchResult call() {
        int attempt = 0;
        SearchResult result = null;
        while (!checkResult(result) && attempt < MAX_NUMBER_OF_SEARCH_ATTEMPTS) {
            result = search(searchConf);

            if (!checkResult(result)) {
                log.warn("Failed to perform search " + searchConf.getFlights() + ". Trying again...");
            }
            attempt++;
        }

        if (!checkResult(result)) {
            log.error("Failed to perform search " + searchConf.getFlights() + " after " + MAX_NUMBER_OF_SEARCH_ATTEMPTS + " attempts.");
        }
        return result;
    }

    private boolean checkResult(SearchResult result) {
        return result != null && result.trips != null && !result.trips.isEmpty();
    }

    protected abstract SearchResult search(SearchConf searchConf);

}
