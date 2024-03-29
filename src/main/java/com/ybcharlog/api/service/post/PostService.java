package com.ybcharlog.api.service.post;

import com.ybcharlog.common.dto.CustomPage;
import com.ybcharlog.api.dto.RequestDto.post.PostCreateDto;
import com.ybcharlog.api.dto.RequestDto.post.PostEditDto;
import com.ybcharlog.api.dto.RequestDto.post.PostSearchDto;
import com.ybcharlog.api.dto.ResponseDto.post.PostResponse;
import com.ybcharlog.api.dto.ResponseDto.post.PostResponse.GetPostPageReq;
import com.ybcharlog.api.domain.comment.Comment;
import com.ybcharlog.api.domain.post.Post;
import com.ybcharlog.exception.PostNotFound;
import com.ybcharlog.api.mapper.post.GetPostResDtoMapper;
import com.ybcharlog.api.repository.comment.CommentRepository;
import com.ybcharlog.api.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

	public Post write(PostCreateDto postCreateDto) {
		return postRepository.save(Post.initPost(postCreateDto.getTitle(), postCreateDto.getContent(), postCreateDto.getDisplay()));
	}

	@Transactional
	public PostResponse getOne(Long postId) {
		return postRepository.getPostOne(postId);
	}

	public List<PostResponse> getList(PostSearchDto postSearchDto) {
		// Pageable 한번 까서 볼것!
//		Pageable pageable = PageRequest.of(page, 5, Sort.by("id").descending());

		// 순수 JPA
//		return postRepository.findAll(pageable).stream()
//				.map(PostResponse::new
//				)
//				.collect(Collectors.toList());
		// Querydsl
		return postRepository.getList(postSearchDto).stream()
				.map(PostResponse::new)
				.collect(Collectors.toList());
	}

	@Transactional
	public CustomPage<PostResponse> getListByPage(GetPostPageReq req, Pageable pageable) {
		Page<Post> postPage = postRepository.getPostListByPage(req, pageable);
		// Querydsl
		List<PostResponse> dtoList = GetPostResDtoMapper.INSTANCE.toDtoList(postPage.getContent());

		return CustomPage.<PostResponse>builder()
				.list(dtoList)
				// list
				.totalElements(postPage.getTotalElements())
				// count
				.totalCount(postPage.getTotalPages())
				.build();
	}

	@Transactional
	public void editPost(Long postId, PostEditDto postEditDto) {
		Post post = postRepository.findById(postId)
				.orElseThrow(PostNotFound::new);

		post.edit(postEditDto, post);
	}

	@Transactional
	public void deletePost(Long postId) {
		List<Long> commentIds = postRepository.getPostOne(postId)
				.getComments()
				.stream().map(Comment::getId).collect(Collectors.toList());
		commentRepository.deleteAllByCommentInQuery(commentIds);
		postRepository.updateDeletedByPostId(postId);
	}

	@Transactional
	public void updatePostThumbnailImage(String uploadImagePath, Long postId) {
		postRepository.updatePostThumbnailImage(uploadImagePath, postId);
	}
}
