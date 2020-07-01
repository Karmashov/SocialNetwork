package com.skillbox.socialnetwork.main.service.impl;

import com.skillbox.socialnetwork.main.dto.comment.request.CommentRequest;
import com.skillbox.socialnetwork.main.dto.comment.response.CommentDto;
import com.skillbox.socialnetwork.main.dto.comment.response.CommentResponseFactory;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.dto.universal.Response;
import com.skillbox.socialnetwork.main.model.Notification;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.Post;
import com.skillbox.socialnetwork.main.model.PostComment;
import com.skillbox.socialnetwork.main.model.enumerated.NotificationCode;
import com.skillbox.socialnetwork.main.model.enumerated.ReadStatus;
import com.skillbox.socialnetwork.main.repository.CommentRepository;
import com.skillbox.socialnetwork.main.repository.NotificationRepository;
import com.skillbox.socialnetwork.main.repository.PostRepository;
import com.skillbox.socialnetwork.main.service.CommentService;
import com.skillbox.socialnetwork.main.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PersonService personService;
    private final NotificationRepository notificationRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, PersonService personService, NotificationRepository notificationRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.personService = personService;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public PostComment findById(Integer id) {
        return commentRepository.findPostCommentById(id);
    }

    @Override
    public PostComment save(PostComment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Dto addComment(int postId, CommentRequest request, int authorId) {
        PostComment comment = new PostComment();
        Person commentAuthor = personService.findById(authorId);
        Post post = postRepository.findPostById(postId);
        comment.setPost(post);
        comment.setTime(new Date());
        comment.setCommentText(request.getText());
        comment.setParentComment(commentRepository.findPostCommentById(request.getParentId()));
        comment.setAuthor(commentAuthor);
        comment.setIsBlocked(false);
        commentRepository.save(comment);

        //добавление уведомления о комментарии

        boolean isParentComment = comment.getParentComment() == null;   //проверка является ли комментарий ответом на комментарий или комментарием к посту
        boolean sameAuthor = commentAuthor.equals(post.getAuthor()) && isParentComment;

        if (!sameAuthor) {
            Notification notification = new Notification();
            notification.setEntityAuthor(commentAuthor);
            notification.setReadStatus(ReadStatus.SENT);
            notification.setSentTime(comment.getTime());
            notification.setPerson(
                    isParentComment ? post.getAuthor() : comment.getParentComment().getAuthor());
            notification.setType(
                    isParentComment ? NotificationCode.POST_COMMENT : NotificationCode.COMMENT_COMMENT);
            notification.setInfo(comment.getCommentText());
            notificationRepository.save(notification);
            log.info("SENT COMMENT notification to " + notification.getPerson().getFirstName() + " " + notification.getPerson().getLastName());
        }
        log.info("New comment added from user with id = {}", authorId);
        return CommentResponseFactory.getCommentDto(comment, CommentResponseFactory.getCommentList(comment.getChildComments(), comment));
    }

    @Override
    public Dto updateComment(int commentId, CommentRequest request) {
        PostComment comment = commentRepository.findPostCommentById(commentId);
        comment.setCommentText(request.getText());
        comment.setParentComment(commentRepository.findPostCommentById(request.getParentId()));
        commentRepository.save(comment);
        log.info("Comment with id {} successfully updated", commentId);
        return CommentResponseFactory.getCommentDto(comment, CommentResponseFactory.getCommentList(comment.getChildComments(), comment));
    }

    @Override
    public Dto deleteComment(int commentId) {
        commentRepository.deleteById(commentId);
        CommentDto dto = new CommentDto();
        dto.setId(commentId);
        log.info("Comment with id {} is deleted", commentId);
        return dto;
    }

    @Override
    public Response getComments(int postId) {
        Post postById = postRepository.findPostById(postId);
        return CommentResponseFactory.getComments(postById.getComments(), 10, 10);
    }
}
