package ru.rustyskies.search;

import lombok.extern.log4j.Log4j;
import ru.rustyskies.constants.Airport;
import ru.rustyskies.model.SearchConf;
import ru.rustyskies.model.SearchRangeConf;
import ru.rustyskies.model.SearchResult;
import ru.rustyskies.model.Trip;
import ru.rustyskies.search.handlers.MockSearchHandler;
import ru.rustyskies.search.handlers.MomondoRestSearchHandler;
import ru.rustyskies.search.handlers.MomondoWebDriverHandler;
import ru.rustyskies.search.handlers.SearchHandler;
import ru.rustyskies.utils.DateUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;

@Log4j
public class Search {

    public enum SearchEngine {
        Mock(MockSearchHandler.class),
        MomondoRest(MomondoRestSearchHandler.class),
        MomondoWebDriver(MomondoWebDriverHandler.class);

        private final Class<? extends SearchHandler> searchHandlerClass;

        SearchEngine(Class<? extends SearchHandler> searchHandlerClass) {
            this.searchHandlerClass = searchHandlerClass;
        }
    }

    public final static int MSEC_A_DAY = 86400000; // Number of milliseconds in a day

    private final int searchThreadsPoolSize;
    private final ExecutorService pool;
    private final CompletionService<SearchResult> executorCompletionService;

    private int tasksCount;
    private Date searchStartPoint;

    private Map<String, SearchRangeConf> searchGroupConfigurations;

    private SearchEngine searchEngine;

    public Search(int searchThreadsPoolSize, SearchEngine searchEngine) {
        if (searchThreadsPoolSize <= 0) {
            throw new RuntimeException("searchThreadsPoolSize parameter value has to be a positive number: " + searchThreadsPoolSize);
        }

        this.searchThreadsPoolSize = searchThreadsPoolSize;
        this.searchEngine = searchEngine;

        pool = Executors.newFixedThreadPool(searchThreadsPoolSize);
        executorCompletionService = new ExecutorCompletionService<>(pool);

        searchGroupConfigurations = new HashMap<>();
    }

