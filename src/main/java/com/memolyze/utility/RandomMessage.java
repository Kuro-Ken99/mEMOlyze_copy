package com.memolyze.utility;

import java.util.Random;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RandomMessage {

  public String randomMessage(int sentiScore) {

    Random rand = new Random();
    int num = rand.nextInt(3);
    String message = "";

    if (sentiScore > 0) {
      switch (num) {
        case 0:
          message = "元気いっぱいにすごせているみたいですね！私も嬉しいです♪";
          break;
        case 1:
          message = "前向きなあなたが、素敵です♪";
          break;
        case 2:
          message = "これからも前向きなあなたを応援しています！";
          break;
      }
    } else if (sentiScore == 0) {
      message = "まだ分析できるほどメモが溜まっていないみたい、、、どしどしメモしちゃってくださいね！";
    } else {
      switch (num) {
        case 0:
          message = "お疲れのようですね。無理せず、ゆっくりいきましょう♪";
          break;
        case 1:
          message = "ちょっとストレスが溜まっているかも？たまには自分を甘やかすのもアリですね♪";
          break;
        case 2:
          message = "元気が出ない時、ありますよね…私はあなたの味方ですよっ";
          break;
      }
    }
    return message;
  }
}
