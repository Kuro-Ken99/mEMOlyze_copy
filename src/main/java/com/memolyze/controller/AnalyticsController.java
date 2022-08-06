package com.memolyze.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.memolyze.entity.User;
import com.memolyze.service.AnalyticsService;
import com.memolyze.utility.LDTFormatter;
import com.memolyze.utility.RandomMessage;

@Controller
public class AnalyticsController {
  
  @Autowired
  AnalyticsService analyticsService;

  @PostMapping("/analytics")
  public String analytics(Model model, @AuthenticationPrincipal User loginUser, @RequestParam("from_date") String fromDate, @RequestParam("to_date") String toDate) {
    LocalDateTime fromdate = LDTFormatter.fromFormat(fromDate);
    LocalDateTime todate = LDTFormatter.toFormat(toDate);
    int userId = loginUser.getId();

    List<Entry<String, Integer>> mfList = analyticsService.wordFrequency(userId, fromdate, todate);
    List<Object> sentiScoreList = analyticsService.emotionAnalytics(userId, fromdate, todate);

    int sentiScore = (Integer) sentiScoreList.get(0);
    String message = RandomMessage.randomMessage(sentiScore);


    model.addAttribute("mfList", mfList);
    model.addAttribute("sentiScoreList", sentiScoreList);
    model.addAttribute("from_date", fromDate);
    model.addAttribute("to_date", toDate);
    model.addAttribute("message", message);
    model.addAttribute("loginUser", loginUser);

    return "analytics/analytics";
  }
}