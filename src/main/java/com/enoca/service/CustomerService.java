package com.enoca.service;

import com.enoca.DTO.request.CustomerRequest;
import com.enoca.DTO.request.CustomerUpdateRequest;
import com.enoca.DTO.request.LikeRequest;
import com.enoca.DTO.response.CustomerResponse;
import com.enoca.DTO.response.LikeResponse;
import com.enoca.domain.Customer;
import com.enoca.domain.Order;
import com.enoca.exception.ResourceNotFoundException;
import com.enoca.exception.message.ErrorMessage;
import com.enoca.mapper.CustomerMapper;
import com.enoca.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final OrderService orderService;


    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper, OrderService orderService) {
        this.customerRepository = customerRepository;

        this.customerMapper = customerMapper;
        this.orderService = orderService;
    }
    public void createCustomer(CustomerRequest customerRequest) {
        Customer customer = new Customer();
        customer.setName(customerRequest.getName());
        customer.setAge(customerRequest.getAge());

        customerRepository.save(customer);

        List<Order> orderList = orderService.findAll(customer.getId()); // *****
        customer.setOrders(orderList);//*****
    }
    public Page<CustomerResponse> findAllWithPage(Pageable pageable) {
        Page<Customer> customerPage = customerRepository.findAll(pageable);

        return customerPage.map(customer -> customerMapper.customerToCustomerResponse(customer));
    }

    public void customerUpdate(Long id, CustomerUpdateRequest customerUpdateRequest) {
        //Böyle bir  customer mevcut mu?
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION)));
        customer.setName(customerUpdateRequest.getName());
        customer.setAge(customerUpdateRequest.getAge());

        customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        //Böyle bir  customer mevcut mu?
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION)));

        customerRepository.deleteById(id);
    }

    public List<LikeResponse> likeName(LikeRequest likeRequest) {
        List<Customer> likeList = customerRepository.findByLike(likeRequest.getLike());

        List<LikeResponse> likeResponse = customerMapper.map(likeList);

        for (LikeResponse w : likeResponse) {

            List<Order> orderList = orderService.findAll(w.getId());
            List<Long> orderIdList = new ArrayList<>();
            for (Order x : orderList) {

                orderIdList.add(x.getId());
            }
            w.setOrderId(orderIdList);
        }

        return likeResponse;
    }

    public List<CustomerResponse> getCustomersNotHavingOrder() {
        List<Customer> customerList = customerRepository.findAll();

        List<Customer> notHavingOrderCustomerList = new ArrayList<>();

        for (Customer w : customerList) {
            if (w.getOrders().isEmpty()) {
                notHavingOrderCustomerList.add(w);
            }
        }
        List<CustomerResponse> customerResponse = customerMapper.map2(notHavingOrderCustomerList);
        return  customerResponse;
    }
}
