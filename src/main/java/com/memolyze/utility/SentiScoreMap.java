package com.memolyze.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SentiScoreMap {

  public Map<String, Integer> sentiMap() {
    Map<String, Integer> sentiMap = new HashMap<String, Integer>();

    // 評価局生辞書の読み込み
    try (BufferedReader br = Files.newBufferedReader(Paths.get("src/main/resources/static/files/pn.csv.m3.120408.trim"))) {
      String str = br.readLine();

      while (str != null) {
        String[] split = str.split("\t");
        if (split.length > 1) {
          String emotion = split[1].trim();
          int sentiScore = 0;
          if (emotion.equals("p")) {
            sentiScore = 1;
          } else if (emotion.equals("n")) {
            sentiScore = -1;
          }
          sentiMap.put(split[0].trim(), sentiScore);
        }
        str = br.readLine();
      }
      br.close();
		} catch (IOException e) {
      e.printStackTrace();	
		}
    return sentiMap;
  }
}