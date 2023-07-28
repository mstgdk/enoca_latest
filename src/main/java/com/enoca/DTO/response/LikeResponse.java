package com.enoca.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponse {
    private Long id;


    private String name;

    private int age;

    private List<Long> orderId;
}
