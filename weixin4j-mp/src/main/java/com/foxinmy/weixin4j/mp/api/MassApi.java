package com.foxinmy.weixin4j.mp.api;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.http.weixin.JsonResult;
import com.foxinmy.weixin4j.http.weixin.WeixinResponse;
import com.foxinmy.weixin4j.model.Token;
import com.foxinmy.weixin4j.token.TokenHolder;
import com.foxinmy.weixin4j.tuple.MassTuple;
import com.foxinmy.weixin4j.tuple.MpArticle;
import com.foxinmy.weixin4j.tuple.MpNews;
import com.foxinmy.weixin4j.tuple.Tuple;
import com.foxinmy.weixin4j.tuple.Video;
import com.foxinmy.weixin4j.util.StringUtil;

/**
 * 群发相关API
 * 
 * @className MassApi
 * @author jy.hu
 * @date 2014年9月25日
 * @since JDK 1.7
 * @see <a
 *      href="http://mp.weixin.qq.com/wiki/15/5380a4e6f02f2ffdc7981a8ed7a40753.html">群发接口</a>
 */
public class MassApi extends MpApi {

	private final TokenHolder tokenHolder;

	public MassApi(TokenHolder tokenHolder) {
		this.tokenHolder = tokenHolder;
	}

	/**
	 * 上传图文消息,一个图文消息支持1到10条图文</br> <font
	 * color="red">具备微信支付权限的公众号，在使用高级群发接口上传、群发图文消息类型时，可使用&lta&gt标签加入外链</font>
	 * 
	 * @param articles
	 *            图片消息
	 * @return 媒体ID
	 * @throws WeixinException
	 * @see <a
	 *      href="http://mp.weixin.qq.com/wiki/15/5380a4e6f02f2ffdc7981a8ed7a40753.html#.E4.B8.8A.E4.BC.A0.E5.9B.BE.E6.96.87.E6.B6.88.E6.81.AF.E7.B4.A0.E6.9D.90.E3.80.90.E8.AE.A2.E9.98.85.E5.8F.B7.E4.B8.8E.E6.9C.8D.E5.8A.A1.E5.8F.B7.E8.AE.A4.E8.AF.81.E5.90.8E.E5.9D.87.E5.8F.AF.E7.94.A8.E3.80.91">上传图文素材</a>
	 * @see com.foxinmy.weixin4j.tuple.MpArticle
	 */
	public String uploadArticle(List<MpArticle> articles)
			throws WeixinException {
		String article_upload_uri = getRequestUri("article_upload_uri");
		Token token = tokenHolder.getToken();
		JSONObject obj = new JSONObject();
		obj.put("articles", articles);
		WeixinResponse response = weixinClient.post(
				String.format(article_upload_uri, token.getAccessToken()),
				obj.toJSONString());

		return response.getAsJson().getString("media_id");
	}

	/**
	 * 上传分组群发的视频素材
	 * 
	 * @param video
	 *            视频对象 其中mediaId媒体文件中上传得到的Id 不能为空
	 * @return 上传后的ID 可用于群发视频消息
	 * @throws WeixinException
	 * @see <a
	 *      href="http://mp.weixin.qq.com/wiki/15/5380a4e6f02f2ffdc7981a8ed7a40753.html">高级群发</a>
	 * @see com.foxinmy.weixin4j.tuple.Video
	 * @see com.foxinmy.weixin4j.tuple.MpVideo
	 * @see {@link com.foxinmy.weixin4j.mp.api.MediaApi#uploadMedia(File)}
	 */
	public String uploadVideo(Video video) throws WeixinException {
		String video_upload_uri = getRequestUri("video_upload_uri");
		Token token = tokenHolder.getToken();
		WeixinResponse response = weixinClient.post(
				String.format(video_upload_uri, token.getAccessToken()),
				JSON.toJSONString(video));

		return response.getAsJson().getString("media_id");
	}

	/**
	 * 分组群发
	 * 
	 * @param tuple
	 *            消息元件
	 * @param groupId
	 *            分组ID
	 * @return 群发后的消息ID
	 * @see {@link com.foxinmy.weixin4j.mp.api.MassApi#massMessage(MassTuple,boolean,int)}
	 * @throws WeixinException
	 */
	public String massByGroupId(MassTuple tuple, int groupId)
			throws WeixinException {
		return massMessage(tuple, false, groupId);
	}

