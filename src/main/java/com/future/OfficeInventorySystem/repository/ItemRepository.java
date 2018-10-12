package com.future.OfficeInventorySystem.repository;

import com.future.OfficeInventorySystem.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Item findByIdItem(Long idItem);
    Set<Item> findAllByItemName(String itemName);

    Set<Item> findAllByAvailableQtyGreaterThan(Integer min);


}