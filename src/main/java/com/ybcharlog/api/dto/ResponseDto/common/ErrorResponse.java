package com.ybcharlog.api.dto.ResponseDto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

	private final String code;
	private final String message;
	private final Map<String, String> validation;

	/* Map 사용 지양하는 이유
		- 고정타입 문제
		- 캐스팅 문제
		- 컴파일 에러가 발생 안하는 문제
		- 사용처가 불분명해지는 문제
		- 응답 필드에 뭐가 들어가는지 알 수 없는 문제
		- 불변성문제
	 */
	/*
		{
			"code": "400",
			"message": "잘못된 요청입니다.",
			"valida†ion": {
				"title": "값을 입력해주세요",
			}
		}
	 */

	@Builder
	public ErrorResponse(String code, String message, Map<String, String> validation) {
		this.code = code;
		this.message = message;
		this.validation = validation;
	}


	public void addValidation(String fieldName, String errorMessage) {
//		ValidationTuple tuple = new ValidationTuple(fieldName, errorMessage);
		this.validation.put(fieldName, errorMessage);
	}

	// 이렇게 말고 dto를 선언해서 사용해서 하는 방식이 제일 깔금하다 @Data를 사용한다든지..
	@RequiredArgsConstructor
	private class ValidationTuple {
		private final String fieldName;
		private final String errroMessage;
	}
}
