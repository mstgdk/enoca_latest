package com.enoca.controller;

import com.enoca.DTO.message.ResponseMessage;
import com.enoca.DTO.request.CustomerRequest;
import com.enoca.DTO.request.CustomerUpdateRequest;
import com.enoca.DTO.request.LikeRequest;
import com.enoca.DTO.response.CustomerResponse;
import com.enoca.DTO.response.EnocaResponse;
import com.enoca.DTO.response.LikeResponse;
import com.enoca.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    //CUSTOMER CREATE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnocaResponse> createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        customerService.createCustomer(customerRequest);

        EnocaResponse response = new EnocaResponse();
        response.setMessage(ResponseMessage.CUSTOMER_SAVED_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);
    }
    // listeleme
    @GetMapping("/pages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CustomerResponse>> getAllWithPage(@RequestParam("page") int page,
                                                                 @RequestParam("size") int size,
                                                                 @RequestParam("sort") String prop,
                                                                 @RequestParam(value = "direction",
                                                                         required = false,
                                                                         defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
        Page<CustomerResponse> customerResponse = customerService.findAllWithPage(pageable);
        return ResponseEntity.ok(customerResponse);
    }

    // güncelleme
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnocaResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        customerService.customerUpdate(id, customerUpdateRequest);

        EnocaResponse response = new EnocaResponse();
        response.setMessage(ResponseMessage.CUSTOMER_UPDATED_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);
    }

    //customer silme işlemi
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnocaResponse> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);

        EnocaResponse response = new EnocaResponse();
        response.setMessage(ResponseMessage.CUSTOMER_DELETED_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);

    }

    /* 2- Bir servis olmalı ve bir kelime yada harf değerini parametre olarak alsın ve isminin
içerisinde bu değer geçen müşteri ve müşteriye ait sipariş id sini getirsin.   */
    @GetMapping("/like")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LikeResponse>> like(@Valid @RequestBody LikeRequest likeRequest) {
        List<LikeResponse> likeList = customerService.likeName(likeRequest);
        return ResponseEntity.ok(likeList);
    }


    //3- Bir servis olmalı ve siparişi olmayan müşterileri listesin
    @GetMapping("/noorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerResponse>>getCustomersNotHavingOrder(){
        List<CustomerResponse> customerResponse = customerService.getCustomersNotHavingOrder();

        return ResponseEntity.ok(customerResponse);
    }
}
