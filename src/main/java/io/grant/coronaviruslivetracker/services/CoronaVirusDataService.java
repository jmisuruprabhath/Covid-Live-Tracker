package io.grant.coronaviruslivetracker.services;

import io.grant.coronaviruslivetracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service //@Service annotation is used with classes that provide some business/logical functionalities
public class CoronaVirusDataService {

    // String to store the URL where the data is
    public static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @PostConstruct // When we annotate a method in Spring Bean with @PostConstruct annotation, it gets executed after the spring bean is initialized.
    // We can have only one method annotated with @PostConstruct annotation.

    @Scheduled(cron = "* * 1 * * *") // This is used to run the method in the first hour of every day.

    //Method to fetch URL Data
    // Exceptions are used because if the send to client fails, no way to handle it
    public void fetchVirusData() throws IOException, InterruptedException {

        List<LocationStats> newStats = new ArrayList<>(); //To avoid concurrency errors

        // The HttpClient class instance acts as a session to send HTTP requests.
        HttpClient client = HttpClient.newHttpClient();

        // Creating the HTTP Request including URI
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build(); //Convert the String into a URI

        // Sends the given request using this client &
        // Handling the response body as a String
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Used instead of Reader to pass String get as HTTP RESPONSE into the following Iterable method
        StringReader csvStringReader = new StringReader(httpResponse.body());

        // To format the fetching data
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvStringReader);
        for (CSVRecord record : records) {
            LocationStats locationStats = new LocationStats();
            locationStats.setState(record.get("Province/State"));
            locationStats.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
            locationStats.setLatestTotalCases(latestCases);
            locationStats.setDiffFromPrevDay(latestCases - prevDayCases);
            newStats.add(locationStats);
        }
        this.allStats = newStats;

    }
}
