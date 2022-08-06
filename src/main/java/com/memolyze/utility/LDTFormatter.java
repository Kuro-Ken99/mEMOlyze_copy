package com.memolyze.utility;

import java.time.LocalDateTime;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LDTFormatter {

  public LocalDateTime fromFormat(String strDate) {
    if (strDate.equals("")) {
      strDate = "2000-01-01T00:00:00.000";
    } else {
      strDate += "T00:00:00.000"; 
    }

    return LocalDateTime.parse(strDate);
  }

  public LocalDateTime toFormat(String strDate) {
    if (strDate.equals("")) {
      strDate = "2100-12-31T23:59:59.999";
    } else {
      strDate += "T23:59:59.999"; 
    }

    return LocalDateTime.parse(strDate);
  }

  public LocalDateTime defaultFromDate() {
    return LocalDateTime.of(2000, 1, 1, 0, 0, 0, 00);
  }

  public LocalDateTime defaultToDate() {
    return LocalDateTime.of(2100, 1, 1, 0, 0, 0, 00);
  }
}
