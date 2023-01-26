package com.jesys.notify.posts;

import com.jesys.notify.enums.Months;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class RulatePost extends AbstractPost {
    private String status;
    private String Title;

    @Override
    protected LocalDateTime convertStringToDate(String s) {
        String[] parts = s.replace("г.,", "").split(" ");
        Months month = Months.valueOf(parts[1]);
        LocalDate localDate = LocalDate.now();
        String newString = parts[0] + " " + month.getNumeric() + " " + parts[2] + " " + parts[3];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy HH:mm");
        return LocalDateTime.parse(newString, formatter);
    }

    @Override
    public void setDateOfPost(String s) {
        super.dateOfPost = convertStringToDate(s);
    }
}
