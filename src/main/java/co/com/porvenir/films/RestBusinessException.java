package co.com.porvenir.films;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import co.com.porvenir.films.model.ErrorMessage;
import lombok.Getter;

@Getter
public class RestBusinessException extends HttpStatusCodeException {

	private ErrorMessage errorMsg;

	public RestBusinessException(ErrorMessage errorMsg, HttpStatus statusCode, String statusText) {
		super(statusCode, statusText);
		this.errorMsg = errorMsg;
	}
}
