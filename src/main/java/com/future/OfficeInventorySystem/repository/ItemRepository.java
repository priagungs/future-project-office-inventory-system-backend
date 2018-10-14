package com.future.OfficeInventorySystem.repository;

import com.future.OfficeInventorySystem.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Item findByIdItem(Long idItem);
    Page<Item> findAllByItemName(String itemName, Pageable pageable);
    Page<Item> findAllByAvailableQtyGreaterThan(Integer min, Pageable pageable);

}
