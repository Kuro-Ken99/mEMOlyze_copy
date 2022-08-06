package com.memolyze.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.memolyze.utility.FrequencyList;
import com.memolyze.utility.SentiScoreMap;
import com.memolyze.repository.MemoRepository;

@Service
public class AnalyticsService {

  @Autowired
  MemoRepository memoRepository;


  // 何が何回使われたかを格納するリスト。評価極性辞書は参照せず、kuromojiの辞書に格納されているか否かでフィルタリングされている。
  public List<Entry<String, Integer>> wordFrequency(int userId, LocalDateTime fromDate, LocalDateTime toDate) {

    List<String> memosList = memoRepository.findText(userId, fromDate, toDate);
    List<Entry<String, Integer>> memosFrequencyList = FrequencyList.frequencyList(memosList);

    return memosFrequencyList;
  }


  // 上記のリストをもとに、評価極性辞書に含まれていれば点数を増減させ、最終的なスコアを算出する。
  public List<Object> emotionAnalytics(int userId, LocalDateTime fromDate, LocalDateTime toDate) {
    List<String> memosList =  memoRepository.findText(userId, fromDate, toDate);

    List<Entry<String, Integer>> memosFrequencyList = FrequencyList.frequencyList(memosList);

    Map<String, Integer> sentiMap = SentiScoreMap.sentiMap();
    int sentiScore = 0;
    
    // 感情スコアの合計算出
    for (Entry<String, Integer> entry : memosFrequencyList) {
      if (sentiMap.containsKey(entry.getKey().substring(4))) {
        sentiScore += sentiMap.get(entry.getKey().substring(4)) * entry.getValue();
      }
    }
    
    // 感情スコアに最も影響を与えた単語と、その単語の合計スコアを析出
    String influentialWord = "";
    int topSentiScore = 0;
    for (Entry<String, Integer> entry : memosFrequencyList) {
      String entryKey = entry.getKey().substring(4);
      Integer entryValue = entry.getValue();

      if (sentiScore < 0 && sentiMap.containsKey(entryKey) && sentiMap.get(entryKey) < 0) {
        influentialWord = entryKey;
        topSentiScore = entryValue * sentiMap.get(entryKey);
        break;

      } else if (sentiScore > 0 && sentiMap.containsKey(entryKey) && sentiMap.get(entryKey) > 0) {
        influentialWord = entryKey;
        topSentiScore = entryValue * sentiMap.get(entryKey);
        break;
      }
    }

    List<Object> sentiScoreList = Arrays.asList(sentiScore, influentialWord, topSentiScore);
    
    return sentiScoreList;
  }
}
