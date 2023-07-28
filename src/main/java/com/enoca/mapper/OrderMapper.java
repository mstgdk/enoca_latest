package com.enoca.mapper;

import com.enoca.DTO.response.OrderResponse;
import com.enoca.domain.Customer;
import com.enoca.domain.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "customer", target = "customerId", qualifiedByName = "customerId")
    OrderResponse orderToOrderResponse(Order order);

    @Named("customerId")
    public static Long getCustomerId(Customer customer) {
        return customer.getId();
    }

    List<OrderResponse> map(List<Order>orders);
}
