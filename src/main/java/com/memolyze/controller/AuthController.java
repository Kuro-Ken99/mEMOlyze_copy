package com.memolyze.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.memolyze.entity.Reissue;
import com.memolyze.entity.TmpUser;
import com.memolyze.entity.User;
import com.memolyze.form.UserForm;
import com.memolyze.service.PasswordReissueService;
import com.memolyze.service.UserService;
import com.memolyze.utility.CreateMail;
import com.memolyze.utility.UpdateUserContext;

@Controller
public class AuthController {

  @Autowired
  UserService userService;
  @Autowired
  PasswordReissueService passwordReissueService;
  @Autowired
  MailSender mailSender;
  @Autowired
  PasswordEncoder passwordEncoder;

  @GetMapping("/login")
  public String login(Model model, @RequestParam(value = "error", required = false) String error) {
    if (error != null) {
      model.addAttribute("error", "メールアドレスまたはパスワードに誤りがあります");
    }
    return "auth/login";
  }

  @GetMapping("/register")
  public String register() {
    return "auth/register";
  }

  @PostMapping("/register")
  public String register(RedirectAttributes redirectAttributes, @Validated UserForm userForm,
      BindingResult bindingResult) {
    String name = userForm.getName().strip();
    String mail = userForm.getMail();
    String password = userForm.getPassword();
    boolean userExists = userService.userExists(mail);
    boolean errorExists = false;

    // デフォルトのバリデーション（全角空白は弾かれず）
    if (bindingResult.hasErrors()) {
      List<String> errorList = new ArrayList<String>();
      for (ObjectError error : bindingResult.getAllErrors()) {
        errorList.add(error.getDefaultMessage());
      }
      errorExists = true;
      redirectAttributes.addFlashAttribute("errorList", errorList);
    }
    // 全角空白用のバリデーション
    if (name.equals("")) {
      errorExists = true;
      redirectAttributes.addFlashAttribute("error", "name: 空白のみでは登録できません");
    }
    // 登録済みのときのバリデーション
    if (userExists) {
      errorExists = true;
      redirectAttributes.addFlashAttribute("error", "mail: このメールアドレスは既に使われています");
    }

    if (errorExists) {
      return "redirect:/register";
    }

    String uuid = UUID.randomUUID().toString();

    try {
      // 同一アドレスで仮登録が重複していた場合、古いほうの削除を同時に行う
      userService.saveTmpUser(name, mail, password, uuid);
      SimpleMailMessage msg = CreateMail.createMail(name, mail, uuid);
      mailSender.send(msg);
      redirectAttributes.addFlashAttribute("mailSentAnnounce", "本登録用メールを送信しました。メール内のリンクより、登録を完了させてください。");
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "redirect:/register";
  }

  @GetMapping("/validate")
  public String validate(RedirectAttributes redirectAttributes, @RequestParam("id") String uuid) {

    if (userService.tmpUserExists(uuid)) {
      if (userService.tmpUserExistsInUsers(uuid)) {
        redirectAttributes.addFlashAttribute("error", "既に登録が完了しているユーザーです。上記よりログインしてください。");
      } else {
        try {
          TmpUser tmpUser = userService.findTmpUserByUuid(uuid);
          // 登録と同時にtmpUserを削除
          userService.saveRegisteredUser(uuid);
          SimpleMailMessage msg = CreateMail.registerDoneMail(tmpUser.getName(), tmpUser.getUsername());
          mailSender.send(msg);

          redirectAttributes.addFlashAttribute("isRegistered", "会員登録が完了しました。上記フォームよりログインしてください。");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } else {
      // 既に登録が完了している、リンクに誤りがある、最新のリンクでない、のいずれか
      redirectAttributes.addFlashAttribute("registerError", true);
    }
    return "redirect:/login";
  }

  @PostMapping("/account_delete")
  public String accontDelete(@AuthenticationPrincipal User loginUser, @RequestParam String opinion) {
    String name = loginUser.getName();
    String mail = loginUser.getUsername();
    
    try {
      userService.accountDelete(loginUser.getId());
  
      SimpleMailMessage msg = CreateMail.thanksMail(name, mail);
      SimpleMailMessage deleteAnnounce = CreateMail.deleteAnnounce(name, opinion);
      mailSender.send(msg);
      mailSender.send(deleteAnnounce);
    } catch (Exception e) {
      e.printStackTrace();
    }

    SecurityContextHolder.clearContext();
    return "redirect:/";
  }

  @PostMapping("/resend")
  public String resend(RedirectAttributes redirectAttributes, @RequestParam String mail) {

    if (userService.tmpUserExistsByUsername(mail)) {
      TmpUser tmpUser = userService.findTmpUserByUsername(mail);
      String name = tmpUser.getName();
      String username = tmpUser.getUsername();

      String uuid = tmpUser.getUUID();

      SimpleMailMessage msg = CreateMail.createMail(name, username, uuid);
      mailSender.send(msg);
      redirectAttributes.addFlashAttribute("mailSentAnnounce", "本登録用メールを再送しました。メール内のリンクより、登録を完了させてください。");
    } else {

      redirectAttributes.addFlashAttribute("error", "メールアドレスが間違っています");

      return "redirect:/login";
    }

    return "redirect:/register";
  }

  @PostMapping("/name_update")
  public String nameUpdate(RedirectAttributes redirectAttributes, @AuthenticationPrincipal User loginUser, @RequestParam String name) {
    String newName = name.strip();
    int userId = loginUser.getId();

    if (newName.equals("")) {
      redirectAttributes.addFlashAttribute("errorMessage", "name: 空白のみでは登録できません");
      redirectAttributes.addFlashAttribute("nameError", true);

      return "redirect:/mypage";
    }
    userService.updateName(newName, userId);
    redirectAttributes.addFlashAttribute("doneAnnounce", "name: 名前の変更が完了しました");

    // セッション更新
    User user = new User(userId, name, loginUser.getUsername(), loginUser.getPassword());
    UpdateUserContext.updateSecurityContext(user);
    return "redirect:/mypage";
  }

  @PostMapping("/mail_update")
  public String mailUpdate(RedirectAttributes redirectAttributes, @AuthenticationPrincipal User loginUser, @RequestParam String mail) {
    String newMail = mail.strip();
    boolean mailError = false;
    if (newMail.equals("")) {
      redirectAttributes.addFlashAttribute("errorMessage", "mail: 空白のみでは登録できません");
      mailError = true;
    }

    if (userService.userExists(newMail)) {
      redirectAttributes.addFlashAttribute("errorMessage", "mail: このメールアドレスは既に使われています");
      mailError = true;
    }

    if (newMail.equals(loginUser.getUsername())) {
      redirectAttributes.addFlashAttribute("errorMessage", "mail: 現在ご登録されているメールアドレスと同一です");
      mailError = true;
    }

    if (mailError) {
      redirectAttributes.addFlashAttribute("mailError", mailError);
      return "redirect:/mypage";
    }

    int userId = loginUser.getId();
    if (userService.duplicateTmpUserCheck(userId)) {
      // tmpUserは複数残したくないので、都度消去
      userService.duplicateTmpUserDelete(userId);
    }
    String uuid = UUID.randomUUID().toString();

    userService.saveTmpUser(loginUser.getName(), newMail, loginUser.getPassword(), uuid, userId);

    try {
      SimpleMailMessage msg = CreateMail.createUpdateMail(loginUser.getName(), newMail, uuid);

      mailSender.send(msg);
      redirectAttributes.addFlashAttribute("doneAnnounce", "アドレス変更用メールを送信しました。メール内のリンクより、登録を完了させてください。");
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "redirect:/mypage";
  }

  @GetMapping("/address_update")
  public String addressUpdate(RedirectAttributes redirectAttributes, @RequestParam("id") String uuid) {

    // ページ遷移先分岐に必要な認証情報を確認
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean isAuthenticated = auth.isAuthenticated();

    if (userService.tmpUserExists(uuid)) {
      TmpUser tmpUser = userService.findTmpUserByUuid(uuid);
      userService.updateMail(uuid);

      redirectAttributes.addFlashAttribute("mailUpdated", true);

      // 認証されていて、かつid一致→認証されているがid不一致→認証されていない
      if (isAuthenticated) {
        User authUser = (User) auth.getPrincipal();
        if (authUser.getId() == tmpUser.getUserId()) {
          User user = new User(authUser.getId(), authUser.getName(), tmpUser.getUsername(), authUser.getPassword());
          UpdateUserContext.updateSecurityContext(user);
          return "redirect:/mypage";
        } else {
          // 別アカウントでログインしている場合
          SecurityContextHolder.clearContext();
          return "redirect:/login";
        }
      } 
      // どのアカウントでもログインしていない場合
      return "redirect:/login";

    } else {
      // リンクミス、別のメールで更新処理中、既に更新が完了している、のいづれか。長文のため、ビュー側でエラーメッセージを書く。

      redirectAttributes.addFlashAttribute("validateError", true);

      if (isAuthenticated) {
        SecurityContextHolder.clearContext();
      }
    }
    return "redirect:/login";
  }

  @PostMapping("/pass_update")
  public String passUpdate(RedirectAttributes redirectAttributes, @AuthenticationPrincipal User loginUser, @RequestParam("current_password") String currentPassword, @RequestParam("new_pass") String newPass, @RequestParam("renew_pass") String renewPass) {

    if (!passwordEncoder.matches(currentPassword, loginUser.getPassword())) {
      redirectAttributes.addFlashAttribute("errorMessage", "password: 現在のパスワードが一致していません");
      redirectAttributes.addFlashAttribute("passError", true);
      return "redirect:/mypage";
    }
    if (!newPass.equals(renewPass)) {
      redirectAttributes.addFlashAttribute("errorMessage", "password: 新しいパスワードが一致していません");
      redirectAttributes.addFlashAttribute("passError", true);
      return "redirect:/mypage";
    }

    String password = passwordEncoder.encode(newPass);
    userService.updatePassword(password, loginUser.getId());

    // セッションの更新
    User user = new User(loginUser.getId(), loginUser.getName(), loginUser.getUsername(), password);
    UpdateUserContext.updateSecurityContext(user);

    redirectAttributes.addFlashAttribute("doneAnnounce", "パスワードの変更が完了しました");

    return "redirect:/mypage";
  }

  @PostMapping("/pass_reissue")
  public String passRewrite(@RequestParam String mail, RedirectAttributes redirectAttributes) {
    String username = mail.strip();
    if (username.equals("")) {
      redirectAttributes.addFlashAttribute("error", "メールアドレスを入力して下さい");
    }
    String rawSecret = passwordReissueService.createAndSendReissueInfo(mail);

    redirectAttributes.addFlashAttribute("rawSecret", rawSecret);
    return "redirect:/login";
  }

  @GetMapping("/pass_validate")
  public String passRewrite(Model model, RedirectAttributes redirectAttributes, @RequestParam("id") String token) {

    Reissue reissue = passwordReissueService.findOne(token);
    if (reissue == null || LocalDateTime.now().isAfter(reissue.getExpiryDate())) {
      redirectAttributes.addFlashAttribute("error", "リンクの時間切れ、またはリンクに誤りがあります。再発行メールを再度受信してください。");
      return "redirect:/login";
    }
    model.addAttribute("token", token);
    model.addAttribute("username", reissue.getUsername());

    return "auth/pass_validate";
  }

  @PostMapping("/pass_validate")
  public String passValidate(RedirectAttributes redirectAttributes, Model model, @RequestParam String secret,
      @RequestParam("new_pass") String newPass, @RequestParam("renew_pass") String renewPass,
      @RequestParam String token) {
    if (!newPass.equals(renewPass)) {
      model.addAttribute("error", "password: 新しいパスワードが一致していません");
      model.addAttribute("token", token);
      return "auth/pass_validate";
    }

    boolean updatePass = passwordReissueService.updatePass(token, secret, newPass);

    if (updatePass) {
      SecurityContextHolder.clearContext();
      redirectAttributes.addFlashAttribute("isRegistered", "再設定完了しました、ログインしてください");
    } else {
      redirectAttributes.addFlashAttribute("reissueError", true);
    }
    return "redirect:/login";
  }
}