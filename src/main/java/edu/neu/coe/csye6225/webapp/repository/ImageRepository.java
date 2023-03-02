package edu.neu.coe.csye6225.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.neu.coe.csye6225.webapp.model.Image;


@Repository
public interface ImageRepository extends JpaRepository<Image, Long>{

	@Query(value="select * from image where product_id =?1",
			nativeQuery = true)
    List<Image> findImageByProductId(Long productId);
}
