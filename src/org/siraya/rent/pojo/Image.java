package org.siraya.rent.pojo;

import javax.validation.constraints.NotNull;

public class Image {
	@NotNull
	private String imgTarget;
	@NotNull
	private String shareUrl;
	private long created;
	private long modified;
	private String id;
	@NotNull
	private String imgGroup;
	@NotNull
	private int status;
	private String name;

	private int width;
	private int height;
	private long size;

	@NotNull
	private String userId;
	
	private String ext;
	public String getExt() {
		if (this.ext == null){
			ext = imgTarget.substring(imgTarget.lastIndexOf(".") + 1 );
		}
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public static String genId() {
		return java.util.UUID.randomUUID().toString();
	}

	public String getImgTarget() {
		return imgTarget;
	}

	public void setImgTarget(String imgTarget) {
		this.imgTarget = imgTarget;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
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
		if (modified == 0) {
			modified=java.util.Calendar.getInstance().getTimeInMillis()/1000;
		}
		return modified;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImgGroup() {
		return imgGroup;
	}

	public void setImgGroup(String imgGroup) {
		this.imgGroup = imgGroup;
	}
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	public int getWidth() {
		return width;
	}

	public void setWidth(int widdh) {
		this.width = widdh;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
