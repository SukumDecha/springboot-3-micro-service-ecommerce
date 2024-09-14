package me.sukumdev.ecommerce.customer;

import org.springframework.data.mongodb.repository.MongoRepository;

//Customer as data, String as id
public interface CustomerRepository extends MongoRepository<Customer, String> {
}