	/**
	 * 群发消息
	 * <p>
	 * 在返回成功时,意味着群发任务提交成功,并不意味着此时群发已经结束,所以,仍有可能在后续的发送过程中出现异常情况导致用户未收到消息,
	 * 如消息有时会进行审核、服务器不稳定等,此外,群发任务一般需要较长的时间才能全部发送完毕
	 * </p>
	 * 
	 * @param tuple
	 *            消息元件
	 * @param isToAll
	 *            用于设定是否向全部用户发送，值为true或false，选择true该消息群发给所有用户，
	 *            选择false可根据group_id发送给指定群组的用户
	 * @param groupId
	 *            分组ID
	 * @return 群发后的消息ID
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.mp.model.Group
	 * @see com.foxinmy.weixin4j.tuple.Text
	 * @see com.foxinmy.weixin4j.tuple.Image
	 * @see com.foxinmy.weixin4j.tuple.Voice
	 * @see com.foxinmy.weixin4j.tuple.MpVideo
	 * @see com.foxinmy.weixin4j.tuple.MpNews
	 * @see com.foxinmy.weixin4j.tuple.MassTuple
	 * @see {@link com.foxinmy.weixin4j.mp.api.GroupApi#getGroups()}
	 * @see <a
	 *      href="http://mp.weixin.qq.com/wiki/15/5380a4e6f02f2ffdc7981a8ed7a40753.html#.E6.A0.B9.E6.8D.AE.E5.88.86.E7.BB.84.E8.BF.9B.E8.A1.8C.E7.BE.A4.E5.8F.91.E3.80.90.E8.AE.A2.E9.98.85.E5.8F.B7.E4.B8.8E.E6.9C.8D.E5.8A.A1.E5.8F.B7.E8.AE.A4.E8.AF.81.E5.90.8E.E5.9D.87.E5.8F.AF.E7.94.A8.E3.80.91">根据分组群发</a>
	 */
	public String massMessage(MassTuple tuple, boolean isToAll, int groupId)
			throws WeixinException {
		if (tuple instanceof MpNews) {
			MpNews _news = (MpNews) tuple;
			List<MpArticle> _articles = _news.getArticles();
			if (StringUtil.isBlank(_news.getMediaId())
					&& (_articles == null || _articles.isEmpty())) {
				throw new WeixinException(
						"mass fail:mediaId or articles is required");
			}
			tuple = new MpNews(uploadArticle(_articles));
		}
		String msgtype = tuple.getMessageType();
		JSONObject obj = new JSONObject();
		JSONObject item = new JSONObject();
		item.put("is_to_all", isToAll);
		if (!isToAll) {
			item.put("group_id", groupId);
		}
		obj.put("filter", item);
		obj.put(msgtype, JSON.toJSON(tuple));
		obj.put("msgtype", msgtype);
		String mass_group_uri = getRequestUri("mass_group_uri");
		Token token = tokenHolder.getToken();
		WeixinResponse response = weixinClient.post(
				String.format(mass_group_uri, token.getAccessToken()),
				obj.toJSONString());

		return response.getAsJson().getString("msg_id");
	}

	/**
	 * 分组ID群发图文消息
	 * 
	 * @param articles
	 *            图文列表
	 * @param groupId
	 *            分组ID
	 * @return 群发后的消息ID
	 * @see <a
	 *      href="http://mp.weixin.qq.com/wiki/15/5380a4e6f02f2ffdc7981a8ed7a40753.html#.E6.A0.B9.E6.8D.AE.E5.88.86.E7.BB.84.E8.BF.9B.E8.A1.8C.E7.BE.A4.E5.8F.91.E3.80.90.E8.AE.A2.E9.98.85.E5.8F.B7.E4.B8.8E.E6.9C.8D.E5.8A.A1.E5.8F.B7.E8.AE.A4.E8.AF.81.E5.90.8E.E5.9D.87.E5.8F.AF.E7.94.A8.E3.80.91">根据分组群发</a>
	 * @see {@link com.foxinmy.weixin4j.mp.api.MassApi#massByGroupId(Tuple,int)}
	 * @see com.foxinmy.weixin4j.tuple.MpArticle
	 * @throws WeixinException
	 */
	public String massArticleByGroupId(List<MpArticle> articles, int groupId)
			throws WeixinException {
		String mediaId = uploadArticle(articles);
		return massByGroupId(new MpNews(mediaId), groupId);
	}

