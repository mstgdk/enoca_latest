package com.enoca.mapper;

import com.enoca.DTO.response.CustomerResponse;
import com.enoca.DTO.response.LikeResponse;
import com.enoca.domain.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {




    CustomerResponse customerToCustomerResponse(Customer customer);


    List<LikeResponse> map(List<Customer>customers);

    List<CustomerResponse> map2(List<Customer>customer);
}
