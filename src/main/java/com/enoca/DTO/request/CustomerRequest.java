package com.enoca.DTO.request;

import com.enoca.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {
    @NotNull(message = "isim boş olamaz")
    private String name;

    @NotNull(message = "age boş olamaz")
    private int age;

    private List<Order> orders;
}
