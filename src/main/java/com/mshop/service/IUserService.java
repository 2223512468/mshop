package com.mshop.service;

import com.mshop.common.ServerResponse;
import com.mshop.po.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgotResetPassword(String username, String passwordNew, String forgotToken);

    ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse checkAnimRole(User user);

}
