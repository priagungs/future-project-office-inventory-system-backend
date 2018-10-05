package com.future.OfficeInventorySystem.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
@Table(name="Transaction")
@TableGenerator(name = "transaction_generator" , initialValue = 18216000)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "transaction_generator")
    private Long idTransaction;

    private Date transactionDate;

    private String supplier;

    private Long idAdmin;

    private ItemTransaction itemTransaction;

}
