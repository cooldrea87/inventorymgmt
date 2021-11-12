package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.InvenDAO;
import com.example.demo.dto.InvenDTO;

@RestController
@RequestMapping("/test")

public class DBConTestController {
	@Autowired
	private InvenDAO invenDAO;

	@GetMapping("/get")
	public List<InvenDTO> getProd() {
		return invenDAO.getProdData();
	}
	
	@GetMapping("/set")
    public int order(@RequestParam String productNo, @RequestParam int number) {
		return invenDAO.setProdData(productNo, number);
		
//		List<InvenDTO> setProdData(productNo, number);
   
    }

}
