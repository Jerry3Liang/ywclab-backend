package com.jerryliang.ywclab.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jerryliang.ywclab.annotation.ActionLogs;
import com.jerryliang.ywclab.dto.createUser.CreateUserReq;
import com.jerryliang.ywclab.dto.createUser.UserReq;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

//    @Autowired
//    TicketService ticketService;
//
//    @ActionLogs(action = "創建新使用者")
//    @PostMapping("/createUser")
//    public String createUser(@RequestBody @Valid CreateUserReq createUserReq) throws JsonProcessingException {
//
//        //將 UserEnv 所需的資訊帶入方法中並傳回 ObjectNode 物件
//        //1. 擴增功能
//        ObjectNode envData = createUserReq.createUserNewEnvDataWithUserName(createUserReq.getExtraPermission());
//
//        //2. 帳號資料
//        CreateUserReq newCreateReq = CreateUserReq.parse(createUserReq, envData);
//        UserReq userReq = UserReq.parse(newCreateReq);
//
//        //3. 將所有相關 API 資訊帶入方法中並由 ticket 處理 UserEvn
//        ticketService.createUserIntoUserEnv(userReq, );
//    }
}
