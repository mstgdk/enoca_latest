package com.enoca.domain.enums;

public enum RoleTypes {
    ROLE_CUSTOMER("Customer"),

    ROLE_ADMIN("Admin");

    private String name;

    private RoleTypes(String name){
        this.name=name;
    }

    public String getName(){
        return name;
    }
}
