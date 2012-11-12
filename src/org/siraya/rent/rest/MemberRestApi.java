package org.siraya.rent.rest;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.pojo.Member;
import org.siraya.rent.user.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("memberRestApi")
@Path("/member")
public class MemberRestApi {
	@Autowired
	private IMemberService memberService;


	private static Map<String, String> OK;
	@Autowired
	private UserAuthorizeData userAuthorizeData;
	public MemberRestApi(){
		if (OK == null) {
			OK = new HashMap<String, String>();
			OK.put("status", "SUCCESS");
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Member post(Member member){
		member.setUserId(this.userAuthorizeData.getUserId());
		memberService.newMember(member);
		return member;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response put(Member member){
		member.setUserId(this.userAuthorizeData.getUserId());
		memberService.updateMember(member);
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response delete(@PathParam("id") String id) {
		memberService.deleteMember(this.userAuthorizeData.getUserId(), id);
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Member get(@PathParam("id") String id) {
		return memberService.getMember(this.userAuthorizeData.getUserId(), id);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/create_members_from_fb")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response createMembers (List<Member> members) {
    	for(Member member : members){
    		// use fb account id as member id
    		member.setMemberId(member.getId());
    		member.setFbAccount(member.getId());
    	}
		this.memberService.createMembers(this.userAuthorizeData.getUserId(), members);
		return Response.status(HttpURLConnection.HTTP_OK)
				.entity(MemberRestApi.OK).build();		
	}
	
	public IMemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(IMemberService memberService) {
		this.memberService = memberService;
	}

	public UserAuthorizeData getUserAuthorizeData() {
		return userAuthorizeData;
	}

	public void setUserAuthorizeData(UserAuthorizeData userAuthorizeData) {
		this.userAuthorizeData = userAuthorizeData;
	}	
	
}
