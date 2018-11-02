package ru.rustyskies;

import ru.rustyskies.constants.Airport;
import ru.rustyskies.search.Search;
import ru.rustyskies.utils.DateUtils;

import java.util.Date;

public class Main {

    public static void main(String args[]) {
        Search search = new Search(1, Search.SearchEngine.MomondoWebDriver);
//        Search search = new Search(1, Search.SearchEngine.Mock);

        search.searchRange(Airport.Moscow, Airport.Venice,
                DateUtils.getDate(2018, 12, 25),
                DateUtils.getDate(2019, 1, 3),
                DateUtils.getDate(2019, 1, 10),
                DateUtils.getDate(2019, 1, 15), false);


//        search.searchRange(Airport.Venice, Airport.Moscow,
//                DateUtils.getDate(2019, 1, 10),
//                DateUtils.getDate(2019, 1, 15), null, null, false);
//
//        search.searchRange(Airport.Moscow, Airport.Milan,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2018, 12, 29), null, null, false);
//        search.searchRange(Airport.Milan, Airport.Venice,
//                DateUtils.getDate(2019, 1, 2),
//                DateUtils.getDate(2019, 1, 3), null, null, false);
//
//        search.searchRange(Airport.Moscow, Airport.Rome,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2018, 12, 29), null, null, false);
//        search.searchRange(Airport.Rome, Airport.Venice,
//                DateUtils.getDate(2019, 1, 2),
//                DateUtils.getDate(2019, 1, 3), null, null, false);
//
//        search.searchRange(Airport.Moscow, Airport.Madrid,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2018, 12, 29), null, null, false);
//        search.searchRange(Airport.Madrid, Airport.Venice,
//                DateUtils.getDate(2019, 1, 2),
//                DateUtils.getDate(2019, 1, 3), null, null, false);
//
//        search.searchRange(Airport.Moscow, Airport.Barcelona,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2018, 12, 29), null, null, false);
//        search.searchRange(Airport.Barcelona, Airport.Venice,
//                DateUtils.getDate(2019, 1, 2),
//                DateUtils.getDate(2019, 1, 3), null, null, false);
//
//        search.searchRange(Airport.Moscow, Airport.Tunis,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2018, 12, 29), null, null, false);
//        search.searchRange(Airport.Tunis, Airport.Venice,
//                DateUtils.getDate(2019, 1, 2),
//                DateUtils.getDate(2019, 1, 3), null, null, false);
//
//        search.searchRange(Airport.Moscow, Airport.Berlin,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2018, 12, 29), null, null, false);
//        search.searchRange(Airport.Berlin, Airport.Venice,
//                DateUtils.getDate(2019, 1, 2),
//                DateUtils.getDate(2019, 1, 3), null, null, false);
//
//        search.searchRange(Airport.Moscow, Airport.Stockholm,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2018, 12, 29), null, null, false);
//        search.searchRange(Airport.Stockholm, Airport.Venice,
//                DateUtils.getDate(2019, 1, 2),
//                DateUtils.getDate(2019, 1, 3), null, null, false);

//        search.searchRange(Airport.Moscow, Airport.Jakarta,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2019, 1, 3),
//                DateUtils.getDate(2019, 1, 10),
//                DateUtils.getDate(2019, 1, 15), false);
//
//        search.searchRange(Airport.Moscow, Airport.Manila,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2019, 1, 3),
//                DateUtils.getDate(2019, 1, 10),
//                DateUtils.getDate(2019, 1, 15), false);
//
//        search.searchRange(Airport.Moscow, Airport.Bangkok,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2019, 1, 3),
//                DateUtils.getDate(2019, 1, 10),
//                DateUtils.getDate(2019, 1, 15), false);
//
//        search.searchRange(Airport.Moscow, Airport.Phuket,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2019, 1, 3),
//                DateUtils.getDate(2019, 1, 10),
//                DateUtils.getDate(2019, 1, 15), false);
//
//        search.searchRange(Airport.Moscow, Airport.HoChiMinh,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2019, 1, 3),
//                DateUtils.getDate(2019, 1, 10),
//                DateUtils.getDate(2019, 1, 15), false);
//
//        search.searchRange(Airport.Moscow, Airport.Colombo,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2019, 1, 3),
//                DateUtils.getDate(2019, 1, 10),
//                DateUtils.getDate(2019, 1, 15), false);
//
//        search.searchRange(Airport.Moscow, Airport.MexicoCity,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2019, 1, 3),
//                DateUtils.getDate(2019, 1, 10),
//                DateUtils.getDate(2019, 1, 15), false);

//        search.searchRange(Airport.Moscow, Airport.NewYork,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2019, 1, 3),
//                DateUtils.getDate(2019, 1, 10),
//                DateUtils.getDate(2019, 1, 15), false);
//
//        search.searchRange(Airport.Moscow, Airport.Marrakesh,
//                DateUtils.getDate(2018, 12, 22),
//                DateUtils.getDate(2018, 12, 29), null, null, false);
//        search.searchRange(Airport.Marrakesh, Airport.Venice,
//                DateUtils.getDate(2019, 1, 2),
//                DateUtils.getDate(2019, 1, 3), null, null, false);

        search.processResults();
    }

}
