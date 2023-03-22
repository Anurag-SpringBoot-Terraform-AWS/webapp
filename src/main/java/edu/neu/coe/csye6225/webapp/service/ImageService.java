package edu.neu.coe.csye6225.webapp.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import edu.neu.coe.csye6225.webapp.exeception.BadInputException;
import edu.neu.coe.csye6225.webapp.exeception.DataNotFoundExeception;
import edu.neu.coe.csye6225.webapp.exeception.InvalidInputException;
import edu.neu.coe.csye6225.webapp.model.Image;
import edu.neu.coe.csye6225.webapp.repository.ImageRepository;

@Service
public class ImageService {

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
	@Value("${aws.s3.bucketName}")
	private String bucketName;
	
	@Autowired
	private FileStore fileStore;

	@Autowired
	private ImageRepository imageRepository;
	
	@Autowired
    private AmazonS3 amazonS3;

	public Image saveImage(Long productId, Long userId, MultipartFile file) throws InvalidInputException   {
		// check if the file is empty
		logger.info("Start of ImageService.saveImage with productId " + productId);
		if (file.isEmpty()) {
			throw new InvalidInputException("Cannot upload empty file");
		}
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(file.getSize());
		objectMetadata.setContentType(file.getContentType());
		objectMetadata.setCacheControl("public, max-age=31536000");
		String path = String.format("%s/%s/%s", bucketName, userId, productId);
		String fileName = String.format("%s/%s",String.valueOf(UUID.randomUUID()), file.getOriginalFilename());
		try {
			amazonS3.putObject(path, fileName,  file.getInputStream(), objectMetadata);
		} catch (IOException e) {
			System.out.println(e);
			throw new IllegalStateException("Failed to upload file", e);
		}
		Image image = Image.builder().productId(productId).fileName(fileName).s3BucketPath(String.valueOf(amazonS3.getUrl(path, fileName))).build();
		return imageRepository.saveAndFlush(image);
	}

	public List<Image> getAllImages(Long productId, Long userId) throws DataNotFoundExeception {
		// TODO Auto-generated method stub
		logger.info("Start of ImageService.getAllImages with productId " + productId);
		List<Image> images=imageRepository.findImageByProductId(productId);
		if(images==null)
			throw new DataNotFoundExeception("No Product with given Id");
		return images;
	}

	public Image getImage(Long productId, Long userId, Long imageId) throws DataNotFoundExeception, BadInputException {
		// TODO Auto-generated method stub
		logger.info("Start of ImageService.getImage with productId " + productId);
		Optional<Image> imgObj=imageRepository.findById(imageId);
		if(imgObj.isEmpty())
			throw new DataNotFoundExeception("No Image with given Id");
		if(imgObj.get().getProductId()!=productId)
			throw new BadInputException("ProductId and imageId won't match");
		return imgObj.get();
	}

	public Image deleteImage(Long productId, Long userId, Long imageId)  throws DataNotFoundExeception, BadInputException {
		// TODO Auto-generated method stub
		logger.info("Start of ImageService.deleteImage with productId " + productId);
		Optional<Image> imgObj=imageRepository.findById(imageId);
		if(imgObj.isEmpty())
			throw new DataNotFoundExeception("No Image with given Id");
		if(imgObj.get().getProductId()!=productId)
			throw new BadInputException("ProductId and imageId won't match");
		String path_name=String.format("%s/%s/%s", userId, productId,imgObj.get().getFileName());
		fileStore.deleteFile(bucketName,path_name);
		imageRepository.deleteById(imageId);
		return imgObj.get();
	
	}
	
	public void deleteImageByProductId(Long productId, Long userId)  throws DataNotFoundExeception {
		// TODO Auto-generated method stub
		logger.info("Start of ImageService.deleteImageByProductId with productId " + productId);
		String path = String.format("%s/%s/", userId, productId);
		String folderName = path;
		ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(folderName);
		ListObjectsV2Result objects = amazonS3.listObjectsV2(listObjectsRequest);
		List<S3ObjectSummary> summaries = objects.getObjectSummaries();
		for (S3ObjectSummary summary : summaries) {
			amazonS3.deleteObject(bucketName, summary.getKey());
		}
		amazonS3.deleteObject(bucketName, folderName);
	}

}
