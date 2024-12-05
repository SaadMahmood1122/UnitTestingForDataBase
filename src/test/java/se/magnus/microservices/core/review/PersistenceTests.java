package se.magnus.microservices.core.review;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import se.magnus.microservices.core.review.persistace.ReviewEntity;
import se.magnus.microservices.core.review.persistace.ReviewRepository;

import java.util.List;

import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = NOT_SUPPORTED)
public class PersistenceTests extends MySqlTestBase{
    @Autowired
    private ReviewRepository reviewRepository;

    private ReviewEntity savedEntity;

    @BeforeEach
    void setupDb(){
        reviewRepository.deleteAll();
        ReviewEntity  entity = new ReviewEntity(1,2,"a","s","c");
        savedEntity = reviewRepository.save(entity);
        assertEqualsReview(savedEntity,entity);
    }


    @Test
    void create() {

        ReviewEntity newEntity = new ReviewEntity(1, 3, "a", "s", "c");
        reviewRepository.save(newEntity);

        ReviewEntity foundEntity = reviewRepository.findById(newEntity.getId()).get();
        assertEqualsReview(newEntity, foundEntity);

        assertEquals(2, reviewRepository.count());
    }

    @Test
    void update(){
        savedEntity.setAuthor("a2");
        reviewRepository.save(savedEntity);
        ReviewEntity foundEntity = reviewRepository.findById(savedEntity.getId()).get();
        assertEquals(1,foundEntity.getVersion());
        assertEquals("a2", foundEntity.getAuthor());
    }


    @Test
    void delete() {
        reviewRepository.delete(savedEntity);
        assertFalse(reviewRepository.existsById(savedEntity.getId()));
    }

    @Test
    void findByProductId(){
        List<ReviewEntity> reviewEntityList = reviewRepository.findByProductId(savedEntity.getProductId());
        assertThat(reviewEntityList,hasSize(1));
        assertEqualsReview(savedEntity, reviewEntityList.get(0));
    }


    @Test
    void duplicateError() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            ReviewEntity entity = new ReviewEntity(1, 2, "a", "s", "c");
            reviewRepository.save(entity);
        });

    }


    @Test
    void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        ReviewEntity entity1 = reviewRepository.findById(savedEntity.getId()).get();
        ReviewEntity entity2 = reviewRepository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setAuthor("a1");
        reviewRepository.save(entity1);

        // Update the entity using the second entity object.
        // This should fail since the second entity now holds an old version number, i.e. an Optimistic Lock Error
        assertThrows(OptimisticLockingFailureException.class, () -> {
            entity2.setAuthor("a2");
            reviewRepository.save(entity2);
        });

        // Get the updated entity from the database and verify its new sate
        ReviewEntity updatedEntity = reviewRepository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("a1", updatedEntity.getAuthor());
    }


    private void assertEqualsReview(ReviewEntity expectedEntity, ReviewEntity actualEntity) {
        assertEquals(expectedEntity.getId(),        actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),   actualEntity.getVersion());
        assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
        assertEquals(expectedEntity.getReviewId(),  actualEntity.getReviewId());
        assertEquals(expectedEntity.getAuthor(),    actualEntity.getAuthor());
        assertEquals(expectedEntity.getSubject(),   actualEntity.getSubject());
        assertEquals(expectedEntity.getContent(),   actualEntity.getContent());

    }


}
