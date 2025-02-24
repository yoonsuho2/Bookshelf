package com.cerdure.bookshelf.service;

import com.cerdure.bookshelf.domain.board.Review;
import com.cerdure.bookshelf.dto.board.ReviewDto;
import com.cerdure.bookshelf.repository.ReviewRepository;
import com.cerdure.bookshelf.service.interfaces.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public Long create(ReviewDto reviewDto) {
        Review review = reviewDto.toEntity();
        reviewRepository.save(review);
        return review.getId();
    }

    @Override
    public Page<Review> findAll(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable= PageRequest.of(page,3, Sort.by("regDate").descending());
        return reviewRepository.findAll(pageable);
    }

    @Override
    public Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId).get();
    }

    @Override
    public Page<Review> findByBookId(Long bookId, Pageable pageable) {
        return reviewRepository.findByBookId(bookId, pageable);
    }

    @Override
    public Page<Review> findByMemberId(Long memberId, Pageable pageable) {
        return reviewRepository.findByMemberId(memberId, pageable);
    }

    @Override
    public void modify(Long reviewId, ReviewDto reviewDto, Authentication authentication) throws Exception {
        Review review = reviewRepository.findById(reviewId).get();
            review.changeRating(reviewDto.getRating());
            review.changeTag(reviewDto.getTag());
            review.changeContent(reviewDto.getContent());
            reviewRepository.save(review);
    }

    @Override
    public void delete(Long reviewId, Authentication authentication) throws Exception {
        Review review = reviewRepository.findById(reviewId).get();
            reviewRepository.delete(review);
    }
}
