package com.enoca.security.service;

import com.enoca.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails { // Bu class ile USER --> USER DETAILSe çevrilir

    private String email;
    private String password;

    private Collection<? extends GrantedAuthority> authorities;// sevurity nin anladığı roller. birden çok olabileceği için collection

    // user ---> UserDetails
    public static UserDetailsImpl build(User user) { // parametre olarak pojo classım olan User
        List<SimpleGrantedAuthority> authorities = user.getRoles().// user ın rollerini aldık
                stream().//akış başladı
                map(role -> new SimpleGrantedAuthority(role.getType().name())).// roller-->grandAuthority  türüne çevrildi
                collect(Collectors.toList());

        return new UserDetailsImpl(user.getEmail(),user.getPassword(),authorities);//contructor kullanarak UserDetailsImpl türünde user oluşturduk

    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
