package com.mshop.service.impl;

import com.mshop.common.Const;
import com.mshop.common.ServerResponse;
import com.mshop.common.TokenCache;
import com.mshop.dao.SessionMapper;
import com.mshop.dao.UserMapper;
import com.mshop.po.Session;
import com.mshop.po.User;
import com.mshop.service.IUserService;
import com.mshop.utils.MD5Util;
import com.mshop.vo.UserSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SessionMapper sessionMapper;

    @Override
    public ServerResponse<UserSession> login(String username, String password, HttpSession reqSession) {
        int resultCode = userMapper.checkUserName(username);
        if (resultCode == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //TODO MD5加密
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        Session ssession = getSession(user);
        if (ssession == null) {
            Session session = new Session();
            session.setUserId(user.getId());
            session.setToken(reqSession.getId());
            sessionMapper.insert(session);
        } else {
            resultCode = sessionMapper.updateTokenByUserId(reqSession.getId(), user.getId());
        }

        Session ssessionss = getSession(user);
        UserSession userSession = new UserSession();
        userSession.setSession(ssessionss);
        userSession.setUser(user);

        return ServerResponse.createBySuccess("登录成功", userSession);
    }

    private Session getSession(User user) {
        return sessionMapper.selectByUserId(user.getId());
    }


    @Override
    public ServerResponse<String> register(User user) {

        ServerResponse<String> vailResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!vailResponse.isSuccess()) {
            return vailResponse;
        }

        vailResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!vailResponse.isSuccess()) {
            return vailResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCode = userMapper.insert(user);
        if (resultCode == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (type.equals(Const.USERNAME)) {
                int resultCode = userMapper.checkUserName(str);
                if (resultCode > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            } else if (type.equals(Const.EMAIL)) {
                int resultCode = userMapper.checkUserEmail(str);
                if (resultCode > 0) {
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccess("检验成功");
    }

    @Override
    public ServerResponse selectQuestion(String username) {
        ServerResponse<String> vailResponse = this.checkValid(username, Const.USERNAME);
        if (vailResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String question = userMapper.selectQuestionByUserName(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");

    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCode = userMapper.checkAnswer(username, question, answer);
        if (resultCode > 0) {
            String forgotToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgotToken);
            return ServerResponse.createBySuccess(forgotToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    @Override
    public ServerResponse<String> forgotResetPassword(String username, String passwordNew, String forgotToken) {
        if (StringUtils.isBlank(forgotToken)) {
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        ServerResponse<String> vailResponse = this.checkValid(username, Const.USERNAME);
        if (vailResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }

        if (StringUtils.equals(forgotToken, token)) {
            String md5passwordNew = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCode = userMapper.updatePasswordByUserName(username, md5passwordNew);
            if (resultCode > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("重置密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user) {
        int resultCode = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword), user.getId());
        if (resultCode == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        resultCode = userMapper.updateByPrimaryKeySelective(user);

        if (resultCode > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user, Session session) {
        int resultCode = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCode > 0) {
            return ServerResponse.createByErrorMessage("email已经存在，请更换email再尝试更新");
        }

        Session ssession = sessionMapper.selectByUserId(user.getId());
        if (!session.getToken().equals(ssession.getToken())) {
            return ServerResponse.createByErrorMessage("token已过期，请重新登录");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        resultCode = userMapper.updateByPrimaryKeySelective(user);
        if (resultCode > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public ServerResponse checkAnimRole(User user) {
        if (user == null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
