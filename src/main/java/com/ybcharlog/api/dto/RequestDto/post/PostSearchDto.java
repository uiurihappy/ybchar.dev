package com.ybcharlog.api.dto.RequestDto.post;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import static java.lang.Math.max;

@Getter
@Builder
@Data
public class PostSearchDto {

	private static final int MAX_SIZE = 1;

	@Builder.Default
	private Integer page = 1;
	@Builder.Default
	private Integer size = 10;

//	@Builder
//	public PostSearchDto(Integer page, Integer size) {
//		this.page = page;
//		this.size = size;
//	}

	public long getOffset(Integer page, Integer size) {
		return (long) (max(1, page) - 1) * max(size, MAX_SIZE);
	}

//	@Data
}
