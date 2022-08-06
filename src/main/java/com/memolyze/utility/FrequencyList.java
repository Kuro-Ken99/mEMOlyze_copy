package com.memolyze.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.atilika.kuromoji.ipadic.Token;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FrequencyList {
  public List<Entry<String, Integer>> frequencyList(List<String> memosList) {

    List<Token> tokens = TargetSplit.splitToToken(memosList);
    Map<String, Integer> bowMap = new HashMap<>();
    Map<String, Integer> sentiMap = SentiScoreMap.sentiMap();
    // 除外ワード
    List<String> stopList = new ArrayList<String>(Arrays.asList("する", "れる", "いる", "なる", "こと", "てき", "ある", "よう", "の", "られる", "もの", "ない", "てる", "ん", "さん", "様"));

    for (Token token : tokens) {
      // 品詞の抽出
      String pos = token.getAllFeaturesArray()[0];

      // 語幹あるいは表層を取得。単体で意味が判別しやすい品詞のみを分析対象とする。
      if (pos.equals("名詞") || pos.equals("動詞") || pos.equals("形容詞") || pos.equals("副詞")) {
        String baseForm = token.getBaseForm();
        if (baseForm == null || baseForm.trim().length() == 0) {
          baseForm = token.getSurface();
        }
        if (stopList.contains(baseForm)) {
          continue;
        }
        int count = 1;

        if (bowMap.containsKey(baseForm)) {
          count += bowMap.get(baseForm);
        }
        bowMap.put(baseForm, count);
      }
    }
    
    // 「*」が分析対象の単語として紛れ込んでしまうので、ここで削除
    bowMap.remove("*");

    Map<String, Integer> newBowMap = new HashMap<>();

    // ビューに渡した際のスタイリングがしやすくなるよう、sentiScoreに対応したステータスを付与
    for (String key : bowMap.keySet()) {
      String newKey = "";
      Integer value = bowMap.get(key);
      if (sentiMap.containsKey(key)) {
        if (sentiMap.get(key) == 1) {
          newKey = "posi" + key;
        } else if (sentiMap.get(key) == -1) {
          newKey = "nega" + key;
        } else {
          newKey = "neut" + key;
        }
      } else {
        newKey = "null" + key;
      }
      newBowMap.put(newKey, value);
    }

    List<Entry<String, Integer>> memosFrequencyList = new ArrayList<Entry<String, Integer>>(newBowMap.entrySet());

    Collections.sort(memosFrequencyList, new Comparator<Entry<String, Integer>>() {
      public int compare(Entry<String, Integer> obj1, Entry<String, Integer> obj2) {
        return obj2.getValue().compareTo(obj1.getValue());
      }
    });
    return memosFrequencyList;
  }
}