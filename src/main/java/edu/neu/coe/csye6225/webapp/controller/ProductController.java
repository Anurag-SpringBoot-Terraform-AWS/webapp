package edu.neu.coe.csye6225.webapp.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import edu.neu.coe.csye6225.webapp.constants.UserConstants;
import edu.neu.coe.csye6225.webapp.exeception.DataNotFoundExeception;
import edu.neu.coe.csye6225.webapp.exeception.InvalidInputException;
import edu.neu.coe.csye6225.webapp.exeception.UserAuthrizationExeception;
import edu.neu.coe.csye6225.webapp.model.Product;
import edu.neu.coe.csye6225.webapp.service.AuthService;
import edu.neu.coe.csye6225.webapp.service.ProductService;
import edu.neu.coe.csye6225.webapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
@RestController()
@RequestMapping("v1/product")
public class ProductController {

	@Autowired
	ProductService productService;

	@Autowired
	UserService userService;

	@Autowired
	AuthService authservice;

	@RestControllerAdvice
	public class MyExceptionHandler {
	    @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
	        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
	        String errorMessage = fieldErrors.stream()
	                .map(FieldError::getDefaultMessage)
	                .collect(Collectors.joining(", "));
	        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
	    }
	}

	@PostMapping()
	public ResponseEntity<?> createProduct(@Valid @RequestBody Product product,
			HttpServletRequest request) {
		try {
			return new ResponseEntity<Product>(
					productService.createProduct(product,
							authservice.getUserNameFromToken(request.getHeader("Authorization").split(" ")[1])),
					HttpStatus.CREATED);
		} 
		catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} 
		catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{productId}")
	public ResponseEntity<?> getProduct(@PathVariable("productId") Long productId) {
		try {
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			return new ResponseEntity<Product>(productService.getProduct(productId), HttpStatus.OK);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping(value = "/{productId}")
	public ResponseEntity<?> updateUserDetails(@PathVariable("productId") Long productId,
			@Valid @RequestBody Product product, HttpServletRequest request, Errors error) {
		try {
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			authservice.isAuthorised(productService.getProduct(productId).getOwnerUserId(),
					request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<String>(productService.updateProductDetails(productId, product),
					HttpStatus.NO_CONTENT);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PatchMapping(value = "/{productId}")
	public ResponseEntity<?> patchUserDetails(@PathVariable("productId") Long productId,
			@Valid @RequestBody Product product, HttpServletRequest request, Errors error) {
		try {
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			authservice.isAuthorised(productService.getProduct(productId).getOwnerUserId(),
					request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<String>(productService.updateProductDetails(productId, product),
					HttpStatus.NO_CONTENT);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@DeleteMapping(value = "/{productId}")
	public ResponseEntity<?> deleteUserDetails(@PathVariable("productId") Long productId, HttpServletRequest request) {
		try {
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			authservice.isAuthorised(productService.getProduct(productId).getOwnerUserId(),
					request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<String>(productService.deleteProductDetails(productId),
					HttpStatus.NO_CONTENT);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
