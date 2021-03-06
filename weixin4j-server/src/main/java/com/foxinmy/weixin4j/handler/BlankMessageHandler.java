package com.foxinmy.weixin4j.handler;

import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.request.WeixinRequest;
import com.foxinmy.weixin4j.response.BlankResponse;
import com.foxinmy.weixin4j.response.WeixinResponse;

/**
 * 空白响应消息处理器
 * 
 * @className BlankMessageHandler
 * @author jy
 * @date 2015年5月17日
 * @since JDK 1.7
 * @see
 */
public class BlankMessageHandler implements WeixinMessageHandler {

	public final static BlankMessageHandler global = new BlankMessageHandler();

	private BlankMessageHandler() {

	}

	@Override
	public boolean canHandle(WeixinRequest request, Object message)
			throws WeixinException {
		return true;
	}

	@Override
	public WeixinResponse doHandle(WeixinRequest request, Object message)
			throws WeixinException {
		return BlankResponse.global;
	}
}
