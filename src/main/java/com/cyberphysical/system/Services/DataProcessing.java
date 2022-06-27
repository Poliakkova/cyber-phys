package com.cyberphysical.system.Services;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class DataProcessing {

    public String processData(String file1, Model model) throws IOException {
        File file = new File(file1);
        FileProcessing fileProcessing = new FileProcessing(file);

        try {
            fileProcessing.processData();
        } catch (Exception e) {
            return "processing_error";
        }

        ArrayList<Integer> mistakeRows = fileProcessing.showMistakes();
        for (Integer i : mistakeRows) {
            System.out.println("mistake! " + i);
        }

        model.addAttribute("rows", mistakeRows);
        return "processing_complete";
    }
}
