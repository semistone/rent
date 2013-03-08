package org.siraya.rent.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
@Service("fileUtility")
public class FileUtility {

	@Autowired
	private IApplicationConfig applicationConfig;
    private static Logger logger = LoggerFactory.getLogger(FileUtility.class);

	private String tmpDir;
	private boolean isInit = false;
	private void init(){
		java.util.Map<String,Object> setting = applicationConfig.get("general");
		tmpDir = (String)setting.get("tmp_dir");		
		if (tmpDir == null) {
			throw new RentException(RentException.RentErrorCode.ErrorInit,"tmp_dir not exist");
		}
		isInit = true;
	}
	
	public File copyInputToTmp(String dest, InputStream inputStream, boolean isOverwrite) throws IOException{
		if (!isInit) this.init();
		logger.debug("copt inputstream to tmp "+dest);
		dest = tmpDir + "/" + dest;
		this.mkdir(dest.substring(0, dest.lastIndexOf("/")));
		File f = new File(dest);
		//
		// check file already exists
		//
		if (!isOverwrite && f.exists()) {
			throw new RentException(RentException.RentErrorCode.ErrorDuplicate,"file already exist");
		}
		saveFileToTmp(f, inputStream);
		return f;
	}
	
	public void copyToOutputStream(File f, OutputStream os) throws Exception {
		java.io.FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			byte[] buf = new byte[8192];
			int n = 0;
			while (-1 != (n = fis.read(buf))) {
				os.write(buf, 0, n);
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}
	
	private void mkdir(String path) {
		logger.debug("mkdir "+path);
		File f = new File(path);
		if (f.exists()) {
			if (f.isDirectory()) {
				return;
			} else {
				throw new RentException(
						RentException.RentErrorCode.ErrorGeneral, "path "
								+ path + " is not dir");
			}
		} else {
			if (!f.mkdirs()) {
				throw new RentException(
						RentException.RentErrorCode.ErrorGeneral, "mkdir "
								+ path + " fail");
			}
		}
	}
	
    private void saveFileToTmp(File f, InputStream is) throws IOException{
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(f);
			byte[] buf = new byte[8192];
			int n =0;
			while (-1 != (n = is.read(buf))) {				
				fos.write(buf, 0, n);
			}
		}finally{
			if (fos != null) {
				fos.flush();
				fos.close();				
			}
		}	
    }
	public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

}
