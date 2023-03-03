package edu.neu.coe.csye6225.webapp.service;

import java.util.Map;
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

	@Autowired
	ImageService imageService;

	public Product createProduct(Product product, String userName)
			throws UserAuthrizationExeception, InvalidInputException {
		// TODO Auto-generated method stub
		User userobj = userService.loadUserByUsername(userName);
		if (userobj != null) {
			checkSku(1L, userobj.getId(), product.getSku(), "PostCheck");
			product.setOwnerUserId(userobj.getId());
			productRepo.saveAndFlush(product);
			return product;
		}
		// throw auth error
		throw new UserAuthrizationExeception("UnAuthrized username dose not exists");
	}

	public Product checkSku(Long id, Long ownerId, String sku, String check) throws InvalidInputException {
		Product p;
		if (check.equals("PostCheck"))
			p = productRepo.findProductByownerUserIdAndSku(ownerId, sku);
		else
			p = productRepo.checkProductSku(id, ownerId, sku);
		System.out.println(p);
		if (p == null)
			return p;
		throw new InvalidInputException("SKU Value Exists already");
	}

	public Product getProduct(Long productId) throws DataNotFoundExeception {
		Optional<Product> product = productRepo.findById(productId);
		if (product.isPresent())
			return product.get();
		throw new DataNotFoundExeception("No product associated with given Id: " + productId);
	}

	public String updateProductDetails(Long productId, Product product)
			throws DataNotFoundExeception, InvalidInputException {
		Product p = getProduct(productId);
		checkSku(p.getId(), p.getOwnerUserId(), product.getSku(), "PutCheck");
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
		Product p = getProduct(productId);
		productRepo.deleteById(p.getId());
		System.out.println("Inside deleteProductDetails "+productId);
		imageService.deleteImageByProductId(productId, p.getOwnerUserId());
		System.out.println("Inside product delete service 80");
		return "Deleted Product";
	}

	public String patchProductDetails(Long productId, Map<String, Object> updates)
			throws DataNotFoundExeception, InvalidInputException {
		Product p = getProduct(productId);
		if (updates.size() == 0)
			throw new InvalidInputException("Request can't be empty");
		for (Map.Entry<String, Object> map : updates.entrySet()) {
			switch (map.getKey()) {
			case "name":
				String name = (String) map.getValue();
				if (name.isBlank() || name.isEmpty() || name == null)
					throw new InvalidInputException("Product Name can't be null/empty");
				else
					p.setName(name);
				break;
			case "description":
				String description = (String) map.getValue();
				if (description.isBlank() || description.isEmpty() || description == null)
					throw new InvalidInputException("Product description can't be null/empty");
				p.setDescription(description);
				break;
			case "sku":
				String sku = (String) map.getValue();
				if (sku.isBlank() || sku.isEmpty() || sku == null)
					throw new InvalidInputException("Product SKU can't be null/empty");
				checkSku(p.getId(), p.getOwnerUserId(), sku, "PostCheck");
				p.setSku(sku);
				break;
			case "manufacture":
				String manufacture = (String) map.getValue();
				if (manufacture.isBlank() || manufacture.isEmpty() || manufacture == null)
					throw new InvalidInputException("Product manufacture can't be null/empty");
				p.setManufacturer(manufacture);
				break;
			case "quantity":
//				String qString = String.valueOf(map.getValue());
//				Integer quantity=Integer.parseInt(qString)
				int quantity=0;
				if (quantity < 0 || quantity > 100)
					throw new InvalidInputException("Product quantity should be btw 1 and 100");
				p.setQuantity(quantity);
				break;
			}
		}
		productRepo.saveAndFlush(p);
		return "Updated Product Details";
	}

}
