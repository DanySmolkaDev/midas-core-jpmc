package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public void processTransaction(Transaction t) {
        UserRecord sender = userRepository.findById(t.getSenderId()).orElse(null);
        UserRecord recipient = userRepository.findById(t.getRecipientId()).orElse(null);

        if(sender != null && recipient != null) {
            Incentive inc = restTemplate.postForObject("http://localhost:8090/incentive", t, Incentive.class);
            float bonus = (inc != null) ? inc.getAmount() : 0f;

            if(sender.getBalance() >= t.getAmount()) {
                sender.setBalance(sender.getBalance() - t.getAmount());
                recipient.setBalance(recipient.getBalance() + t.getAmount() + bonus);

                TransactionRecord transaction = new TransactionRecord(sender, recipient, t.getAmount());

                userRepository.save(sender);
                userRepository.save(recipient);
                transactionRepository.save(transaction);
            }
        }
    }
}
