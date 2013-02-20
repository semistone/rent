package org.siraya.rent.pojo;

public class QueueMeta {
	int volumn;
	int lastRecord;
	String id;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLastRecord() {
		return lastRecord;
	}

	public void increaseVolumn(){
		this.volumn++;
	}
	public void increaseLastRecord(){
		this.lastRecord++;
	}
	public void setLastRecord(int lastRecord) {
		this.lastRecord = lastRecord;
	}

	public int getVolumn() {
		return volumn;
	}

	public void setVolumn(int volumn) {
		this.volumn = volumn;
	}

	public String toString(){
		StringBuffer sb= new StringBuffer();
		sb.append("volumn:"+volumn+"\n");
		sb.append("last record:"+lastRecord+"\n");
		return sb.toString();
	}
}
