package ru.rustyskies;

import ru.rustyskies.constants.Airport;
import ru.rustyskies.search.Search;
import ru.rustyskies.utils.DateUtils;

public class Main {

    public static void main(String args[]) {
        Search search = new Search(1, Search.SearchEngine.MomondoWebDriver);

        search.searchRange(Airport.Moscow, Airport.Tokyo,
                DateUtils.getDate(2018, 3, 26), DateUtils.getDate(2018, 4, 15),
                DateUtils.getDate(2018, 4, 9), DateUtils.getDate(2018, 4, 30),
                9, 14, false);

        search.processResults();
    }

}
