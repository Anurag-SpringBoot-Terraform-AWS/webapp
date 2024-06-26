package edu.neu.coe.csye6225.webapp.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartFile;

import com.timgroup.statsd.StatsDClient;

import edu.neu.coe.csye6225.webapp.constants.UserConstants;
import edu.neu.coe.csye6225.webapp.exeception.BadInputException;
import edu.neu.coe.csye6225.webapp.exeception.DataNotFoundExeception;
import edu.neu.coe.csye6225.webapp.exeception.InvalidInputException;
import edu.neu.coe.csye6225.webapp.exeception.UserAuthrizationExeception;
import edu.neu.coe.csye6225.webapp.model.Image;
import edu.neu.coe.csye6225.webapp.model.Product;
import edu.neu.coe.csye6225.webapp.service.AuthService;
import edu.neu.coe.csye6225.webapp.service.ImageService;
import edu.neu.coe.csye6225.webapp.service.ProductService;
import edu.neu.coe.csye6225.webapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("v6/product")
public class ProductController {

	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
	@Autowired
	ProductService productService;

	@Autowired
	UserService userService;

	@Autowired
	AuthService authservice;

	@Autowired
	ImageService imageService;
	
	@Autowired
	private StatsDClient statsDClient;

	@RestControllerAdvice
	public class MyExceptionHandler {
		@ExceptionHandler(MethodArgumentNotValidException.class)
		public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
			List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
			String errorMessage = fieldErrors.stream().map(FieldError::getDefaultMessage)
					.collect(Collectors.joining(", "));
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping()
	public ResponseEntity<?> createProduct(@Valid @RequestBody Product product, HttpServletRequest request) {
		try {
			logger.info("Start of ProductController.createProduct with sku "+product.getSku());
    		statsDClient.incrementCounter("endpoint.createProduct.http.post");
			return new ResponseEntity<Product>(
					productService.createProduct(product,
							authservice.getUserNameFromToken(request.getHeader("Authorization").split(" ")[1])),
					HttpStatus.CREATED);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			logger.warn("Bad Request error in ProductController.createProduct with sku "+product.getSku());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
			logger.warn("Authorization error in ProductController.createProduct "+e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			logger.error("Server error in ProductController.createProduct "+e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{productId}")
	public ResponseEntity<?> getProduct(@PathVariable("productId") Long productId) {
		try {
			logger.info("Start of ProductController.getProduct with productId "+productId);
    		statsDClient.incrementCounter("endpoint.getProduct.http.get");
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			return new ResponseEntity<Product>(productService.getProduct(productId), HttpStatus.OK);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			logger.warn("Bad Request error in ProductController.getProduct with productId "+productId);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			logger.warn("Data Not found error in ProductController.getProduct with productId "+productId);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Server error in ProductController.getProduct "+e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping(value = "/{productId}")
	public ResponseEntity<?> updateProductDetails(@PathVariable("productId") Long productId,
			@Valid @RequestBody Product product, HttpServletRequest request, Errors error) {
		try {
			logger.info("Start of ProductController.updateProductDetails with productId "+productId);
    		statsDClient.incrementCounter("endpoint.updateProductDetails.http.put");
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			authservice.isAuthorised(productService.getProduct(productId).getOwnerUserId(),
					request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<String>(productService.updateProductDetails(productId, product),
					HttpStatus.NO_CONTENT);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			logger.warn("Bad Request error in ProductController.updateProductDetails with productId "+productId);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
			logger.warn("Authorization error in ProductController.updateProductDetails "+e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			logger.warn("Data Not found error in ProductController.updateProductDetails with productId "+productId);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Server error in ProductController.updateProductDetails "+e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PatchMapping(value = "/{productId}")
	public ResponseEntity<?> patchProductDetails(@PathVariable("productId") Long productId,
			@RequestBody Map<String, Object> updates, HttpServletRequest request) {
		try {
			logger.info("Start of ProductController.patchProductDetails with productId "+productId);
    		statsDClient.incrementCounter("endpoint.patchProductDetails.http.patch");
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			authservice.isAuthorised(productService.getProduct(productId).getOwnerUserId(),
					request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<String>(productService.patchProductDetails(productId, updates),
					HttpStatus.NO_CONTENT);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			logger.warn("Bad Request error in ProductController.patchProductDetails with productId "+productId);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
			logger.warn("Authorization error in ProductController.patchProductDetails "+e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			logger.warn("Data Not found error in ProductController.patchProductDetails with productId "+productId);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Server error in ProductController.patchProductDetails "+e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@DeleteMapping(value = "/{productId}")
	public ResponseEntity<?> deleteProductDetails(@PathVariable("productId") Long productId, HttpServletRequest request) {
		try {
			logger.info("Start of ProductController.deleteProductDetails with productId "+productId);
    		statsDClient.incrementCounter("endpoint.deleteProductDetails.http.delete");
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			authservice.isAuthorised(productService.getProduct(productId).getOwnerUserId(),
					request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<String>(productService.deleteProductDetails(productId), HttpStatus.NO_CONTENT);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			logger.warn("Bad Request error in ProductController.deleteProductDetails with productId "+productId);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
			logger.warn("Authorization error in ProductController.deleteProductDetails "+e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			logger.warn("Data Not found error in ProductController.deleteProductDetails with productId "+productId);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Server error in ProductController.deleteProductDetails "+e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/{product_id}/image", produces = "application/json", consumes = "multipart/form-data")
	public ResponseEntity<?> saveImage(@PathVariable("product_id") Long productId,
			@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		try {
			logger.info("Start of ProductController.saveImage with productId "+productId);
    		statsDClient.incrementCounter("endpoint.saveImage.http.post");
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			Long userId = productService.getProduct(productId).getOwnerUserId();
			authservice.isAuthorised(userId, request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<Image>(imageService.saveImage(productId, userId, file), HttpStatus.CREATED);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			logger.warn("Bad Request error in ProductController.saveImage with productId "+productId);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
			logger.warn("Authorization error in ProductController.saveImage "+e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Server error in ProductController.saveImage "+e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/{product_id}/image", produces = "application/json")
	public ResponseEntity<?> getAllImages(@PathVariable("product_id") Long productId, HttpServletRequest request) {
		try {
			logger.info("Start of ProductController.getAllImages with productId "+productId);
    		statsDClient.incrementCounter("endpoint.getAllImages.http.get");
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			Long userId = productService.getProduct(productId).getOwnerUserId();
			authservice.isAuthorised(userId, request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<List<Image>>(imageService.getAllImages(productId, userId), HttpStatus.OK);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			logger.warn("Bad Request error in ProductController.getAllImages with productId "+productId);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			logger.warn("Authorization error in ProductController.getAllImages "+e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Server error in ProductController.getAllImages "+e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/{product_id}/image/{image_id}", produces = "application/json")
	public ResponseEntity<?> getImage(@PathVariable("product_id") Long productId,
			@PathVariable("image_id") Long imageId, HttpServletRequest request) {
		try {
			logger.info("Start of ProductController.getImage with productId "+productId);
    		statsDClient.incrementCounter("endpoint.getImage.http.get");
			if (productId.toString().isBlank() || productId.toString().isEmpty() || imageId.toString().isBlank()
					|| imageId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id / ImageId");
			}
			Long userId = productService.getProduct(productId).getOwnerUserId();
			System.out.println(userId);
			authservice.isAuthorised(userId, request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<Image>(imageService.getImage(productId, userId, imageId), HttpStatus.OK);
		} catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			logger.warn("Bad Request error in ProductController.getImage with productId "+productId);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (BadInputException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			logger.warn("Authorization error in ProductController.getImage "+e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Server error in ProductController.getImage "+e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping(value = "/{product_id}/image/{image_id}", produces = "application/json")
	public ResponseEntity<?> deleteImage(@PathVariable("product_id") Long productId,
			@PathVariable("image_id") Long imageId, HttpServletRequest request) {
		try {
			logger.info("Start of ProductController.deleteImage with productId "+productId);
    		statsDClient.incrementCounter("endpoint.deleteImage.http.delete");
			if (productId.toString().isBlank() || productId.toString().isEmpty() || imageId.toString().isBlank()
					|| imageId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id / ImageId");
			}
			Long userId = productService.getProduct(productId).getOwnerUserId();
			authservice.isAuthorised(userId, request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<Image>(imageService.deleteImage(productId, userId, imageId), HttpStatus.NO_CONTENT);
		} catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			logger.warn("Bad Request error in ProductController.deleteImage with productId "+productId);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (BadInputException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			logger.warn("Authorization error in ProductController.deleteImage "+e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Server error in ProductController.deleteImage "+e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