    private Future<SearchResult> addBatchTask(SearchConf searchTask) {
        SearchHandler searchHandler;
        try {
            searchHandler = searchEngine.searchHandlerClass.getConstructor(SearchConf.class).newInstance(searchTask);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        // If this is the first task in the line, marking the start point
        if (tasksCount == 0) {
            searchStartPoint = DateUtils.getCurrentDate();

            log.info("Starting to process tasks. There are " + searchThreadsPoolSize + " active threads.");
        }
        tasksCount++;

        return executorCompletionService.submit(searchHandler);
    }

    public void processResults() {
        Map<String, List<Trip>> groups = new HashMap<>();

        int tasksProcessed = 0;
        long totalCpuTime = 0;
        try {
            Future<SearchResult> future;
            while ((future = executorCompletionService.poll(10, TimeUnit.MINUTES)) != null) {
                SearchResult searchResult = future.get();

                if (searchResult.searchGroupId != null) {
                    if (searchResult.trips != null && searchResult.trips.size() > 0) {
                        if (groups.containsKey(searchResult.searchGroupId)) {
                            List<Trip> list = groups.get(searchResult.searchGroupId);
                            list.add(searchResult.trips.get(0));

                            // Checking if current search group is finished
                            SearchRangeConf conf = searchGroupConfigurations.get(searchResult.searchGroupId);
                            if (list.size() >= conf.elementsCount) {
                                // Saving report
                                totalCpuTime += ReportGenerator.saveReport(conf, DateUtils.getCurrentDate(), list);

                                // Cleaning up
                                groups.remove(searchResult.searchGroupId);
                                searchGroupConfigurations.remove(searchResult.searchGroupId);
                            }
                        } else {
                            List<Trip> list = new LinkedList<>();
                            list.add(searchResult.trips.get(0));
                            groups.put(searchResult.searchGroupId, list);
                        }
                    }
                }

                tasksProcessed++;
                int donePercent = tasksProcessed * 100 / tasksCount;
                log.info("Tasks processed: " + tasksProcessed + "/" + tasksCount + " (" + donePercent + "%)");
            }
        } catch (InterruptedException | ExecutionException e1) {
            throw new RuntimeException(e1);
        }

        shutdownAndAwaitTermination();

        // If there are some search groups left, generating reports for them as well
        for (String searchGroupId : groups.keySet()) {
            List<Trip> list = groups.get(searchGroupId);
            SearchRangeConf conf = searchGroupConfigurations.get(searchGroupId);

            // Saving report
            totalCpuTime += ReportGenerator.saveReport(conf, DateUtils.getCurrentDate(), list);

            // Cleaning up
            groups.remove(searchGroupId);
            searchGroupConfigurations.remove(searchGroupId);
        }

        long processTime = DateUtils.getDateDifference(searchStartPoint);
        double speedupFactor = (double) totalCpuTime / (double) processTime;
        int threadsEfficiencyFactor = (int) (speedupFactor * 100.0 / searchThreadsPoolSize);

        DecimalFormat df = new DecimalFormat("#.##");

        log.info("All " + tasksCount + " tasks were completed using " + searchThreadsPoolSize + " threads.");
        log.info("Real time to complete all the tasks: " + DateUtils.getSecondsAsFormatedTime(processTime));
        log.info("CPU time to complete all the tasks: " + DateUtils.getSecondsAsFormatedTime(totalCpuTime));
        log.info("Average CPU time to process a trip: " + DateUtils.getSecondsAsFormatedTime(totalCpuTime / tasksCount));
        log.info("Average real time to process a trip: " + DateUtils.getSecondsAsFormatedTime(processTime / tasksCount));
        log.info("Speed up factor is " + df.format(speedupFactor) + "x");
        log.info("Threads efficiency factor is " + threadsEfficiencyFactor + "%");
    }

    private void shutdownAndAwaitTermination() {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks

                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public Future<SearchResult> search(Airport srcAirport, Airport destAirport, Date toDate, boolean onlyTopResult,
                                       boolean onlyDirectFlights) {
        return search(srcAirport, destAirport, toDate, null, onlyTopResult, onlyDirectFlights);
    }

    public Future<SearchResult> search(Airport srcAirport, Airport destAirport, Date toDate, Date backDate,
                                       boolean onlyTopResult, boolean onlyDirectFlights) {
        return addBatchTask(new SearchConf(srcAirport, destAirport, toDate, backDate, onlyTopResult, onlyDirectFlights));
    }

    public List<Future<SearchResult>> searchRange(Airport srcAirport, Airport destAirport, Date toDateStart, Date toDateEnd,
                                                  Date backDateStart, Date backDateEnd, boolean onlyDirectFlights) {
        return searchRange(srcAirport, destAirport, toDateStart, toDateEnd, backDateStart, backDateEnd, -1, -1, onlyDirectFlights);
    }

    public List<Future<SearchResult>> searchRange(Airport srcAirport, Airport destAirport, Date toDateStart, Date toDateEnd,
                                                  Date backDateStart, Date backDateEnd, int minDaysDifference,
                                                  int maxDaysDifference, boolean onlyDirectFlights) {
        if (toDateStart == null || toDateEnd == null) {
            throw new RuntimeException("toDateStart and toDateEnd can't be null");
        }
        if (toDateEnd.getTime() < toDateStart.getTime()) {
            throw new RuntimeException("toDateEnd should be in future in respect to toDateStart");
        }
        if (backDateStart != null && backDateEnd != null) {
            if (backDateEnd.getTime() < backDateStart.getTime()) {
                throw new RuntimeException("backDateEnd should be in future in respect to backDateStart");
            }
        }

        // Info
        StringBuilder info = new StringBuilder();
        info.append("Searching flights: ");
        info.append(srcAirport.code);
        if (backDateStart != null && backDateEnd != null) {
            info.append(" <-> ");
        } else {
            info.append(" -> ");
        }
        info.append(destAirport.code).append(" on ");
        info.append("(").append(DateUtils.getDateAsString(toDateStart)).append(" -> ")
                .append(DateUtils.getDateAsString(toDateEnd)).append(")");
        if (backDateStart != null && backDateEnd != null) {
            info.append(" <-> ");
            info.append("(").append(DateUtils.getDateAsString(backDateStart)).append(" -> ")
                    .append(DateUtils.getDateAsString(backDateEnd)).append(")");
        }
        log.info(info.toString());

        List<Future<SearchResult>> result = new LinkedList<>();

        String searchGroupId = UUID.randomUUID().toString();

        int tasksCount = 0;

        // Loading tasks into queue
        Date backDate = backDateStart;
        while (backDate == null || backDateEnd == null || backDate.getTime() <= backDateEnd.getTime()) {
            Date toDate = toDateStart;
            while (toDate.getTime() <= toDateEnd.getTime()) {
                // Calculating the length of the trip (between "to" and "back" dates)
                if (backDate != null) {
                    long tripLength = backDate.getTime() - toDate.getTime();
                    if (minDaysDifference > 0 && tripLength < minDaysDifference * MSEC_A_DAY) {
                        // Skipping the search if current combination between "to" and "back" is not within the min days boundary
                        toDate = DateUtils.modifyDateField(toDate, Calendar.DATE, 1); // +1 day to toDate
                        continue;
                    }
                    if (maxDaysDifference > 0 && tripLength > maxDaysDifference * MSEC_A_DAY) {
                        // Skipping the search if current combination between "to" and "back" is not within the max days boundary
                        toDate = DateUtils.modifyDateField(toDate, Calendar.DATE, 1); // +1 day to toDate
                        continue;
                    }
                }

                result.add(addBatchTask(new SearchConf(searchGroupId, srcAirport, destAirport, toDate, backDate, true, onlyDirectFlights)));
                tasksCount++;

                toDate = DateUtils.modifyDateField(toDate, Calendar.DATE, 1); // +1 day to toDate
            }
            // +1 day to backDate
            if (backDate != null) {
                backDate = DateUtils.modifyDateField(backDate, Calendar.DATE, 1);
            } else {
                break;
            }
        }

        SearchRangeConf searchRangeConf = new SearchRangeConf(srcAirport, destAirport, toDateStart, toDateEnd, backDateStart, backDateEnd, tasksCount);
        searchGroupConfigurations.put(searchGroupId, searchRangeConf);

        log.info("New search was added with " + tasksCount + " tasks in it.");

        return result;
    }

}