	/**
	 * openId群发
	 * 
	 * @param tuple
	 *            消息元件
	 * @param openIds
	 *            openId列表
	 * @return 群发后的消息ID
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.mp.model.User
	 * @see com.foxinmy.weixin4j.tuple.Text
	 * @see com.foxinmy.weixin4j.tuple.Image
	 * @see com.foxinmy.weixin4j.tuple.Voice
	 * @see com.foxinmy.weixin4j.tuple.MpVideo
	 * @see com.foxinmy.weixin4j.tuple.MpNews
	 * @see com.foxinmy.weixin4j.tuple.MassTuple
	 * @see <a
	 *      href="http://mp.weixin.qq.com/wiki/15/5380a4e6f02f2ffdc7981a8ed7a40753.html#.E6.A0.B9.E6.8D.AEOpenID.E5.88.97.E8.A1.A8.E7.BE.A4.E5.8F.91.E3.80.90.E8.AE.A2.E9.98.85.E5.8F.B7.E4.B8.8D.E5.8F.AF.E7.94.A8.EF.BC.8C.E6.9C.8D.E5.8A.A1.E5.8F.B7.E8.AE.A4.E8.AF.81.E5.90.8E.E5.8F.AF.E7.94.A8.E3.80.91">根据openid群发</a>
	 * @see {@link com.foxinmy.weixin4j.mp.api.UserApi#getUser(String)}
	 */
	public String massByOpenIds(MassTuple tuple, String... openIds)
			throws WeixinException {
		if (tuple instanceof MpNews) {
			MpNews _news = (MpNews) tuple;
			List<MpArticle> _articles = _news.getArticles();
			if (StringUtil.isBlank(_news.getMediaId())
					&& (_articles == null || _articles.isEmpty())) {
				throw new WeixinException(
						"mass fail:mediaId or articles is required");
			}
			tuple = new MpNews(uploadArticle(_articles));
		}
		String msgtype = tuple.getMessageType();
		JSONObject obj = new JSONObject();
		obj.put("touser", openIds);
		obj.put(msgtype, JSON.toJSON(tuple));
		obj.put("msgtype", msgtype);
		String mass_openid_uri = getRequestUri("mass_openid_uri");
		Token token = tokenHolder.getToken();
		WeixinResponse response = weixinClient.post(
				String.format(mass_openid_uri, token.getAccessToken()),
				obj.toJSONString());

		return response.getAsJson().getString("msg_id");
	}

	/**
	 * 根据openid群发图文消息
	 * 
	 * @param articles
	 *            图文列表
	 * @param openIds
	 *            openId列表
	 * @return 群发后的消息ID
	 * @see <a
	 *      href="http://mp.weixin.qq.com/wiki/15/5380a4e6f02f2ffdc7981a8ed7a40753.html#.E6.A0.B9.E6.8D.AEOpenID.E5.88.97.E8.A1.A8.E7.BE.A4.E5.8F.91.E3.80.90.E8.AE.A2.E9.98.85.E5.8F.B7.E4.B8.8D.E5.8F.AF.E7.94.A8.EF.BC.8C.E6.9C.8D.E5.8A.A1.E5.8F.B7.E8.AE.A4.E8.AF.81.E5.90.8E.E5.8F.AF.E7.94.A8.E3.80.91">根据openid群发</a>
	 * @see {@link com.foxinmy.weixin4j.mp.api.MassApi#massByOpenIds(Tuple,String...)}
	 * @see com.foxinmy.weixin4j.tuple.MpArticle
	 * @throws WeixinException
	 */
	public String massArticleByOpenIds(List<MpArticle> articles,
			String... openIds) throws WeixinException {
		String mediaId = uploadArticle(articles);
		return massByOpenIds(new MpNews(mediaId), openIds);
	}

