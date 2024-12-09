package com.bakirbank.bakirbank.model;

import com.bakirbank.bakirbank.model.enums.AccountStatus;
import com.bakirbank.bakirbank.model.enums.AccountType;
import com.bakirbank.bakirbank.model.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String accountNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private Double balance;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private String branchCode;

    private String ownerName;

    private LocalDateTime createdDate;

    private LocalDateTime lastUpdatedDate;

    @OneToMany(mappedBy = "senderAccount", cascade = CascadeType.ALL)
    private List<Transaction> transactionsAsSender;

    @OneToMany(mappedBy = "receiverAccount", cascade = CascadeType.ALL)
    private List<Transaction> transactionsAsReceiver;
}