package com.seally.service.impl;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Service;

import com.seally.dao.UserDao;
import com.seally.service.UserServive;
import com.seally.utils.ClientResult;
import com.seally.utils.FileUtil;
import com.seally.utils.UUIDGenerator;

@Service
public class UserServiceImpl implements UserServive {
	
	@Resource
	UserDao userDao;

	@Override
	public ClientResult handRequest(HttpServletRequest request, HttpServletResponse response) {
		String baseDir = "D:/fileUpload";
		File tempDir = new File(baseDir+"/temp/");
		File imgDir = new File(baseDir+"/img/");
		if(!tempDir.exists()) { 
			tempDir.mkdirs();
		}
		if(!imgDir.exists()) { 
			imgDir.mkdirs();
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(tempDir);//文件缓存路径
		factory.setSizeThreshold(10*1096);
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(10*1024*1024);
		
		try {
			List<FileItem> fileItems = upload.parseRequest(request);
			for(FileItem item:fileItems){
				String fieldName = item.getFieldName();
				if(item.isFormField()){//普通表单元素
					String value = item.getString();
					
				}else{//文件元素
					String fileName = item.getName();
					String newFileName = UUIDGenerator.generate();//产生文件名
					File store = new File(imgDir+newFileName+FileUtil.parsePrefixName(fileName));
					item.write(store);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ClientResult.error(401,"保存失败！");
		}
		return ClientResult.success("保存成功！", null);
	}
}
