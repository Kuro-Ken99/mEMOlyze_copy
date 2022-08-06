package com.memolyze.utility;

import java.util.List;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TargetSplit {

  public List<Token> splitToToken(List<String> memosList) {

    String memos = "";
    for (String memo : memosList) {
      memos += memo;
    }

    Tokenizer tokenizer = new Tokenizer();
    List<Token> tokens = tokenizer.tokenize(memos);

    return tokens;
  }
}
