package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public void processTransaction(Transaction t) {
        UserRecord sender = userRepository.findById(t.getSenderId());
        UserRecord recipient = userRepository.findById(t.getRecipientId());

        if(sender != null && recipient != null) {
            if(sender.getBalance() >= t.getAmount()) {
                sender.setBalance(sender.getBalance() - t.getAmount());
                recipient.setBalance(recipient.getBalance() + t.getAmount());

                TransactionRecord transaction = new TransactionRecord(sender, recipient, t.getAmount());

                userRepository.save(sender);
                userRepository.save(recipient);
                transactionRepository.save(transaction);
            }
        }
    }
}
