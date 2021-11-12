package com.example.demo.controller;

import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.InvenDAO;
import com.example.demo.dto.InvenDTO;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/inventory")
@ComponentScan(basePackages = { "com.example.demo" })
public class InventoryController {

	private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private InvenDAO invenDAO;

	@GetMapping("/")
	@Hidden
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@Operation(summary = "Prodcuts list")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }) })
	@GetMapping("/products")
	public Set<String> productList() {
		return redisTemplate.keys("*");
	}

	@Operation(summary = "Product Order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product Order Success", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }),
			@ApiResponse(responseCode = "500", description = "Data not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))) })
	@PostMapping("/order")
	public String order(@Valid @RequestBody InvenDTO prod) {
		HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
		String productNo = prod.getProductNo();
		int numOfprod = this.count(productNo);

		logger.info("Current number of product : %", numOfprod);

		if (numOfprod > 0) {
			int number = numOfprod - prod.getNumOfProd();
			hashOperations.put(productNo, "number", Integer.toString(number));
			
			invenDAO.setProdData(productNo, number);
			
			return productNo + " is ordered";
		} else {
			return productNo + " is not available";
		}
	}

	@Operation(summary = "Product order cancel")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Cancel Success", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }) })
	@PostMapping("/cancel")
	public String cancel(@Valid @RequestBody InvenDTO prod) {
		HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
		String productNo = prod.getProductNo();
		int numOfprod = this.count(productNo);
		
		int number = numOfprod + prod.getNumOfProd();
		hashOperations.put(productNo, "number", Integer.toString(number));
		
		invenDAO.setProdData(productNo, number);
		
		return productNo + " is cancelled";
	}

	@Operation(summary = "The number of products")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }) })
	@GetMapping("/count")
	public int count(@RequestParam String productNo) {
		HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
		Map<Object, Object> entries = hashOperations.entries(productNo);
		return Integer.valueOf((String) entries.get("number"));
	}

	@Operation(summary = "Register a new products")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }) })
	@GetMapping("/registration")
	public void CreateNewProduct(@RequestParam String productNo, @RequestParam int numOfProd) {
		HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
		hashOperations.put(productNo, "number", Integer.toString(numOfProd));
		
		invenDAO.insertProdData(productNo, numOfProd);
	}

}
