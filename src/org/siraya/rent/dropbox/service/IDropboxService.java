package org.siraya.rent.dropbox.service;
import java.net.URI;
import java.io.File;
public interface IDropboxService {
	public String doLink();
	
	public String upload(File src, String target)  throws Exception;
}
