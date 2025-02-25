package se.magnus.microservices.core.review.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.microservices.api.core.review.Review;
import se.magnus.microservices.core.review.persistace.ReviewEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {


    @Mapping(target = "serviceAddress", ignore = true)

    Review EntityToApi(ReviewEntity review);


    @Mappings({
            @Mapping(target = "id" , ignore = true),
            @Mapping(target = "version" , ignore = true),
    })
    ReviewEntity ApiToEntity(Review review);

    List<Review> entityListToApiList(List<ReviewEntity> entity);

    List<ReviewEntity> apiListToEntityList(List<Review> api);
}
