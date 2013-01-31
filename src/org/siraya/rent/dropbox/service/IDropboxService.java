package org.siraya.rent.dropbox.service;

import org.siraya.rent.pojo.Image;



public interface IDropboxService {
	public String doLink();
	
	public void upload(Image img);

	public void save(Image img);
	
	public void sync();
	
	public void delete(Image img);
	
	public Image get(String id);
	
	public void thumbnail(Image image, String size, java.io.OutputStream out);
}
