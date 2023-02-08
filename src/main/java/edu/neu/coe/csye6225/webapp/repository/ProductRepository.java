package edu.neu.coe.csye6225.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.neu.coe.csye6225.webapp.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

	@Query(value="SELECT * FROM product p WHERE p.owner_user_id = ?1 and p.sku=?2",
			nativeQuery = true)
    Product findProductByownerUserIdAndSku(Long ownerUserId,String sku);
	
	@Query(value="SELECT * FROM product p WHERE p.id!=?1 and p.owner_user_id = ?2 and p.sku=?3",
			nativeQuery = true)
    Product checkProductSku(Long id,Long ownerUserId,String sku);


}
