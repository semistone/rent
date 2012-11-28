package org.siraya.rent.page.service;
import java.util.List;
import org.siraya.rent.pojo.Space;
public interface IPageService {
	public List<Space> getSpaces(String pageName);
	
	public void update(Space space);
}
