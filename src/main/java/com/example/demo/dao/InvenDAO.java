package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.InvenDTO;


@Mapper
public interface InvenDAO {
	
    public List<InvenDTO> getProdData();
    
    public int setProdData(String productNo, int number);
    
    public void insertProdData(String productNo, int number);
    
   }