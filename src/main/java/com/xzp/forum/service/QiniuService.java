package com.xzp.forum.service;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.xzp.forum.util.FileUtil;

@Service
public class QiniuService {
	private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);

	// 设置好账号的ACCESS_KEY和SECRET_KEY
	String ACCESS_KEY = "VT8wfuxppE2jq6FzRcnSQvPkDfr0V45jA581P2GM";
	String SECRET_KEY = "_xzbgHK5Ej44XPoeML36CvunX6R6alRvAjO0xJ96";
	// 要上传的空间
	String bucketname = "shangke7ns";

	// 密钥配置
	Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
	// 构造一个带指定Zone对象的配置类
	Configuration cfg = new Configuration(Zone.autoZone());
	// ...其他参数参考类注释
	UploadManager uploadManager = new UploadManager(cfg);

	private static String QINIU_IMAGE_DOMAIN = "https://imghost.unknownshangke.top/";

	// 简单上传，使用默认策略，只需要设置上传的空间名就可以了
	public String getUpToken() {
		return auth.uploadToken(bucketname);
	}

	public String saveImage(MultipartFile file) throws IOException {
		try {
			int dotPos = file.getOriginalFilename().lastIndexOf(".");
			if (dotPos < 0) {
				return null;
			}
			String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
			if (!FileUtil.isFileAllowed(fileExt)) {
				return null;
			}

			String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
			// 调用put方法上传
			Response res = uploadManager.put(file.getBytes(), fileName, getUpToken());
			// 打印返回的信息
			if (res.isOK() && res.isJson()) {
				// 返回这张存储照片的地址http://ox6xu9hb7.bkt.clouddn.com/f07ff5a8945b4d6fb1bfc4cc8910e0fb.png，json串格式为：{"msg":"http://ox6xu9hb7.bkt.clouddn.com/f07ff5a8945b4d6fb1bfc4cc8910e0fb.png","code":0}
				return QINIU_IMAGE_DOMAIN + JSONObject.parseObject(res.bodyString()).get("key");
			} else {
				logger.error("七牛异常:" + res.bodyString());
				return null;
			}
		} catch (QiniuException e) {
			// 请求失败时打印的异常的信息
			logger.error("七牛异常:" + e.getMessage());
			return null;
		}
	}
}
