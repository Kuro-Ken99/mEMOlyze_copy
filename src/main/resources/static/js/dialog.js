'use strict';

// メモ作成、一覧画面での削除アラート
$('.delete_button').click(function() {
  const CONFIRM_MESSAGE = "本当に削除してよろしいですか？"
  if(!confirm(CONFIRM_MESSAGE)) {
    return false;
  }
});

// 感情分析結果の開閉
$('.open').click(function() {
  $('.open').addClass('hidden');
  $('.close').removeClass('hidden');
  $('#sixth_or_later').removeClass('hidden');
});

$('.close').click(function() {
  $('#sixth_or_later').addClass('hidden');
  $('.close').addClass('hidden');
  $('.open').removeClass('hidden');
});

// 登録情報変更の開閉
$('.change').click(function() {
  var $form = $(this).closest('.info_box').find('#info_form');
  $form.toggleClass('hidden');
});

// faqの開閉
$('.question').click(function() {
  var $answer = $(this).nextAll();
  if($answer.css('display') == 'block') {
    $answer.slideUp('slow');
  } else {
    $answer.slideDown('slow');
  }
});

// 説明文の開閉
$('.show').click(function() {
  var $content = $(this).next();
  if($content.css('display') == 'block') {
    $content.slideUp('slow');
  } else {
    $content.slideDown('slow');
  }
});

// modalのshow/hide
$('.reference').click(function() {
  $('.modal').fadeIn();
});

$('.modal').click(function() {
  if($('.modal').css('display') == 'block') {
    $('.modal').fadeOut();
  }
})