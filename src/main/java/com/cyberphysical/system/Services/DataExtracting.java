package com.cyberphysical.system.Services;

import com.cyberphysical.system.Models.Record;
import com.cyberphysical.system.Repos.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DataExtracting {
    @Autowired
    RecordRepository recordRepository;

    public String extractData(String region, String date1,
                               String date2, Model model) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date startDate = formatter.parse(date1);
        Date endDate = formatter.parse(date2);

        List<Record> records = recordRepository.findByRegionAndDate(region, startDate, endDate);
        if(records.isEmpty()) {
            return "nodata_error";
        }

        List<String> dates = new ArrayList<>();
        List<Integer> temps = new ArrayList<>();
        for (Record r : records) {
            dates.add(r.getDate().toString());
            temps.add(r.getTemp());
        }
        Set<Integer> distinctTemps = new TreeSet<>(temps);

        List<Integer> countTemps = new ArrayList<>();
        for (Integer distinctTemp : distinctTemps) {
            countTemps.add(recordRepository.countRecordsByTempEqualsAndRegionIsAndDateAfterAndDateBefore(
                    distinctTemp, region, startDate, endDate));
        }

        String[] windDirections = {"Северный", "С-З", "Западный", "Ю-З", "Южный", "Ю-В", "Восточный", "С-В"};

        List<Integer> distinctSpeeds = recordRepository.findDistinctSpeed(region, startDate, endDate);
        Set<Integer> orderedDistinctSpeeds = new TreeSet<>(distinctSpeeds);

        List<Integer> countOrderedDistinctSpeeds = new ArrayList<>();
        for (Integer orderDistinctSpeed : orderedDistinctSpeeds) {
            countOrderedDistinctSpeeds.add(recordRepository.countRecordsBySpeedIsAndRegionIsAndDateAfterAndDateBefore(
                    orderDistinctSpeed, region, startDate, endDate));
        }

        List<String> orderedDistinctSpeedsStr = new ArrayList<>();
        for (Integer i : orderedDistinctSpeeds) {
            orderedDistinctSpeedsStr.add(i.toString());
        }

        List<List<Integer>> countOrderedDistinctSpeedsByWinds = new ArrayList<>(windDirections.length);
        for (int i = 0; i < windDirections.length; i++) {
            countOrderedDistinctSpeedsByWinds.add(new ArrayList<>());
            for (Integer orderedDistinctSpeed : orderedDistinctSpeeds) {
                countOrderedDistinctSpeedsByWinds.get(i).add(
                        recordRepository.countRecordsBySpeedIsAndWindIsAndRegionIsAndDateAfterAndDateBefore(
                                orderedDistinctSpeed, windDirections[i], region, startDate, endDate));
            }
        }

        model.addAttribute("dates", dates.toArray());
        model.addAttribute("temps", temps.toArray());
        model.addAttribute("distinctTemps", distinctTemps.toArray());
        model.addAttribute("countTemps", countTemps.toArray());
        model.addAttribute("winds", windDirections);
        model.addAttribute("countSpeedsByWinds", countOrderedDistinctSpeedsByWinds);
        model.addAttribute("distinctSpeeds", orderedDistinctSpeeds);
        model.addAttribute("distinctSpeedsStr", orderedDistinctSpeedsStr);
        model.addAttribute("countSpeeds", countOrderedDistinctSpeeds);

        return "visual";
    }
}
