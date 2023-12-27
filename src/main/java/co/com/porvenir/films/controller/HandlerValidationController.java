package co.com.porvenir.films.controller;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import co.com.porvenir.films.model.ErrorMessage;

public class HandlerValidationController {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorMessage validationMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
	       
	    FieldError fieldError = ex.getBindingResult().getFieldError();	
	    return new ErrorMessage( new StringBuilder("Error en la solicitud. ")
	    			.append(fieldError.getField())
					.append(": ")
					.append(fieldError.getDefaultMessage())
					.toString() );
	}
	
	@ExceptionHandler( {ConstraintViolationException.class, MethodArgumentTypeMismatchException.class, MissingPathVariableException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorMessage validationExceptionsGroup(Exception ex) {
		return new ErrorMessage( new StringBuilder("Error en la solicitud. ")
				.append(ex.getMessage())
				.toString());
	}

}