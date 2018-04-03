package ru.rustyskies.search;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import ru.rustyskies.model.SearchRangeConf;
import ru.rustyskies.model.Trip;
import ru.rustyskies.model.TripsReportData;

import java.io.*;
import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Egor Markin
 * @since 22.10.2014
 */
public class ReportGenerator {

	private static final String TEMPLATE_FILE_NAME = "TripsReport.ftl";

	/** Generates report based on specified Statistics object, report will be saved into specified OutputStream object */
	private static void generateReport(TripsReportData reportData, OutputStream outputStream) throws IOException,
			TemplateException {
		// Preparing data for the report template engine
		Map<String, Object> root = new HashMap<>();
		root.put("reportData", reportData);

		// FreeMarker initialization
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

		ClassTemplateLoader ctl = new ClassTemplateLoader(ReportGenerator.class, "/");
		FileTemplateLoader ftl = new FileTemplateLoader(new File("."));
		MultiTemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[] { ctl, ftl });
		cfg.setTemplateLoader(mtl);

		cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_23));
		Template template = cfg.getTemplate(TEMPLATE_FILE_NAME);

		// Generating the report
		Writer out = new OutputStreamWriter(outputStream);
		template.process(root, out);
		out.flush();
		out.close();
	}

	static long saveReport(SearchRangeConf conf, Date reportDate, List<Trip> trips) {
		// Calculating total search time for the report
		long searchTime = 0;
		for (Trip trip : trips) {
			searchTime += trip.searchTime;
		}

		// Assembling filename for the report
		StringBuilder fileName = new StringBuilder();
		fileName.append("FlightPrices-");
		fileName.append(conf.srcAirport.code);
		fileName.append("-to-");
		fileName.append(conf.destAirport.code);
		fileName.append("-on-");
		fileName.append(conf.toDateStart);
		fileName.append("-");
		fileName.append(conf.toDateEnd);
		if (conf.backDateStart != null && conf.backDateEnd != null) {
			fileName.append("-and-");
			fileName.append(conf.backDateStart);
			fileName.append("-");
			fileName.append(conf.backDateEnd);
		}
		fileName.append(".html");

		// Preparing data
		Collections.sort(trips);
		TripsReportData reportData = new TripsReportData(conf, searchTime, reportDate, trips);

		// Saving the report
		try (FileOutputStream outputStream = new FileOutputStream(new File(fileName.toString()))) {
			generateReport(reportData, outputStream);
		} catch (IOException | TemplateException e) {
			throw new RuntimeException(e);
		}

		return searchTime;
	}
}
