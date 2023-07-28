package com.enoca.service;

import com.enoca.DTO.request.DateRequest;
import com.enoca.DTO.request.OrderRequest;
import com.enoca.DTO.response.CustomerResponse;
import com.enoca.DTO.response.OrderResponse;
import com.enoca.domain.Customer;
import com.enoca.domain.Order;
import com.enoca.exception.ResourceNotFoundException;
import com.enoca.exception.message.ErrorMessage;
import com.enoca.mapper.CustomerMapper;
import com.enoca.mapper.OrderMapper;
import com.enoca.repository.CustomerRepository;
import com.enoca.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;
    private final CustomerMapper customerMapper;


    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository, OrderMapper orderMapper, CustomerMapper customerMapper) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;


        this.orderMapper = orderMapper;

        this.customerMapper = customerMapper;
    }

    public void createOrder(OrderRequest orderRequest) {
        //hangi customer için order create ediyoruz?
        Customer customer =customerRepository.findById(orderRequest.getCustomerId()).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION)));



        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalPrice(orderRequest.getTotalPrice());
        orderRepository.save(order);
    }

    public Page<OrderResponse> findAllWithPage(Pageable pageable) {
        Page<Order> orderPage=orderRepository.findAll(pageable);

        return orderPage.map(order -> orderMapper.orderToOrderResponse(order));
    }


    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION)));

        Customer customer =customerRepository.findById(order.getCustomer().getId()).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION)));

        CustomerResponse customerResponse = customerMapper.customerToCustomerResponse(customer);

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setCreateDate(order.getCreateDate());
        orderResponse.setTotalPrice(order.getTotalPrice());
        orderResponse.setCustomerId(customer.getId());

        return orderResponse;
    }

    public void updateOrder(Long id, OrderRequest orderRequest) {
        Order order = orderRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION)));

        Customer customer =customerRepository.findById(orderRequest.getCustomerId()).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION)));

        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setCustomer(customer);
        orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION)));

        orderRepository.delete(order);
    }

    public List<OrderResponse> getOrdersAfterGivenDate(DateRequest dateRequest) {
        List<Order> givenDateOrders= orderRepository.findAllByDate(dateRequest.getGivenDate());

        return orderMapper.map(givenDateOrders);

    }

    public List<Order> findAll(Long id) {//******
        List<Order> orderList=orderRepository.findAllOrders(id);
        return orderList;
    }


}

