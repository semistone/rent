package org.siraya.rent.utils;

public class RentException extends RuntimeException {
	static final long serialVersionUID = 1L ; 
	RentErrorCode errorCode;
	/**
	 * Exception error code
	 * @author angus_chen
	 *
	 */
	public enum RentErrorCode {
		Success(0),
		ErrorMobileGateway(1),
		ErrorStatusViolate(2),
		ErrorUserExist(3),
		ErrorNotFound(4),
		ErrorExceedLimit(5),
		ErrorAuthExpired(6),
		ErrorAuthFail(7),
		ErrorRemoved(8),
		ErrorUpdateSameItem(9),
		ErrorCountryNotSupport(10),
		ErrorCanNotOverwrite(11),
		ErrorInvalidParameter(12);
		private int code;	
		
		
		private RentErrorCode(int code) {
			this.code = code;
		}
		
		public int getStatus() {
			return code;
		}
	}
	
	public RentException(RentErrorCode code,String msg){
		super(msg);
		this.errorCode = code;
	}

	public RentErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(RentErrorCode errorCode) {
		this.errorCode = errorCode;
	}
}