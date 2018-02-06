package com.mshop.controller.portal;


import com.mshop.common.Const;
import com.mshop.common.ServerResponse;
import com.mshop.po.Session;
import com.mshop.po.User;
import com.mshop.service.IUserService;
import com.mshop.vo.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;


    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<UserSession> login(@RequestBody User user, HttpSession session) {
        ServerResponse<UserSession> response = iUserService.login(user.getUsername(), user.getPassword(), session);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(@RequestBody User user) {
        return iUserService.register(user);
    }

    @RequestMapping(value = "checkValid", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    @RequestMapping(value = "getUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
    }

    @RequestMapping(value = "forgetPassword", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetPassword(String username) {
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value = "checkAnswer", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    @RequestMapping(value = "forgotResetPassword", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgotResetPassword(String username, String passwordNew, String forgotToken) {
        return iUserService.forgotResetPassword(username, passwordNew, forgotToken);
    }

    @RequestMapping(value = "resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld, passwordNew, user);
    }

    @RequestMapping(value = "updateInformation", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(@RequestPart("json") User user, @RequestPart("session") Session session) {
        ServerResponse<User> response = iUserService.updateInformation(user, session);
        if (response.isSuccess()) {
            response.getData().setUsername(user.getUsername());
        }
        return response;
    }
}
