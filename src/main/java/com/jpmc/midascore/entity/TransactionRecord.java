package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private UserRecord sender;

    @ManyToOne
    private UserRecord receiver;

    private float amount;

    public TransactionRecord() {}

    public TransactionRecord(UserRecord sender, UserRecord receiver, float amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }


}
