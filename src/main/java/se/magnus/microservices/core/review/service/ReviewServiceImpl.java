package se.magnus.microservices.core.review.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.microservices.api.core.review.Review;
import se.magnus.microservices.api.core.review.ReviewService;
import se.magnus.microservices.api.exception.InvalidInputException;
import se.magnus.microservices.core.review.persistace.ReviewEntity;
import se.magnus.microservices.core.review.persistace.ReviewRepository;
import se.magnus.microservices.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;
@RestController
public class ReviewServiceImpl implements ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewServiceImpl(ServiceUtil serviceUtil, ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.serviceUtil = serviceUtil;
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public List<Review> getReviews(int productId) {

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        if (productId == 213) {
            LOG.debug("No reviews found for productId: {}", productId);
            return new ArrayList<>();
        }

       List<ReviewEntity> reviewEntityList= reviewRepository.findByProductId(productId);
       List<Review> reviewList= reviewMapper.entityListToApiList(reviewEntityList);
       reviewList.forEach(review -> {review.setServiceAddress(serviceUtil.getServiceAddress());});

        LOG.debug("/reviews response size: {}", reviewList.size());

        return reviewList;
    }

    @Override
    public Review createReview(Review review) {
        try{
            ReviewEntity reviewEntity =reviewMapper.ApiToEntity(review);
            ReviewEntity newEntity =reviewRepository.save(reviewEntity);
            System.out.print(newEntity);
            LOG.debug("createReview: created a review entity: {}/{}", review.getProductId(), review.getReviewId());
            return reviewMapper.EntityToApi(newEntity);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidInputException("Duplicate key, Product Id: "
                    + review.getProductId() + ", Review Id:" + review.getReviewId());
        }

    }

    @Override
    public void deleteReview(int productId) {
        LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        reviewRepository.deleteAll(reviewRepository.findByProductId(productId));
    }


}
