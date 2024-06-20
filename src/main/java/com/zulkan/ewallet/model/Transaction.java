package com.zulkan.ewallet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "transactions")
@NoArgsConstructor
public class Transaction extends BaseEntity {

    @Column
    private Integer sourceId;

    @Column
    private Integer destinationId;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column
    private Integer amount;


}
