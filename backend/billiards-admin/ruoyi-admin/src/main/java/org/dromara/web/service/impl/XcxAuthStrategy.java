package org.dromara.web.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.request.AuthWechatMiniProgramRequest;
import org.apache.commons.lang3.StringUtils;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.billiards.domain.entity.BlsUser;
import org.dromara.billiards.service.IBlsWalletAccountService;
import org.dromara.billiards.service.UserService;
import org.dromara.common.core.domain.model.XcxLoginBody;
import org.dromara.common.core.domain.model.XcxLoginUser;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.domain.vo.SysClientVo;
import org.dromara.web.domain.vo.LoginVo;
import org.dromara.web.service.IAuthStrategy;
import org.dromara.web.service.SysLoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 小程序认证策略
 *
 * @author Michelle.Chung
 */
@Slf4j
@Service("xcx" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class XcxAuthStrategy implements IAuthStrategy {

    private final SysLoginService loginService;

    private final UserService billiardsUserService;

    private final IBlsWalletAccountService walletAccountService;

    @Value("${billiards.wechat.appid}")
    private String appid;

    @Value("${billiards.wechat.secret}")
    private String secret;

    @Override
    public LoginVo login(String body, SysClientVo client) {
        XcxLoginBody loginBody = JsonUtils.parseObject(body, XcxLoginBody.class);
        ValidatorUtils.validate(loginBody);
        // 校验 appid + appsrcret + xcxCode 调用登录凭证校验接口 获取 session_key 与 openid
        AuthRequest authRequest = new AuthWechatMiniProgramRequest(AuthConfig.builder()
            .clientId(appid).clientSecret(secret)
            .ignoreCheckRedirectUri(true).ignoreCheckState(true).build());
        AuthCallback authCallback = new AuthCallback();
        authCallback.setCode(loginBody.getXcxCode());
        AuthResponse<AuthUser> resp = authRequest.login(authCallback);
        String openid, unionId;
        if (resp.ok()) {
            AuthToken token = resp.getData().getToken();
            openid = token.getOpenId();
            // 微信小程序只有关联到微信开放平台下之后才能获取到 unionId，因此unionId不一定能返回。
            unionId = token.getUnionId();
        } else {
            throw new ServiceException(resp.getMsg());
        }
        // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
        BlsUser blsUser = billiardsUserService.getByOpenid(openid);
        boolean isNewUser = false;
        if(blsUser == null){
            // 构建用户保存的用户信息
            blsUser = new BlsUser();
            blsUser.setOpenid(openid);
            blsUser.setNickname(loginBody.getNickname());
            blsUser.setUserName(loginBody.getNickname());
            blsUser.setAvatarUrl(loginBody.getAvatarUrl());
            blsUser.setGender(loginBody.getGender());
            blsUser.setIsMember(0);
            blsUser.setMemberLevel(0);
            blsUser.setPoints(0);
            blsUser.setStatus(0);
            blsUser.setLastLoginTime(LocalDateTime.now());
            if (!billiardsUserService.save(blsUser)) {
                throw BilliardsException.of(ResultCode.ERROR, "创建用户失败");
            }
            isNewUser = true;
        }
        // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
        XcxLoginUser loginUser = new XcxLoginUser();
        loginUser.setUserId(blsUser.getId());
        loginUser.setUsername(blsUser.getUserName());
        loginUser.setNickname(blsUser.getNickname());
        loginUser.setUserType(UserType.XCX_USER.getUserType());
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());
        loginUser.setOpenid(openid);

        SaLoginModel model = new SaLoginModel();
        model.setDevice(client.getDeviceType());
        // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
        // 例如: 后台用户30分钟过期 app用户1天过期
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, client.getClientId());
        // 生成token
        LoginHelper.login(loginUser, model);

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(client.getClientId());
        loginVo.setOpenid(openid);
        loginVo.setNickname(blsUser.getNickname());
        loginVo.setAvatarUrl(blsUser.getAvatarUrl());
        loginVo.setToken(StpUtil.getTokenValue());
        loginVo.setIsNewUser(isNewUser);
        loginVo.setIsMember(blsUser.getIsMember() != null && blsUser.getIsMember() == 1);
        loginVo.setMemberLevel(blsUser.getMemberLevel());
        loginVo.setIsPhoneBound(StringUtils.isNotBlank(blsUser.getPhone()));
        return loginVo;
    }
}
