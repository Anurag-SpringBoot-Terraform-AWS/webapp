package edu.neu.coe.csye6225.webapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.neu.coe.csye6225.webapp.exeception.DataNotFoundExeception;
import edu.neu.coe.csye6225.webapp.exeception.InvalidInputException;
import edu.neu.coe.csye6225.webapp.exeception.UserAuthrizationExeception;
import edu.neu.coe.csye6225.webapp.model.Product;
import edu.neu.coe.csye6225.webapp.model.User;
import edu.neu.coe.csye6225.webapp.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	ProductRepository productRepo;
	
	@Autowired
	UserService userService;
	
	public Product createProduct(Product product,String userName) throws UserAuthrizationExeception, InvalidInputException {
		// TODO Auto-generated method stub
		User userobj=userService.loadUserByUsername(userName);
		if(userobj!=null) {
			checkSku(1L,userobj.getId(),product.getSku(),"PostCheck");
			product.setOwnerUserId(userobj.getId());
			productRepo.saveAndFlush(product);
			return product;
		}
		// throw auth error
		throw new UserAuthrizationExeception("UnAuthrized username dose not exists");
	}
	
	public Product checkSku(Long id,Long ownerId,String sku,String check) throws InvalidInputException {
		Product p;
		if(check.equals("PostCheck"))
			p=productRepo.findProductByownerUserIdAndSku(ownerId,sku);
		else
			p=productRepo.checkProductSku( id,ownerId, sku);
		if(p==null)
			return p;
		throw new InvalidInputException("SKU Value Exists already");
	}

	public Product getProduct(Long productId) throws DataNotFoundExeception {
		Optional<Product> product=productRepo.findById(productId);
		if(product.isPresent())
			return product.get();
		throw new DataNotFoundExeception("No product associated with given Id: "+productId);
	}

	public String updateProductDetails(Long productId,Product product) throws DataNotFoundExeception, InvalidInputException {
		Product p=getProduct(productId);
		checkSku(p.getId(), p.getOwnerUserId(),product.getSku(),"PutCheck");
		p.setId(productId);
		p.setName(product.getName());
		p.setDescription(product.getDescription());
		p.setSku(product.getSku());
		p.setManufacturer(product.getManufacturer());
		p.setQuantity(product.getQuantity());
		productRepo.saveAndFlush(p);
		return "Updated Product Details";
	}

	public String deleteProductDetails(Long productId) throws DataNotFoundExeception {
		Product p=getProduct(productId);
		productRepo.deleteById(p.getId());
		return "Deleted Product";
	}

	
}
