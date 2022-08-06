package com.memolyze.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.memolyze.entity.Memo;
import com.memolyze.entity.User;
import com.memolyze.service.AnalyticsService;
import com.memolyze.service.MemoCRUDService;
import com.memolyze.utility.LDTFormatter;
import com.memolyze.utility.RandomMessage;

@Controller
public class IndexController {
  
  @Autowired
  MemoCRUDService memoCRUDService;
  @Autowired
  AnalyticsService analyticsService;

  @GetMapping("/")
  public String home(Model model) {
    List<String> logoList = new ArrayList<String>(Arrays.asList("bonheur", "感情スコア", "楽しい", "analyze", "「好き」って使いすぎ笑", "negative", "メモ", "？", "嬉しい", "melancholie", "よく使う単語は…"));
    model.addAttribute("logoList", logoList);
    return "home/home";
  }

  @GetMapping("/top")
  public String top(Model model, @AuthenticationPrincipal User loginUser) {
    int userId = loginUser.getId();
    LocalDateTime fromdate = LDTFormatter.defaultFromDate();
    LocalDateTime todate = LDTFormatter.defaultToDate();

    List<Memo> memosList = memoCRUDService.findMemo(userId, "memo");
    List<String> monthList = memoCRUDService.findMonthList(userId);
    List<Entry<String, Integer>> mfList = analyticsService.wordFrequency(userId, fromdate, todate);
    List<Object> sentiScoreList = analyticsService.emotionAnalytics(userId, fromdate, todate);

    model.addAttribute("loginUser", loginUser);
    model.addAttribute("memosList", memosList);
    model.addAttribute("monthList", monthList);
    model.addAttribute("mfList", mfList);
    model.addAttribute("sentiScoreList", sentiScoreList);
    
    return "top/top";
  }

  @GetMapping("/create")
  public String create(Model model, @AuthenticationPrincipal User loginUser) {

    List<Memo> draftsList = memoCRUDService.findMemo(loginUser.getId(), "draft");

    model.addAttribute("loginUser", loginUser);
    model.addAttribute("draftsList", draftsList);
    
    return "crud/create";
  }

  @GetMapping("/edit")
  public String edit(Model model, @AuthenticationPrincipal User loginUser, @RequestParam("id") int memoId) {
    Memo memo = memoCRUDService.findMemo(memoId);

    model.addAttribute("memo", memo);
    model.addAttribute("loginUser", loginUser);

    return "crud/edit";
  }

  @GetMapping("/drop")
  public String drop(@AuthenticationPrincipal User loginUser, @RequestParam("memo_type") String memoType) {
    if (memoType.equals("draft")) {
      return "redirect:/create";
    }
    return "redirect:/top";
  }

  @GetMapping("/search")
  public String search(Model model, @AuthenticationPrincipal User loginUser) {
    model.addAttribute("loginUser", loginUser);
    return "crud/search";
  }

  @GetMapping("/archive")
  public String archive(Model model, @AuthenticationPrincipal User loginUser, @RequestParam String month) {

    LocalDateTime fromDate = LocalDateTime.parse(month.substring(0, 10) + "T" + month.substring(11));
    LocalDateTime toDate = fromDate.plusMonths(1);
    int userId = loginUser.getId();

    List<Memo> archiveList = memoCRUDService.findArchiveMemo(userId, fromDate, toDate);
    List<String> monthList = memoCRUDService.findMonthList(userId);

    model.addAttribute("month", month);
    model.addAttribute("loginUser", loginUser);
    model.addAttribute("archiveList", archiveList);
    model.addAttribute("monthList", monthList);

    return "crud/archive";
  }

  @GetMapping("/analytics")
  public String analytics(Model model, @AuthenticationPrincipal User loginUser) {
    LocalDateTime fromdate = LDTFormatter.defaultFromDate();
    LocalDateTime todate = LDTFormatter.defaultToDate();
    int userId = loginUser.getId();

    List<Entry<String, Integer>> mfList = analyticsService.wordFrequency(userId, fromdate, todate);
    List<Object> sentiScoreList = analyticsService.emotionAnalytics(userId, fromdate, todate);
    int sentiScore = (Integer) sentiScoreList.get(0);
    String message = RandomMessage.randomMessage(sentiScore);

    model.addAttribute("loginUser", loginUser);
    model.addAttribute("mfList", mfList);
    model.addAttribute("sentiScoreList", sentiScoreList);
    model.addAttribute("message", message);

    return "analytics/analytics";
  }

  @GetMapping("/contact")
  public String contact(Model model, @AuthenticationPrincipal User loginUser) {
    model.addAttribute("loginUser", loginUser);
    return "contact/contact";
  }
  
  @GetMapping("/mypage")
  public String mypage(Model model, @AuthenticationPrincipal User loginUser) {
    model.addAttribute("loginUser", loginUser);

    return "mypage/mypage";
  }
}