package org.siraya.rent.dropbox.service;

import org.siraya.rent.pojo.Image;



public interface IDropboxService {
	public String doLink();
	
	public void upload(Image img);

	public void save(Image img);
}
