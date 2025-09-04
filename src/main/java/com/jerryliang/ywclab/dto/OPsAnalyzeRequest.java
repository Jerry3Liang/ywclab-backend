package com.jerryliang.ywclab.dto;

import lombok.Data;

import java.util.List;

@Data
public class OPsAnalyzeRequest {

   private String expGroupName;
   private List<Double> rightEyeRawData;
   private List<Double> leftEyeRawData;
   private List<Double> milliSec;
   private String rightMinPointSelected;
   private String leftMinPointSelected;
}
