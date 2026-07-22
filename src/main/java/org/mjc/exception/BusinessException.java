
package org.mjc.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("all")
@Data
@EqualsAndHashCode(callSuper = false)
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
