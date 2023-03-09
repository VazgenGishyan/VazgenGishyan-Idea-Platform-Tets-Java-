package org.example;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Main {

    static long MILLISECONDS_IN_MINUTES = 60000;
    static long MILLISECONDS_IN_HOURS = MILLISECONDS_IN_MINUTES * 60;
    static long MILLISECONDS_IN_DAYS = MILLISECONDS_IN_HOURS * 24;

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {

        String content = new String(Files.readAllBytes(Paths.get("tickets.json")));
        content = content.replace("﻿", "");
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        JSONArray jsonArray = (JSONArray) json.get("tickets");
        DateFormat format = new SimpleDateFormat("hh:mm dd.MM.yy", Locale.ENGLISH);

        int count = jsonArray.size();

        if(count == 0) {
            System.out.println("Not enough data");
            return;
        }

        long[] durations = new long[count];
        long sum = 0;
        int index = 0;

        for(Object obj : jsonArray) {
            JSONObject object = (JSONObject) obj;

            String arrivalDateString = object.get("arrival_time") + " " + object.get("arrival_date");
            String departureDateString = object.get("departure_time") + " " + object.get("departure_date");

            Date arrivalDate = format.parse(arrivalDateString);
            Date departureDate = format.parse(departureDateString);

            long duration = arrivalDate.getTime() - departureDate.getTime();
            sum += duration;

            durations[index++] = duration;
        }

        Arrays.sort(durations);

        long averageDuration  = sum / count;
        System.out.println("averageDuration = " + getTime(averageDuration));

        int percentileIndexInteger = count * 9/10 - 1;
        float percentileIndexFloat = count * 0.9f  - 1;
        if(percentileIndexFloat - percentileIndexInteger > 0.0f) {
            ++percentileIndexInteger;
        }


        long percentile = durations[percentileIndexInteger];
        System.out.println("percentile = " + getTime(percentile));
    }

    public static String getTime(long duration) {
        long dd = duration / MILLISECONDS_IN_DAYS;
        duration -= dd * MILLISECONDS_IN_DAYS;
        long hh = duration / MILLISECONDS_IN_HOURS;
        duration -= hh * MILLISECONDS_IN_HOURS;
        long mm = duration / MILLISECONDS_IN_MINUTES;
        return dd + " day(s) " + hh + " hour(s) " + mm + " minute(s)";
    }

}