package com.ybcharlog.api.controller.common;


import com.ybcharlog.api.ResponseDto.common.ErrorResponse;
import com.ybcharlog.api.exception.InvalidRequest;
import com.ybcharlog.api.exception.PostNotFound;
import com.ybcharlog.api.exception.YbcharLogException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponse MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
		log.info("exceptionHandler error", e);

		if (e.hasErrors()) {

//			ErrorResponse response = new ErrorResponse("400", "잘못된 요청입니다.");
			ErrorResponse response = ErrorResponse.builder()
					.code("400")
					.message("잘못된 요청입니다.")
					.build();

			for (FieldError fieldError : e.getFieldErrors())
				response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());

			return response;
		} else {
			// 에러가 없는 경우
			return null;
		}
	}

	@ResponseBody
	@ExceptionHandler(YbcharLogException.class)
	public ResponseEntity<ErrorResponse> ybcharLogException(YbcharLogException e) {
		int statusCode = e.statusCode();

		ErrorResponse body = ErrorResponse.builder()
				.code(String.valueOf(statusCode))
				.message(e.getMessage())
				.validation(e.getValidation())
				.build();
		// 응답 json validation - 해당 키의 에러를 포함시킨다.
//		if (e instanceof InvalidRequest) {
//			InvalidRequest invalidRequest = (InvalidRequest) e;
//			String fieldName = invalidRequest.getFieldName();
//			String message = invalidRequest.getMessage();
//			body.addValidation(fieldName, message);
//		}

		ResponseEntity<ErrorResponse> response = ResponseEntity.status(statusCode).body(body);
		return response;
	}

}
