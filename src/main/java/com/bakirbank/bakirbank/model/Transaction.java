package com.bakirbank.bakirbank.model;

import com.bakirbank.bakirbank.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
public class Transaction implements Serializable {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private Double amount;

    private String description;
}