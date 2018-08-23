package com.bonc.credit.service.tianchuang;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.HttpRequest;
import com.bonc.util.ProjectErrorInformation;

/**
 *
 * @author zhijie.ma
 * @date 2017年7月17日
 * 
 */
@Service
public class TianChuangServiceImpl implements TianChuangService {

	private static final Logger logger = Logger.getLogger(TianChuangServiceImpl.class);

	@Autowired
	private TianChuangHelper tianChuangHelper;

	@Override
	public String getOnlineTime(JSONObject params) {

		String title = "手机号码在网时长";
		JSONObject jsonObject = new JSONObject();
		String method = "/mobile/cmcc/getOnlineTime";

		String code = "B0001";
		String desc = "调用失败";
		String isbilling = "0";

		String mobile = params.getString("mobile");
		String idcard = "152501196909230031";
		String name = "xxx";

		/*
		 * if(mobile == null || mobile.equals("") || idcard == null || idcard.equals("")
		 * || name == null || name.equals("")){
		 * 
		 * return ProjectErrorInformation.businessError5(title); }
		 */

		if (mobile == null || mobile.equals("")) {

			return ProjectErrorInformation.businessError5(title);
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", mobile);
		map.put("idcard", idcard);
		map.put("name", name);

		String tokenKey = tianChuangHelper.getTokenKey(method, map);

		String urlResult = tianChuangHelper.getUrlResult(tokenKey, map, method);

		String response = HttpRequest.sendPost(urlResult, null);

		logger.info("response >>> " + response);

		if (response == null || response.equals("")) {

			return ProjectErrorInformation.businessError1(title);
		}

		JSONObject ret = JSONObject.parseObject(response);
		String status = ret.getString("status");
		String errorCode = TianChuangHelper.getErrorCode(status);
		logger.info("调用信息  >>> " + status + " >>> " + errorCode);

		JSONObject jsonObject3 = new JSONObject();
		if (status.equals("0")) {
			JSONObject jsonObject2 = ret.getJSONObject("data");
			String resultMsg = jsonObject2.getString("resultMsg");
			String isp = jsonObject2.getString("isp");
			String province = jsonObject2.getString("province");
			String city = jsonObject2.getString("city");

			String result = jsonObject2.getString("result");

			jsonObject3.put("resultMsg", resultMsg + ", 单位：月");
			jsonObject3.put("province", province);
			jsonObject3.put("city", city);
			jsonObject3.put("isp", isp);

			if (result.equals("03")) {

				code = "C";
				isbilling = "1";

			} else if (result.equals("04")) {

				code = "B";
				isbilling = "1";

			} else if (result.equals("1")) {

				code = "A";
				isbilling = "1";

			} else if (result.equals("2")) {

				code = "AA";
				isbilling = "1";

			} else if (result.equals("3")) {

				code = "AAA";
				isbilling = "1";

			} else if (result.equals("-1")) {

				code = result;
				isbilling = "1";

			} else {

				code = "B0004";

			}

			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", jsonObject3);
			jsonObject.put("isbilling", isbilling);

			return jsonObject.toString();

		} else if (status.equals("3") || status.equals("2")) {
			code = "B0005";
			desc = ret.getString("message");
			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", desc);
			jsonObject.put("isbilling", isbilling);

			return jsonObject.toString();

		} else if (status.equals("1") || status.equals("4") || status.equals("5")
				|| status.equals("6") || status.equals("7") || status.equals("8") || status.equals("9")
				|| status.equals("10")) {
			return ProjectErrorInformation.businessError1(title);
		} else {
			return ProjectErrorInformation.businessError4(title);
		}

	}

	@Override
	public String getState(JSONObject params) {
		String title = "获取指定号码的当前状态";
		JSONObject jsonObject = new JSONObject();
		String method = "/mobile/cmcc/getState";

		String code = "B0001";
		String desc = "调用失败";
		String isbilling = "0";

		String mobile = params.getString("mobile");
		String idcard = "152501196909230031";
		String name = "xxx";

		/*
		 * if(mobile == null || mobile.equals("") || idcard == null || idcard.equals("")
		 * || name == null || name.equals("")){
		 * 
		 * return ProjectErrorInformation.businessError5(title); }
		 */

		if (mobile == null || mobile.equals("")) {

			return ProjectErrorInformation.businessError5(title);
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", mobile);
		map.put("idcard", idcard);
		map.put("name", name);

		String tokenKey = tianChuangHelper.getTokenKey(method, map);

		String urlResult = tianChuangHelper.getUrlResult(tokenKey, map, method);

		String response = HttpRequest.sendPost(urlResult, null);

		logger.info("response >>> " + response);

		if (response == null || response.equals("")) {

			return ProjectErrorInformation.businessError1(title);
		}

		JSONObject ret = JSONObject.parseObject(response);
		String status = ret.getString("status");
		String errorCode = TianChuangHelper.getErrorCode(status);
		logger.info("调用信息  >>> " + status + " >>> " + errorCode);

		JSONObject jsonObject3 = new JSONObject();
		if (status.equals("0")) {
			JSONObject jsonObject2 = ret.getJSONObject("data");
			String resultMsg = jsonObject2.getString("resultMsg");
			String isp = jsonObject2.getString("isp");
			String province = jsonObject2.getString("province");
			String city = jsonObject2.getString("city");

			String result = jsonObject2.getString("result");

			jsonObject3.put("resultMsg", resultMsg);
			jsonObject3.put("province", province);
			jsonObject3.put("city", city);
			jsonObject3.put("isp", isp);

			if (result.equals("0") || result.equals("1") || result.equals("2") || result.equals("3")
					|| result.equals("-1")) {

				code = result;
				isbilling = "1";

			} else {

				code = "B0004";

			}

			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", jsonObject3);
			jsonObject.put("isbilling", isbilling);

			return jsonObject.toString();

		} else if (status.equals("3") || status.equals("2")) {
			code = "B0005";
			desc = ret.getString("message");
			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", desc);
			jsonObject.put("isbilling", isbilling);

			return jsonObject.toString();

		} else if (status.equals("1") || status.equals("4") || status.equals("5")
				|| status.equals("6") || status.equals("7") || status.equals("8") || status.equals("9")
				|| status.equals("10")) {
			return ProjectErrorInformation.businessError1(title);
		} else {
			return ProjectErrorInformation.businessError4(title);
		}

	}

	@Override
	public String getVerifyMobileInfo(JSONObject params) {
		String title = "手机号码-身份证号码-姓名三要素验证";
		JSONObject jsonObject = new JSONObject();
		String method = "/mobile/cmcc/verifyMobileInfo3";

		String code = "B0001";
		String desc = "调用失败";
		String isbilling = "0";

		String mobile = params.getString("mobile");
		String idcard = params.getString("idcard");
		String name = params.getString("name");

		if (mobile == null || mobile.equals("") || idcard == null || idcard.equals("") || name == null
				|| name.equals("")) {

			return ProjectErrorInformation.businessError5(title);
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", mobile);
		map.put("idcard", idcard);
		map.put("name", name);

		String tokenKey = tianChuangHelper.getTokenKey(method, map);

		String urlResult = tianChuangHelper.getUrlResult(tokenKey, map, method);

		String response = HttpRequest.sendPost(urlResult, null);

		logger.info("response >>> " + response);

		if (response == null || response.equals("")) {

			return ProjectErrorInformation.businessError1(title);
		}

		JSONObject ret = JSONObject.parseObject(response);
		String status = ret.getString("status");
		String errorCode = TianChuangHelper.getErrorCode(status);
		logger.info("调用信息  >>> " + status + " >>> " + errorCode);

		JSONObject jsonObject3 = new JSONObject();
		if (status.equals("0")) {
			JSONObject jsonObject2 = ret.getJSONObject("data");
			String resultMsg = jsonObject2.getString("resultMsg");
			String isp = jsonObject2.getString("isp");
			String province = jsonObject2.getString("province");
			String city = jsonObject2.getString("city");

			String result = jsonObject2.getString("result");

			jsonObject3.put("resultMsg", resultMsg);
			jsonObject3.put("province", province);
			jsonObject3.put("city", city);
			jsonObject3.put("isp", isp);

			if (result.equals("0") || result.equals("1") || result.equals("2") || result.equals("3")
					|| result.equals("4") || result.equals("5") || result.equals("6") || result.equals("-1")) {

				code = result;

			} else {

				code = "B0004";

			}

			isbilling = "1";
			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", jsonObject3);
			jsonObject.put("isbilling", isbilling);

			return jsonObject.toString();

		} else if (status.equals("3") || status.equals("2")) {
			code = "B0005";
			desc = ret.getString("message");
			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", desc);
			jsonObject.put("isbilling", isbilling);

			return jsonObject.toString();

		} else if (status.equals("1") || status.equals("4") || status.equals("5")
				|| status.equals("6") || status.equals("7") || status.equals("8") || status.equals("9")
				|| status.equals("10")) {
			return ProjectErrorInformation.businessError1(title);
		} else {
			return ProjectErrorInformation.businessError4(title);
		}
	}

	@Override
	public String getVerifyIdcard(JSONObject params) {
		String title = "身份证号码和姓名认证,带有照片流";
		JSONObject jsonObject = new JSONObject();
		String method = "/identity/verifyIdcard";

		String code = "B0001";
		String desc = "调用失败";
		String isbilling = "0";

		String idcard = params.getString("idcard");
		String name = params.getString("name");

		if (idcard == null || idcard.equals("") || name == null || name.equals("")) {

			return ProjectErrorInformation.businessError5(title);
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("idcard", idcard);
		map.put("name", name);

		String tokenKey = tianChuangHelper.getTokenKey(method, map);

		map.put("appId", TianChuangHelper.appId);
		map.put("tokenKey", tokenKey);
		String urlResult = HttpRequest.getUrl(map, TianChuangHelper.url + method);

		String response = HttpRequest.sendPost(urlResult, null);

		logger.info("response >>> " + response);

		if (response == null || response.equals("")) {

			return ProjectErrorInformation.businessError1(title);
		}

		JSONObject ret = JSONObject.parseObject(response);
		String status = ret.getString("status");
		String errorCode = TianChuangHelper.getErrorCode(status);
		logger.info("调用信息  >>> " + status + " >>> " + errorCode);

		if (status.equals("0")) {
			JSONObject jsonObject2 = ret.getJSONObject("data");
			String resultMsg = jsonObject2.getString("resultMsg");

			String result = jsonObject2.getString("result");
			String photo = jsonObject2.getString("photo");
			if (photo == null) {
				photo = "";
			}

			if (result.equals("1")) {

				code = result;
				desc = "认证成功";
				isbilling = "1";

			} else if (result.equals("2")) {

				code = result;
				desc = "认证失败";
				isbilling = "1";

			} else if (result.equals("3")) {

				code = result;
				desc = resultMsg;
				isbilling = "1";

			} else if (result.equals("4")) {

				code = result;
				desc = resultMsg;
				isbilling = "1";

			} else if (result.equals("5")) {

				code = result;
				desc = resultMsg;
				isbilling = "1";

			} else {

				code = "B0004";
				desc = "其他错误";
			}

			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", desc);
			jsonObject.put("isbilling", isbilling);
			jsonObject.put("photo", photo);

			return jsonObject.toString();

		} else if (status.equals("3") || status.equals("2")) {
			code = "B0005";
			desc = ret.getString("message");
			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", desc);
			jsonObject.put("isbilling", isbilling);

			return jsonObject.toString();

		} else if (status.equals("1") || status.equals("4") || status.equals("5")
				|| status.equals("6") || status.equals("7") || status.equals("8") || status.equals("9")
				|| status.equals("10")) {
			return ProjectErrorInformation.businessError1(title);
		} else {
			return ProjectErrorInformation.businessError4(title);
		}
	}

	@Override
	public String getVerifyIdcardC(JSONObject params) {
		String title = "身份证号码和姓名认证";
		JSONObject jsonObject = new JSONObject();
		String method = "/identity/verifyIdcardC";

		String code = "B0001";
		String desc = "调用失败";
		String isbilling = "0";

		String idcard = params.getString("idcard");
		String name = params.getString("name");

		if (idcard == null || idcard.equals("") || name == null || name.equals("")) {

			return ProjectErrorInformation.businessError5(title);
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("idcard", idcard);
		map.put("name", name);

		String tokenKey = tianChuangHelper.getTokenKey(method, map);

		map.put("appId", TianChuangHelper.appId);
		map.put("tokenKey", tokenKey);
		String urlResult = HttpRequest.getUrl(map, TianChuangHelper.url + method);

		String response = HttpRequest.sendPost(urlResult, null);

		logger.info("response >>> " + response);

		if (response == null || response.equals("")) {

			return ProjectErrorInformation.businessError1(title);
		}

		JSONObject ret = JSONObject.parseObject(response);
		String status = ret.getString("status");
		String errorCode = TianChuangHelper.getErrorCode(status);
		logger.info("调用信息  >>> " + status + " >>> " + errorCode);

		if (status.equals("0")) {
			JSONObject jsonObject2 = ret.getJSONObject("data");
			String resultMsg = jsonObject2.getString("resultMsg");

			String result = jsonObject2.getString("result");

			if (result.equals("1")) {

				code = result;
				desc = "认证成功";
				isbilling = "1";

			} else if (result.equals("2")) {

				code = result;
				desc = "认证失败";
				isbilling = "1";

			} else if (result.equals("3")) {

				code = result;
				desc = resultMsg;
				isbilling = "1";

			} else {

				code = "B0004";
				desc = "其他错误";
			}

			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", desc);
			jsonObject.put("isbilling", isbilling);

			return jsonObject.toString();

		} else if (status.equals("3") || status.equals("2")) {
			code = "B0005";
			desc = ret.getString("message");
			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", desc);
			jsonObject.put("isbilling", isbilling);

			return jsonObject.toString();

		} else if (status.equals("1") || status.equals("4") || status.equals("5")
				|| status.equals("6") || status.equals("7") || status.equals("8") || status.equals("9")
				|| status.equals("10")) {
			return ProjectErrorInformation.businessError1(title);
		} else {
			return ProjectErrorInformation.businessError4(title);
		}
	}

	@Override
	public String getDegreeInfoC(JSONObject params) {
		String title = "获取个人学历信息";
		JSONObject jsonObject = new JSONObject();
		String method = "/identity/getDegreeInfoC";

		String code = "B0001";
		Object desc = "调用失败";
		String isbilling = "0";

		String idcard = params.getString("idcard");
		String name = params.getString("name");

		if (idcard == null || idcard.equals("") || name == null || name.equals("")) {

			return ProjectErrorInformation.businessError5(title);
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("idcard", idcard);
		map.put("name", name);

		String tokenKey = tianChuangHelper.getTokenKey(method, map);

		map.put("appId", TianChuangHelper.appId);
		map.put("tokenKey", tokenKey);
		String urlResult = HttpRequest.getUrl(map, TianChuangHelper.url + method);

		String response = HttpRequest.sendPost(urlResult, null);

		logger.info("response >>> " + response);

		if (response == null || response.equals("")) {

			return ProjectErrorInformation.businessError1(title);
		}

		JSONObject ret = JSONObject.parseObject(response);
		String status = ret.getString("status");
		String errorCode = TianChuangHelper.getErrorCode(status);
		logger.info("调用信息  >>> " + status + " >>> " + errorCode);

		if (status.equals("0")) {
			JSONObject jsonObject2 = ret.getJSONObject("data");
			if (jsonObject2 == null || jsonObject2.isEmpty()) {
				code = "0";
				desc = "无学历记录";
				isbilling = "1";
			} else {
				JSONObject degreeInfo = jsonObject2.getJSONObject("degreeInfo");
				JSONObject jsonObject3 = new JSONObject();
				if (degreeInfo != null || !degreeInfo.isEmpty()) {
					
					String degree = degreeInfo.getString("degree");
					String startTime = degreeInfo.getString("startTime");
					String graduateTime = degreeInfo.getString("graduateTime");
					String iskeySubject = degreeInfo.getString("iskeySubject");
					String levelNo = degreeInfo.getString("levelNo");
					String studyResult = degreeInfo.getString("studyResult");
					String studyStyle = degreeInfo.getString("studyStyle");
					String studyType = degreeInfo.getString("studyType");
					String specialty = degreeInfo.getString("specialty");
					String photo = degreeInfo.getString("photo");
					String photostyle = degreeInfo.getString("photostyle");
					String college = degreeInfo.getString("college");
					
					Map<String,Object> hashMap = new HashMap<String,Object>();
					hashMap.put("degree", degree);
					hashMap.put("startTime", startTime);
					hashMap.put("graduateTime", graduateTime);
					hashMap.put("iskeySubject", iskeySubject);
					hashMap.put("levelNo", levelNo);
					hashMap.put("studyResult", studyResult);
					hashMap.put("studyStyle", studyStyle);
					hashMap.put("studyType", studyType);
					hashMap.put("specialty", specialty);
					hashMap.put("photo", photo);
					hashMap.put("photostyle", photostyle);
					hashMap.put("college", college);
					
					
					jsonObject3.put("degreeInfo", hashMap);
					code = "0";
					desc = jsonObject3;
					isbilling = "1";
					
					
				}
				JSONObject collegeInfo = jsonObject2.getJSONObject("collegeInfo");
				if (collegeInfo != null && !collegeInfo.isEmpty()) {
					String academicianNum = collegeInfo.getString("academicianNum");
					String address = collegeInfo.getString("address");
					String artBatch = collegeInfo.getString("artBatch");
					String character = collegeInfo.getString("character");
					String colgCharacter = collegeInfo.getString("colgCharacter");
					String colgLevel = collegeInfo.getString("colgLevel");
					String colgType = collegeInfo.getString("colgType");
					String college = collegeInfo.getString("college");
					String collegeOldName = collegeInfo.getString("collegeOldName");
					String createDate = collegeInfo.getString("createDate");
					String createYears = collegeInfo.getString("createYears");
					String doctorDegreeNum = collegeInfo.getString("doctorDegreeNum");
					String is211 = collegeInfo.getString("is211");
					String keySubjectNum = collegeInfo.getString("keySubjectNum");
					String manageDept = collegeInfo.getString("manageDept");
					String masterDegreeNum = collegeInfo.getString("masterDegreeNum");
					String postdoctorNum = collegeInfo.getString("postdoctorNum");
					String scienceBatch = collegeInfo.getString("scienceBatch");
					
					Map<String,Object> hashMap = new HashMap<String,Object>();
					hashMap.put("academicianNum", academicianNum);
					hashMap.put("address", address);
					hashMap.put("artBatch", artBatch);
					hashMap.put("character", character);
					hashMap.put("colgCharacter", colgCharacter);
					hashMap.put("colgLevel", colgLevel);
					hashMap.put("colgType", colgType);
					hashMap.put("college", college);
					hashMap.put("collegeOldName", collegeOldName);
					hashMap.put("createDate", createDate);
					hashMap.put("createYears", createYears);
					hashMap.put("doctorDegreeNum", doctorDegreeNum);
					hashMap.put("is211", is211);
					hashMap.put("keySubjectNum", keySubjectNum);
					hashMap.put("manageDept", manageDept);
					hashMap.put("masterDegreeNum", masterDegreeNum);
					hashMap.put("postdoctorNum", postdoctorNum);
					hashMap.put("scienceBatch", scienceBatch);
					
					jsonObject3.put("collegeInfo", collegeInfo);
					desc = jsonObject3;
				}
			}

			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", desc);
			jsonObject.put("isbilling", isbilling);

			return jsonObject.toString();

		} else if (status.equals("3") || status.equals("2")) {
			code = "B0005";
			desc = ret.getString("message");
			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", desc);
			jsonObject.put("isbilling", isbilling);

			return jsonObject.toString();

		} else if (status.equals("1") || status.equals("4") || status.equals("5")
				|| status.equals("6") || status.equals("7") || status.equals("8") || status.equals("9")
				|| status.equals("10")) {
			return ProjectErrorInformation.businessError1(title);
		} else {
			return ProjectErrorInformation.businessError4(title);
		}
	}

	@Override
	public String getConsumeGrade(JSONObject params) {
		String title = "查询指定号码的月消费档次";
		JSONObject jsonObject = new JSONObject();
		String method = "/mobile/cmcc/getConsumeGrade";

		String code = "B0001";
		String desc = "调用失败";
		String isbilling = "0";

		String mobile = params.getString("mobile");
		String idcard = "430425198011253739";
		String name = "张三";

		if (mobile == null || mobile.equals("") || idcard == null || idcard.equals("") || name == null
				|| name.equals("")) {

			return ProjectErrorInformation.businessError5(title);
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", mobile);
		map.put("idcard", idcard);
		map.put("name", name);

		String tokenKey = tianChuangHelper.getTokenKey(method, map);

		String urlResult = tianChuangHelper.getUrlResult(tokenKey, map, method);

		String response = HttpRequest.sendPost(urlResult, null);

		logger.info("response >>> " + response);

		if (response == null || response.equals("")) {

			return ProjectErrorInformation.businessError1(title);
		}

		JSONObject ret = JSONObject.parseObject(response);
		String status = ret.getString("status");
		String errorCode = TianChuangHelper.getErrorCode(status);
		logger.info("调用信息  >>> " + status + " >>> " + errorCode);

		JSONObject jsonObject3 = new JSONObject();
		if (status.equals("0")) {
			JSONObject jsonObject2 = ret.getJSONObject("data");
			String resultMsg = jsonObject2.getString("resultMsg");
			String isp = jsonObject2.getString("isp");
			String province = jsonObject2.getString("province");
			String city = jsonObject2.getString("city");

			String result = jsonObject2.getString("result");

			jsonObject3.put("resultMsg", resultMsg);
			jsonObject3.put("province", province);
			jsonObject3.put("city", city);
			jsonObject3.put("isp", isp);

			if (result.equals("0") || result.equals("1") || result.equals("2") || result.equals("3")
					|| result.equals("4") || result.equals("101") || result.equals("102") || result.equals("-1")
					|| result.equals("103") || result.equals("01")|| result.equals("02")) {

				code = result;

			} else {

				code = "B0004";

			}

			isbilling = "1";
			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", jsonObject3);
			jsonObject.put("isbilling", isbilling);

			return jsonObject.toString();

		} else if (status.equals("3") || status.equals("2")) {
			code = "B0005";
			desc = ret.getString("message");
			jsonObject.put("interface", title);
			jsonObject.put("code", code);
			jsonObject.put("desc", desc);
			jsonObject.put("isbilling", isbilling);

			return jsonObject.toString();

		} else if (status.equals("1") || status.equals("4") || status.equals("5")
				|| status.equals("6") || status.equals("7") || status.equals("8") || status.equals("9")
				|| status.equals("10")) {
			return ProjectErrorInformation.businessError1(title);
		} else {
			return ProjectErrorInformation.businessError4(title);
		}
	}

}
