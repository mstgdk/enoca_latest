package com.enoca.controller;

import com.enoca.DTO.message.ResponseMessage;
import com.enoca.DTO.request.DateRequest;
import com.enoca.DTO.request.OrderRequest;
import com.enoca.DTO.response.EnocaResponse;
import com.enoca.DTO.response.OrderResponse;
import com.enoca.service.OrderService;
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
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    //Order oluşturma
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnocaResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        orderService.createOrder(orderRequest);

        EnocaResponse response = new EnocaResponse();
        response.setMessage(ResponseMessage.ORDER_SAVED_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);
    }

    //Order listeleme
    @GetMapping("/pages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> getAllWithPage(@RequestParam("page") int page,
                                                              @RequestParam("size") int size,
                                                              @RequestParam("sort") String prop,
                                                              @RequestParam(value = "direction",
                                                                      required = false,
                                                                      defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
        Page<OrderResponse> orderResponse = orderService.findAllWithPage(pageable);
        return ResponseEntity.ok(orderResponse);
    }

    //Get one By Id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse orderResponse = orderService.getOrderById(id);
        return ResponseEntity.ok(orderResponse);
    }
    // Güncelleme
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnocaResponse>updateOrder(@PathVariable Long id, @Valid @RequestBody OrderRequest orderRequest){
        orderService.updateOrder(id, orderRequest);

        EnocaResponse response = new EnocaResponse();
        response.setMessage(ResponseMessage.ORDER_UPDATED_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);

    }
    // Delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnocaResponse>deleteOrder(@PathVariable Long id){
        orderService.deleteOrder(id);

        EnocaResponse response = new EnocaResponse();
        response.setMessage(ResponseMessage.ORDER_DELETED_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);

    }
    //Bir servis olmalı ve parametre olarak verilen tarihten sonra oluşturulmuş siparişleri listelesin
    @GetMapping("/date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>>getOrdersAfterGivenDate(@RequestBody DateRequest dateRequest){
        List<OrderResponse>orderResponseList= orderService.getOrdersAfterGivenDate(dateRequest);
        return ResponseEntity.ok(orderResponseList);
    }
}
