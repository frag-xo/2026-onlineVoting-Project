package org.mjc.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.text.SimpleDateFormat;
import java.util.Date;


public class Log {
   /**
    * 日志id
    */
   private Integer id;

   /**
    * 当前操作人id
    */
   private String loginAccount;

   /**
    * 当前操作人ip
    */
   private String loginIp;

   /**
    *
    */
   private String actionUrl;

   /**
    *
    */
   private String module;

   /**
    * 执行的方�?
    */
   private String method;

   /**
    * 执行操作时间
    */
   private Long actionTime;

   /**
    * 描述
    */
   private String description;

   /**
    *
    */
   private Date gmtCreate;

   /**
    * 该操作状态，1表示成功�?1表示失败�?
    */



   private Short state = 1;

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getLoginAccount() {
      return loginAccount;
   }

   public void setLoginAccount(String loginAccount) {
      this.loginAccount = loginAccount;
   }

   public String getLoginIp() {
      return loginIp;
   }

   public void setLoginIp(String loginIp) {
      this.loginIp = loginIp;
   }

   public String getActionUrl() {
      return actionUrl;
   }

   public void setActionUrl(String actionUrl) {
      this.actionUrl = actionUrl;
   }

   public String getModule() {
      return module;
   }

   public void setModule(String module) {
      this.module = module;
   }

   public String getMethod() {
      return method;
   }

   public void setMethod(String method) {
      this.method = method;
   }

   public Long getActionTime() {
      return actionTime;
   }

   public void setActionTime(Long actionTime) {
      this.actionTime = actionTime;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Date getGmtCreate() {
      return gmtCreate;
   }

   public void setGmtCreate(Date gmtCreate) {
      this.gmtCreate = gmtCreate;
   }

   public Short getState() {
      return state;
   }

   public void setState(Short state) {
      this.state = state;
   }

   public String printfomat() throws JsonProcessingException {
      ObjectMapper objectMapper =  new ObjectMapper();
      objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
      return objectMapper.writeValueAsString(this);
   }

   //
   public String transferFormat(){
       StringBuffer stringBuffer = new StringBuffer();

      String transfDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//    stringBuffer.append("球员")
//          .append(player.getPName())
//          .append(" ")
//
//
      return stringBuffer.toString();

   }
}
