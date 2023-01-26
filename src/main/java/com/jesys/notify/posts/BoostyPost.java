package com.jesys.notify.posts;

import com.jesys.notify.Months;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class BoostyPost extends AbstractPost {

    @Override
    protected LocalDateTime convertStringToDate(String s) {
        String[] parts = s.split(" ");
        Months month = Months.valueOf(parts[1].replace(".",""));
        String[] time = parts[3].split(":");
        LocalDate localDate = LocalDate.now();
        String newString = parts[0] + " " + month.getEnglish() + " " + localDate.getYear() + " " + time[0] + ":" + time[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy HH:mm");
        return LocalDateTime.parse(newString, formatter);
    }

    @Override
    public void setDateOfPost(String s) {
        super.dateOfPost = convertStringToDate(s);
    }
}
