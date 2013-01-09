package org.siraya.rent.utils;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.WebAuthSession;

@Service("dropboxUtility")
public class DropboxUtility {

	@Autowired
	private IApplicationConfig applicationConfig;

	public String doLink() throws DropboxException {
		Map<String, Object> settings = applicationConfig.get("dropbox");
		AppKeyPair appKeyPair = new AppKeyPair(
				(String) settings.get("app_key"),
				(String) settings.get("app_secret"));
		WebAuthSession was = new WebAuthSession(appKeyPair,
				Session.AccessType.APP_FOLDER);

		// Make the user log in and authorize us.
		WebAuthSession.WebAuthInfo info = was.getAuthInfo();
		return info.url;
	}

	public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}
}
