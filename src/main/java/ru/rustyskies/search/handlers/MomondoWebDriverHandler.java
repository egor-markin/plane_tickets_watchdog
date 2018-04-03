package ru.rustyskies.search.handlers;

import lombok.extern.log4j.Log4j;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.rustyskies.model.Flight;
import ru.rustyskies.model.SearchConf;
import ru.rustyskies.model.SearchResult;
import ru.rustyskies.model.Trip;
import ru.rustyskies.utils.DateUtils;
import ru.rustyskies.utils.OsUtils;

import java.sql.Date;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Log4j
public class MomondoWebDriverHandler extends SearchHandler {

    // Download it from https://github.com/mozilla/geckodriver/releases
    private static final String GECKO_DRIVER_LOCATION = "c:\\Tools\\GeckoDriver\\geckodriver.exe";

    public MomondoWebDriverHandler(SearchConf searchTask) {
        super(searchTask);
    }

    @Override
    protected SearchResult search(SearchConf searchConf) {
        WebDriver driver = getDriverInstance();
        try {
            return new SearchResult(searchConf.searchGroupId, search(driver, searchConf.flights, searchConf.onlyTopResult, searchConf.onlyDirectFlights));
        } finally {
            try {
                driver.quit();
            } catch (WebDriverException e) {
                // Ignoring
            }
        }
    }

    private List<Trip> search(WebDriver driver, List<Flight> flights, boolean onlyTopResult, boolean onlyDirectFlights) {
        Date startPoint = DateUtils.getCurrentDate();

        List<Trip> trips = new LinkedList<>();

        // URL
        String momondoSearchUrl = MomondoUrlGenerator.getUrl(flights, onlyDirectFlights);

        // http://docs.seleniumhq.org/docs/03_webdriver.jsp#locating-ui-elements-webelements

        try {
            driver.get(momondoSearchUrl);
            (new WebDriverWait(driver, 240)).until((ExpectedCondition<Boolean>) d -> {
                String text = d.findElement(By.id("searchProgressText")).getText();
                return "Поиск завершен".equals(text) || "Search complete".equals(text);
            });
        } catch (WebDriverException e) {
            return null;
        }

        List<WebElement> results = driver.findElements(By.className("result-box"));
        for (WebElement r : results) {
            Trip trip = new Trip();
            trips.add(trip);

            // From
            WebElement s0 = r.findElement(By.className("segment0"));
            trip.flight1 = flights.get(0);
            trip.airlines1 = s0.findElement(By.tagName("img")).getAttribute("alt");
            trip.travelTime1 = parseTime(s0.findElement(By.className("travel-time")).getText());
            trip.stops1 = parseStops(s0.findElement(By.className("total")).getText());

            // To
            List<WebElement> s1 = r.findElements(By.className("segment1"));
            if (s1.size() > 0) {
                trip.flight2 = flights.size() > 1 ? flights.get(1) : null;
                trip.airlines2 = s1.get(0).findElement(By.tagName("img")).getAttribute("alt");
                trip.travelTime2 = parseTime(s1.get(0).findElement(By.className("travel-time")).getText());
                trip.stops2 = parseStops(s1.get(0).findElement(By.className("total")).getText());
            }

            // Price
            WebElement priceElement = r.findElement(By.className("price"));
            trip.price = parsePrice(priceElement.findElement(By.className("value")).getText());
            trip.priceCurrency = priceElement.findElement(By.className("unit")).getText();

            // TODO
//            trip.priceEur;
//            trip.ticketUrl;


            // URL
            trip.momondoSearchUrl = momondoSearchUrl;

            if (onlyTopResult) {
                break;
            }
        }

        long searchTime = DateUtils.getDateDifference(startPoint);
        log.info(flights + " best price: " + (trips.size() > 0 ? trips.get(0).price : "???") + " search time: "
                + DateUtils.getSecondsAsFormatedTime(searchTime));

        // Setting process time
        for (Trip t : trips) {
            t.searchTime = searchTime;
        }
        return trips;
    }

    /** Returns formatted time in seconds */
    private int parseTime(String timeStr) {
        // Removing all the non-digit symbols from the string and splitting numbers in groups
        String[] parts = timeStr.replaceAll("\\D", " ").trim().replaceAll("\\s+"," ").split(" ");
        if (parts.length == 1) {
            int minutes = Integer.parseInt(parts[0]);
            return minutes * 60;
        } else if (parts.length == 2) {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours * 3600 + minutes * 60;
        } else {
            throw new RuntimeException("Unexpected number of parts (" + parts.length + ") in string: " + timeStr + ", parts are " + Arrays.toString(parts));
        }
    }

    /** Returns a number of stops */
    private int parseStops(String stopsStr) {
        // Removing all the non-digit symbols from the string
        String s = stopsStr.replaceAll("\\D", "");
        if (s.length() == 0) {
            return 0;
        } else {
            return Integer.parseInt(s);
        }
    }

    private int parsePrice(String priceStr) {
        // Removing all the non-digit symbols from the string
        return Integer.parseInt(priceStr.replaceAll("\\D", ""));
    }

    private static WebDriver getDriverInstance() {
        WebDriver driver;

        if (OsUtils.isUnix()) {
            throw new RuntimeException("Linux is not supported");
//			// Setup firefox binary to start in Xvfb
//			String Xport = System.getProperty("lmportal.xvfb.id", ":1");
//			final File firefoxPath = new File(System.getProperty("lmportal.deploy.firefox.path", "/usr/bin/firefox"));
//			FirefoxBinary firefoxBinary = new FirefoxBinary(firefoxPath);
//			//DriverService.Builder#withEnvironment
//			firefoxBinary.setEnvironmentProperty("DISPLAY", Xport);
//			driver = new FirefoxDriver(firefoxBinary, null);
        } else if (OsUtils.isWindows()) {
            // Based on https://www.seleniumhq.org/docs/04_webdriver_advanced.jsp
            // Based on https://github.com/mozilla/geckodriver
            FirefoxOptions options = new FirefoxOptions();
//            options.setLogLevel(FirefoxDriverLogLevel.ERROR);
            options.setHeadless(true);
//            options.setProxy(new Proxy().setSocksProxy("127.0.0.1:888"));

            System.setProperty("webdriver.gecko.driver", GECKO_DRIVER_LOCATION);
            driver = new FirefoxDriver(options);

//            driver = new FirefoxDriver();
        } else {
            throw new RuntimeException(OsUtils.getOsName() + " is not supported");
        }

        return driver;
    }
}
