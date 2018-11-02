package ru.rustyskies.search.handlers;

import lombok.extern.log4j.Log4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.rustyskies.model.Flight;
import ru.rustyskies.model.SearchConf;
import ru.rustyskies.model.SearchResult;
import ru.rustyskies.model.Trip;
import ru.rustyskies.utils.DateUtils;
import ru.rustyskies.utils.OsUtils;
import ru.rustyskies.utils.StringUtils;

import java.sql.Date;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                log.warn("Unable to close WebDriver", e);
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
                WebElement searchBlock = d.findElement(By.className("Common-Results-SpinnerWithProgressBar"));
                if (searchBlock != null) {
                    WebElement searchResult = searchBlock.findElement(By.className("title"));
                    if (searchResult != null) {
                        return "Поиск завершен".equals(searchResult.getText()) || "Search complete".equals(searchResult.getText());
                    }
                }
                return false;
            });
        } catch (WebDriverException e) {
            return null;
        }

        List<WebElement> results = driver.findElements(By.className("Flights-Results-FlightResultItem"));
        for (WebElement r : results) {
            Trip trip = new Trip();
            trips.add(trip);

            String searchResultId = r.getAttribute("id");

            if (searchResultId == null || searchResultId.trim().equals("")) {
                log.warn("Search result has no id!");
                continue;
            }

            // From
            WebElement s0 = r.findElement(By.id(searchResultId + "-info-leg-0"));
            trip.flight1 = flights.get(0);
            trip.airlines1 = s0.findElement(By.className("times")).findElement(By.className("bottom")).getText();
            trip.travelTime1 = parseTime(s0.findElement(By.className("duration")).findElement(By.className("top")).getText());
            trip.stops1 = parseStops(s0.findElement(By.className("stops")).findElement(By.className("top")).getText());

            // To
            if (flights.size() > 1) {
                WebElement s1 = r.findElement(By.id(searchResultId + "-info-leg-1"));
                trip.flight2 = flights.get(1);
                trip.airlines2 = s1.findElement(By.className("times")).findElement(By.className("bottom")).getText();
                trip.travelTime2 = parseTime(s1.findElement(By.className("duration")).findElement(By.className("top")).getText());
                trip.stops2 = parseStops(s1.findElement(By.className("stops")).findElement(By.className("top")).getText());
            }

            // Price
            WebElement priceElement = r.findElement(By.className("price"));
            trip.price = parsePrice(priceElement.getText());
            trip.priceCurrency = parseCurrency(priceElement.getText());

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
            int minutes = StringUtils.getInt(parts[0]);
            return minutes * 60;
        } else if (parts.length == 2) {
            int hours = StringUtils.getInt(parts[0]);
            int minutes = StringUtils.getInt(parts[1]);
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
            return StringUtils.getInt(s);
        }
    }

    private static int parsePrice(String priceStr) {
        // Removing all the non-digit symbols from the string
        return StringUtils.getInt(priceStr.replaceAll("\\D", ""));
    }

    private static String parseCurrency(String priceStr) {
        Matcher matcher = Pattern.compile("(\\w+)$").matcher(priceStr.trim());
        return matcher.find() ? matcher.group() : "";
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
            options.setLogLevel(FirefoxDriverLogLevel.FATAL);
            options.setHeadless(true);
//            options.setProxy(new Proxy().setSocksProxy("127.0.0.1:8888"));
            System.setProperty("webdriver.gecko.driver", GECKO_DRIVER_LOCATION);
            driver = new FirefoxDriver(options);
        } else {
            throw new RuntimeException(OsUtils.getOsName() + " is not supported");
        }

        return driver;
    }
}
