package com.ybcharlog.api.repository.post;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ybcharlog.api.RequestDto.post.PostEditDto;
import com.ybcharlog.api.RequestDto.post.PostSearchDto;
import com.ybcharlog.api.ResponseDto.comment.CommentResponse;
import com.ybcharlog.api.ResponseDto.post.PostResponse;
import com.ybcharlog.api.domain.post.Post;
import com.ybcharlog.api.domain.post.QPost;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import java.util.List;

import static com.ybcharlog.api.domain.comment.QComment.*;
import static com.ybcharlog.api.domain.post.QPost.*;


@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Post getPostOne(Long postId) {
        return jpaQueryFactory
                .selectFrom(post)
                .innerJoin(post.comments).fetchJoin()
                .where(post.id.eq(postId))
                .fetchOne();
    }

    @Override
    public List<Post> getList(PostSearchDto postSearchDto) {
        return jpaQueryFactory.selectFrom(post)
                .limit(postSearchDto.getSize())
                .offset(postSearchDto.getOffset(postSearchDto.getPage(), postSearchDto.getSize()))
                .orderBy(post.id.desc())
                .fetch();
    }

    @Override
    public void editPost(Long postId, PostEditDto postEditDto) {
        jpaQueryFactory.update(post)
                .set(post.title, postEditDto.getTitle())
                .set(post.content, postEditDto.getContent())
                .where(post.id.eq(postId))
                .execute();
    }
}
