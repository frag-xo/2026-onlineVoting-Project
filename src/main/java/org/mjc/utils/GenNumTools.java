package org.mjc.utils;

import cn.hutool.core.util.NumberUtil;

public class GenNumTools {
    /**
     *
     * @param beginHead
     * @param size
     * @param str
     * @return
     */
    public static String initId(String beginHead,int size,String  str){
        int len = str.length();//
        String processStr = str.substring(beginHead.length(),len);
        int result = -1;
        int zeroBeginIndex = 0;
        int processStrLen = processStr.length();
        String processResult = "error";

       if(NumberUtil.isNumber(processStr)) {
           for (int i = 0; i < processStrLen; i++) {
               char chr = processStr.charAt(i);
               if (chr == '0') {
                   zeroBeginIndex++;
               } else {
                   break;
               }
           }
           result = Integer.parseInt(processStr.substring(zeroBeginIndex,processStrLen));
           processResult =  beginHead+ addZero(size,result + 1);
       }

        System.out.println(processResult);
        return processResult;
    }

    /**
     * 特定字符串长度的  整数补零
     * @param size
     * @param val
     * @return
     */
    public static String addZero(int size,int val){
        String result =  String.valueOf(val);
        int len = String.valueOf(val).length();
        if(size > len){
            String zeroStr = "";
            for(int i=0;i<size-len;i++){
                zeroStr += "0";
            }
            result  =zeroStr + result;
        }
       // System.out.println(result);
        return result;
    }

    /**
     *
     * @param beginHead
     * @param firstStringNum
     * @param secondStringNum
     * @return
     */
    public static Boolean compareStringNum(String beginHead,String  firstStringNum,String secondStringNum){
        int length = beginHead.length();
        firstStringNum = firstStringNum.substring(length);
        secondStringNum = secondStringNum.substring(length);
        int firstNum = Integer.parseInt(firstStringNum.substring(calcZore(firstStringNum)));
        int secondNum = Integer.parseInt(secondStringNum.substring(calcZore(secondStringNum)));
        return secondNum<firstNum?true:false;//
    }
    public static int calcZore(String str){
        int beginZoreNum = 0;//
        while(str.substring(0,1).equals("0")){//
            str = str.substring(1);//
            beginZoreNum++;
        }
        return beginZoreNum;
    }
    public static void main(String[] args) {
        //GenNumTools.initId("yjs",10,"yjs000401547");
        //System.out.println(calcZore("00015"));
        System.out.println(compareStringNum("P","P00015","P00017"));
    }
}
