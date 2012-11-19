package org.siraya.rent.page.service;
import java.util.List;
import org.siraya.rent.pojo.Space;
public interface IPageService {
	List<Space> getSpaces(String pageName);
}
