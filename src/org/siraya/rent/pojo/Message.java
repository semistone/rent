package org.siraya.rent.pojo;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
public class Message {
	private long id;
	private String userId;
	private String cmd;
	@JsonIgnore
	private byte[] data;
	private long created;
	private long modified;
	private boolean isBinary = false;
	private String contentType;
	


	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isBinary() {
		return isBinary;
	}

	public void setBinary(boolean isBinary) {
		this.isBinary = isBinary;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	@JsonProperty("data")
	public String getStringData() {
		return new String(data);
	}
	
	public void setStringData(String data) {
		this.data = data.getBytes();
	}

	public long getCreated() {
		if (created == 0) {
			created=java.util.Calendar.getInstance().getTimeInMillis()/1000;
			modified= created;
		}

		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public long getModified() {
		return modified;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("id:"+id+"\n");
		sb.append("cmd:"+cmd+"\n");
		sb.append("user id:"+userId+"\n");
		sb.append("data:"+new String(data)+"\n");
		sb.append("created:"+created+"\n");
		return sb.toString();
	}
}