	/**
	 * 删除群发消息
	 * <p>
	 * 请注意,只有已经发送成功的消息才能删除删除消息只是将消息的图文详情页失效,已经收到的用户,还是能在其本地看到消息卡片
	 * </p>
	 * 
	 * @param msgid
	 *            发送出去的消息ID
	 * @throws WeixinException
	 * @see <a
	 *      href="http://mp.weixin.qq.com/wiki/15/5380a4e6f02f2ffdc7981a8ed7a40753.html#.E5.88.A0.E9.99.A4.E7.BE.A4.E5.8F.91.E3.80.90.E8.AE.A2.E9.98.85.E5.8F.B7.E4.B8.8E.E6.9C.8D.E5.8A.A1.E5.8F.B7.E8.AE.A4.E8.AF.81.E5.90.8E.E5.9D.87.E5.8F.AF.E7.94.A8.E3.80.91">删除群发</a>
	 * @see {@link com.foxinmy.weixin4j.mp.api.MassApi#massByGroupId(Tuple, int)}
	 * @see {@link com.foxinmy.weixin4j.mp.api.MassApi#massByOpenIds(Tuple, String...)
	 */
	public JsonResult deleteMassNews(String msgid) throws WeixinException {
		JSONObject obj = new JSONObject();
		obj.put("msgid", msgid);
		String mass_delete_uri = getRequestUri("mass_delete_uri");
		Token token = tokenHolder.getToken();
		WeixinResponse response = weixinClient.post(
				String.format(mass_delete_uri, token.getAccessToken()),
				obj.toJSONString());

		return response.getAsJsonResult();
	}

	/**
	 * 预览群发消息</br> 开发者可通过该接口发送消息给指定用户，在手机端查看消息的样式和排版
	 * 
	 * @param openId
	 *            接收用户的ID
	 * @param tuple
	 *            消息元件
	 * @return 处理结果
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.tuple.MassTuple
	 * @see <a
	 *      href="http://mp.weixin.qq.com/wiki/15/5380a4e6f02f2ffdc7981a8ed7a40753.html#.E9.A2.84.E8.A7.88.E6.8E.A5.E5.8F.A3.E3.80.90.E8.AE.A2.E9.98.85.E5.8F.B7.E4.B8.8E.E6.9C.8D.E5.8A.A1.E5.8F.B7.E8.AE.A4.E8.AF.81.E5.90.8E.E5.9D.87.E5.8F.AF.E7.94.A8.E3.80.91">预览群发消息</a>
	 */
	public JsonResult previewMassNews(String openId, MassTuple tuple)
			throws WeixinException {
		String msgtype = tuple.getMessageType();
		JSONObject obj = new JSONObject();
		obj.put("touser", openId);
		obj.put(msgtype, JSON.toJSON(tuple));
		obj.put("msgtype", msgtype);
		String mass_preview_uri = getRequestUri("mass_preview_uri");
		Token token = tokenHolder.getToken();
		WeixinResponse response = weixinClient.post(
				String.format(mass_preview_uri, token.getAccessToken()),
				obj.toJSONString());

		return response.getAsJsonResult();
	}

	/**
	 * 查询群发发送状态
	 * 
	 * @param msgId
	 *            消息ID
	 * @return 消息发送状态
	 * @throws WeixinException
	 * @see {@link com.foxinmy.weixin4j.mp.event.MassEventMessage#getStatusDesc(String)}
	 * @see <a
	 *      href="http://mp.weixin.qq.com/wiki/15/5380a4e6f02f2ffdc7981a8ed7a40753.html#.E6.9F.A5.E8.AF.A2.E7.BE.A4.E5.8F.91.E6.B6.88.E6.81.AF.E5.8F.91.E9.80.81.E7.8A.B6.E6.80.81.E3.80.90.E8.AE.A2.E9.98.85.E5.8F.B7.E4.B8.8E.E6.9C.8D.E5.8A.A1.E5.8F.B7.E8.AE.A4.E8.AF.81.E5.90.8E.E5.9D.87.E5.8F.AF.E7.94.A8.E3.80.91">查询群发状态</a>
	 */
	public String getMassNews(String msgId) throws WeixinException {
		JSONObject obj = new JSONObject();
		obj.put("msg_id", msgId);
		String mass_get_uri = getRequestUri("mass_get_uri");
		Token token = tokenHolder.getToken();
		WeixinResponse response = weixinClient.post(
				String.format(mass_get_uri, token.getAccessToken()),
				obj.toJSONString());

		return response.getAsJson().getString("msg_status");
	}
}
