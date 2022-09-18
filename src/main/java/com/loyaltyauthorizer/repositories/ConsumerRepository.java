package com.loyaltyauthorizer.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.loyaltyauthorizer.models.Consumer;

@Repository
public interface ConsumerRepository extends MongoRepository<Consumer, String> {
    
    Page<Consumer> findAll(Pageable pageable);
    
}
