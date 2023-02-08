package edu.neu.coe.csye6225.webapp.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.NumberFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Product {

	@Id
	@SequenceGenerator(
            name = "product_sequence",
            sequenceName = "product_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_sequence"
    )
	@Column(nullable = false)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

	@JsonProperty("name")
	@NotEmpty(message = "Product Name cannot be empty")
    private String name;
	
	@JsonProperty("description")
	@NotEmpty(message="Description cannot be empty")
    private String description;

	@JsonProperty("sku")
	@NotEmpty(message="sku cannot be empty")
    private String sku;


    @JsonProperty("manufacturer")
    @NotEmpty( message="manufacturer Name cannot be empty")
    private String manufacturer;
    
    @IntegerCheck
    @Min(value=1, message="Quantity must be greater than or equal to 1")
    @Max(value=100, message="Quantity must be less than or equal to 100")
    private Integer quantity;
    

    @JsonProperty(value ="date_added",access = JsonProperty.Access.READ_ONLY)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date_added")
    private LocalDateTime dateAdded;

    @JsonProperty(value = "date_last_updated",access = JsonProperty.Access.READ_ONLY)
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date_last_updated")
    private LocalDateTime dateLastUpdated;
    
    @JsonProperty(value="owner_user_id",access = JsonProperty.Access.READ_ONLY)
    @Column(name="owner_user_id")
    private Long ownerUserId;
}
