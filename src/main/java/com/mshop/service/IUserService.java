package com.mshop.service;

import com.mshop.common.ServerResponse;
import com.mshop.po.Session;
import com.mshop.po.User;
import com.mshop.vo.UserSession;

import javax.servlet.http.HttpSession;

public interface IUserService {
    ServerResponse<UserSession> login(String username, String password, HttpSession session);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgotResetPassword(String username, String passwordNew, String forgotToken);

    ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user);

    ServerResponse<User> updateInformation(User user, Session session);

    ServerResponse checkAnimRole(User user);


}
