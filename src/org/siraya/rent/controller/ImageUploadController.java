package org.siraya.rent.controller;

import org.siraya.rent.dropbox.service.DropboxService;
import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import javax.servlet.http.*;
import javax.servlet.ServletConfig;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

@RequestMapping("/uploadimage")
@Controller
public class ImageUploadController {

	private String uploadFolderPath;

	ServletConfig config;
	private static Logger logger = LoggerFactory
			.getLogger(ImageUploadController.class);

	@RequestMapping(method = RequestMethod.POST)
	public String create(UploadItem uploadItem, BindingResult result,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		logger.debug("post upload image");
		if (result.hasErrors()) {
			for (ObjectError error : result.getAllErrors()) {
				throw new RentException(
						RentException.RentErrorCode.ErrorInvalidParameter,
						"Error: " + error.getCode() + " - "
								+ error.getDefaultMessage());
			}
		}

		try {
			MultipartFile file = uploadItem.getFileData();
			String fileName = null;
			InputStream inputStream = null;
			OutputStream outputStream = null;
			if (file.getSize() > 0) {
				inputStream = file.getInputStream();
				if (file.getSize() > 10000) {
					throw new RentException(RentException.RentErrorCode.ErrorInvalidParameter,"File Size:::" + file.getSize());
				}
				logger.debug("size::" + file.getSize());
				fileName = request.getRealPath("") + "/images/"
						+ file.getOriginalFilename();
				outputStream = new FileOutputStream(fileName);
				logger.debug("fileName:" + file.getOriginalFilename());
				int readBytes = 0;
				byte[] buffer = new byte[10000];
				while ((readBytes = inputStream.read(buffer, 0, 10000)) != -1) {
					outputStream.write(buffer, 0, readBytes);
				}
				outputStream.close();
				inputStream.close();
			}

			session.setAttribute("uploadFile", file.getOriginalFilename());
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}

	public String getUploadFolderPath() {
		return uploadFolderPath;
	}

	public void setUploadFolderPath(String uploadFolderPath) {
		this.uploadFolderPath = uploadFolderPath;
	}

}
