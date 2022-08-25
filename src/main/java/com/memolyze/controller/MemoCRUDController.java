package com.memolyze.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.memolyze.entity.Memo;
import com.memolyze.entity.User;
import com.memolyze.service.MemoCRUDService;
import com.memolyze.utility.CreateMail;
import com.memolyze.utility.LDTFormatter;

@Controller
public class MemoCRUDController {

  @Autowired
  MemoCRUDService memoCRUDService;
  @Autowired
  MailSender mailSender;

  @PostMapping("/create")
  public String create(RedirectAttributes redirectattributes, @AuthenticationPrincipal User loginUser, @RequestParam String title, @RequestParam String memo,  @RequestParam("memo_type") String memoType) {

    if (memo != null && memo.strip().length() != 0) {
      if (title.strip().equals("")) {
      title = "(無題)";
      }
      memoCRUDService.createMemo(loginUser.getId(), title, memo, memoType);
    } else {
      redirectattributes.addFlashAttribute("errorMessage", "本文は入力必須です");
      return "redirect:/create";
    }

    if (memoType.equals("draft")) {
      return "redirect:/create";
    }

    return "redirect:/top";
  }

  @PostMapping("/update")
  public String update(Model model, @AuthenticationPrincipal User loginUser, @RequestParam String title, @RequestParam String memo, @RequestParam("memo_id") int memoId, @RequestParam("memo_type") String memoType) {
    if (memo != null && memo.strip().length() != 0) {
      if (title.strip().equals("")) {
        title = "(無題)";
      }
      memoCRUDService.updateMemo(title, memo, memoType, memoId, loginUser.getId());
    } else {
      Memo reloadedMemo = memoCRUDService.findMemo(memoId);

      model.addAttribute("errorMessage", "本文は入力必須です");
      model.addAttribute("memo", reloadedMemo);
      model.addAttribute("loginUser", loginUser);

      return "crud/edit";
    }

    if (memoType.equals("draft")) {
      return "redirect:/create";
    }
    return "redirect:/top";
  }

  @PostMapping("/delete")
  public String delete(@AuthenticationPrincipal User loginUser, @RequestParam("id") int memoId, @RequestParam("memo_type") String memoType) {
    memoCRUDService.deleteMemo(memoId, loginUser.getId());
    if (memoType.equals("draft")) {
      return "redirect:/create";
    }
    return "redirect:/top";
  }

  @PostMapping("/search")
  public String search(@AuthenticationPrincipal User loginUser, RedirectAttributes redirectAttributes,
  @RequestParam("free_word") String freeWord, @RequestParam("from_date") String fromDate, @RequestParam("to_date") String toDate, @RequestParam("memo_type") String memoType) {
    if (freeWord.equals("") && fromDate.equals("") && toDate.equals("")) {
      redirectAttributes.addFlashAttribute("errorMessage", "検索条件が入力されていません");
      return "redirect:/search";
    } 
    // 検索条件を保持し、リダイレクト後のビューにて表示
    redirectAttributes.addFlashAttribute("free_word", freeWord);
    redirectAttributes.addFlashAttribute("from_date", fromDate);
    redirectAttributes.addFlashAttribute("to_date", toDate);
    redirectAttributes.addFlashAttribute("memo_type", memoType);

    // あいまい検索用
    String freeword = '%' + freeWord + '%'; 
    LocalDateTime fromdate = LDTFormatter.fromFormat(fromDate);
    LocalDateTime todate = LDTFormatter.toFormat(toDate);
    List<Memo> searchedList;

    if (memoType.equals("both")) {
      searchedList = memoCRUDService.searchMemoFromAll(loginUser.getId(), freeword, fromdate, todate);
      
    } else {
      searchedList = memoCRUDService.searchMemo(loginUser.getId(), freeword, memoType, fromdate, todate);
    }
    redirectAttributes.addFlashAttribute("searchedList", searchedList);

    return "redirect:/search";
  }

  @PostMapping("/contact")
  public String contact(@AuthenticationPrincipal User loginUser, RedirectAttributes redirectAttributes, @RequestParam String content) {
    String name = loginUser.getName();
    String mail = loginUser.getUsername();

    SimpleMailMessage msgForAdmin = CreateMail.createContactMail(name, mail, content);
    SimpleMailMessage msgForClient = CreateMail.createCopiedMail(name, mail, content);

    mailSender.send(msgForAdmin);
    mailSender.send(msgForClient);

    return "redirect:/contact";
  }

  
}
