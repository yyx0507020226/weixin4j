package com.foxinmy.weixin4j.mp.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.alibaba.fastjson.TypeReference;
import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.http.weixin.WeixinResponse;
import com.foxinmy.weixin4j.model.Consts;
import com.foxinmy.weixin4j.model.WeixinAccount;
import com.foxinmy.weixin4j.mp.model.OauthToken;
import com.foxinmy.weixin4j.mp.model.User;
import com.foxinmy.weixin4j.util.ConfigUtil;

/**
 * oauth授权
 * 
 * @className OauthApi
 * @author jy
 * @date 2015年3月6日
 * @since JDK 1.7
 * @see <a
 *      href="https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&lang=zh_CN">微信登陆</a>
 */
public class OauthApi extends MpApi {
	/**
	 * @see {@link com.foxinmy.weixin4j.mp.api.OauthApi#getAuthorizeURL(String, String,String)}
	 * 
	 * @return
	 */
	public String getAuthorizeURL() {
		return getAuthorizeURL("state");
	}

	/**
	 * @see {@link com.foxinmy.weixin4j.mp.api.OauthApi#getAuthorizeURL(String, String,String)}
	 * 
	 * @return
	 */
	public String getAuthorizeURL(String state) {
		String appId = ConfigUtil.getWeixinAccount().getId();
		String redirectUri = ConfigUtil.getValue("redirect_uri");
		return getAuthorizeURL(appId, redirectUri, state);
	}

	/**
	 * 请求CODE
	 * 
	 * @param appId
	 *            应用ID
	 * @param redirectUri
	 *            重定向地址
	 * @param state
	 *            用于保持请求和回调的状态，授权请求后原样带回给第三方
	 * @return 请求的URL
	 */
	public String getAuthorizeURL(String appId, String redirectUri, String state) {
		String sns_user_auth_uri = getRequestUri("sns_user_auth_uri");
		try {
			return String.format(sns_user_auth_uri, appId,
					URLEncoder.encode(redirectUri, Consts.UTF_8.name()),
					"snsapi_login", state);
		} catch (UnsupportedEncodingException e) {
			;
		}
		return "";
	}

	/**
	 * @see {@link com.foxinmy.weixin4j.mp.api.OauthApi#getOauthToken(String, String,String)}
	 * 
	 * @return
	 */
	public OauthToken getOauthToken(String code) throws WeixinException {
		WeixinAccount account = ConfigUtil.getWeixinAccount();
		return getOauthToken(code, account.getId(), account.getSecret());
	}

	/**
	 * oauth授权code获取token
	 * 
	 * @param code
	 *            用户授权后返回的code
	 * @param appid
	 *            应用ID
	 * @param appsecret
	 *            应用密钥
	 * @return token对象
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.mp.model.OauthToken
	 */
	public OauthToken getOauthToken(String code, String appid, String appsecret)
			throws WeixinException {
		String user_token_uri = getRequestUri("sns_user_token_uri");
		WeixinResponse response = weixinClient.get(String.format(user_token_uri, appid,
				appsecret, code));

		return response.getAsObject(new TypeReference<OauthToken>() {
		});
	}

	/**
	 * @see {@link com.foxinmy.weixin4j.mp.api.OauthApi#getOauthToken(String, String,String)}
	 * 
	 * @return
	 */
	public OauthToken refreshToken(String refreshToken) throws WeixinException {
		WeixinAccount account = ConfigUtil.getWeixinAccount();
		return refreshToken(account.getId(), refreshToken);
	}

	/**
	 * 刷新token
	 * 
	 * @param appId
	 *            应用ID
	 * @param refreshToken
	 *            填写通过access_token获取到的refresh_token参数
	 * @return token对象
	 * @throws WeixinException
	 */
	public OauthToken refreshToken(String appId, String refreshToken)
			throws WeixinException {
		String sns_token_refresh_uri = getRequestUri("sns_token_refresh_uri");
		WeixinResponse response = weixinClient.get(String.format(sns_token_refresh_uri,
				appId, refreshToken));

		return response.getAsObject(new TypeReference<OauthToken>() {
		});
	}

	/**
	 * 验证access_token是否正确
	 * 
	 * @param accessToken
	 *            接口调用凭证
	 * @param openId
	 *            用户标识
	 * @return 验证结果
	 */
	public boolean authAccessToken(String accessToken, String openId) {
		String sns_auth_token_uri = getRequestUri("sns_auth_token_uri");
		try {
			weixinClient.get(String.format(sns_auth_token_uri, accessToken, openId));
			return true;
		} catch (WeixinException e) {
			;
		}
		return false;
	}

	/**
	 * oauth获取用户信息(需scope为 snsapi_userinfo)
	 * 
	 * @param token
	 *            授权票据
	 * @return 用户对象
	 * @throws WeixinException
	 * @see <a
	 *      href="http://mp.weixin.qq.com/wiki/17/c0f37d5704f0b64713d5d2c37b468d75.html">拉取用户信息</a>
	 * @see com.foxinmy.weixin4j.mp.model.User
	 * @see com.foxinmy.weixin4j.mp.model.OauthToken
	 * @see {@link com.foxinmy.weixin4j.mp.api.UserApi#getOauthToken(String)}
	 */
	public User getUser(OauthToken token) throws WeixinException {
		String user_info_uri = getRequestUri("sns_user_info_uri");
		WeixinResponse response = weixinClient.get(String.format(user_info_uri,
				token.getAccessToken(), token.getOpenid()));

		return response.getAsObject(new TypeReference<User>() {
		});
	}
}
