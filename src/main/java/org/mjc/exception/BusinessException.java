
package org.mjc.exception;

import lombok.Data;

@SuppressWarnings("all")
@Data
public class BusinessException extends Exception {
		private String message;
		private Integer code;
		public BusinessException(String message) {
			this.message=message;
		}

	public BusinessException(Integer code,String message ) {
		super(message);
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
}
