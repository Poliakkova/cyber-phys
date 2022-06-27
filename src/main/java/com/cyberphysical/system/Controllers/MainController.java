package com.cyberphysical.system.Controllers;

import com.cyberphysical.system.Models.Record;
import com.cyberphysical.system.Repos.RecordRepository;
import com.cyberphysical.system.Services.DataExtracting;
import com.cyberphysical.system.Services.DataProcessing;
import com.cyberphysical.system.Services.FileUploading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.cyberphysical.system.Services.FileProcessing;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class MainController {
    @Autowired
    FileUploading fileUploading;
    @Autowired
    RecordRepository recordRepository;
    @Autowired
    DataExtracting dataExtracting;
    @Autowired
    DataProcessing dataProcessing;

    @GetMapping("/")
    public String first(Model model) {
        return "first";
    }

    @PostMapping("/process-data")
    public String processData(@RequestParam String file1, Model model) throws IOException {
        return dataProcessing.processData(file1, model);
    }

    @PostMapping("/load-data")
    public String loadData(@RequestParam String file2, @RequestParam int year,
                           @RequestParam int month, @RequestParam String region, Model model) throws IOException {
        File file = new File(file2);
        fileUploading.uploadData(file, region, year, month);
        return "loading_complete";
    }

    @GetMapping("/analise")
    public String second(Model model) {
        List<String> regions = recordRepository.findDistinctRegion();
        model.addAttribute("regions", regions);
        return "second";
    }

    @PostMapping("define-params")
    public String defineParams(@RequestParam String region, @RequestParam String date1,
                               @RequestParam String date2, Model model) throws ParseException {
        return dataExtracting.extractData(region, date1, date2, model);
    }

}
