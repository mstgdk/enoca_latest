package com.enoca.repository;

import com.enoca.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    @Query(value = "select * from t_customer where t_customer.name like %:like%", nativeQuery = true)
    List<Customer> findByLike(@Param("like") String like);
}
