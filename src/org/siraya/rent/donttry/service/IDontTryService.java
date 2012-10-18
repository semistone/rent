package org.siraya.rent.donttry.service;

public interface IDontTryService {
	public enum DontTryType {
		Today(0),
		Week(1),	
		Month(2),
		Life(3);
		private int type;
		private DontTryType(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
	}
	
	public void doTry(String name,DontTryType limitType,int maxCount);
}
