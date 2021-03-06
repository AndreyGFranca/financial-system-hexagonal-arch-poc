package com.github.andreygfranca.accountspayable.services;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.github.andreygfranca.accountspayable.domain.AccountPayable;
import com.github.andreygfranca.accountspayable.domain.Settlement;
import com.github.andreygfranca.accountspayable.ports.in.CreateAccountPayableUseCase;
import com.github.andreygfranca.accountspayable.ports.in.SettleAccountPayableUseCase;
import com.github.andreygfranca.accountspayable.ports.out.CreateAccountPayablePort;
import com.github.andreygfranca.accountspayable.ports.out.CreateSettlementPort;
import com.github.andreygfranca.accountspayable.ports.out.LoadAccountPayablePort;
import com.github.andreygfranca.accountspayable.ports.out.PublishSettlementToCashFlow;

/**
 * @author Andrey Franca 
 */
@Service
public class AccountPayableService implements CreateAccountPayableUseCase, SettleAccountPayableUseCase {

    private final LoadAccountPayablePort loadAccountPayablePort;
    private final CreateAccountPayablePort createAccountPayablePort;
    private final CreateSettlementPort createSettlementPort;
    private final PublishSettlementToCashFlow publishSettlementToCashFlow;

    public AccountPayableService(LoadAccountPayablePort loadAccountPayablePort, CreateAccountPayablePort createAccountPayablePort, CreateSettlementPort createSettlementPort, PublishSettlementToCashFlow publishSettlementToCashFlow) {
        this.loadAccountPayablePort = loadAccountPayablePort;
        this.createAccountPayablePort = createAccountPayablePort;
        this.createSettlementPort = createSettlementPort;
        this.publishSettlementToCashFlow = publishSettlementToCashFlow;
    }

    @Override
    public AccountPayable create(AccountPayable accountPayable) {
        return createAccountPayablePort.create(accountPayable);
    }

    @Override
    public Settlement settle(UUID accountPayableId, Settlement settlement) {
        Optional<AccountPayable> accountPayable = loadAccountPayablePort.load(accountPayableId);

        beforeCreate(accountPayable);

        settlement.setAccountPayable(accountPayable.get());

        Settlement settlementCreated = createSettlementPort.create(settlement);

        afterCreate(settlementCreated);

        return settlementCreated;
    }

    private void afterCreate(Settlement settlement) {
        publishSettlementToCashFlow.publish(settlement);
    }

    private void beforeCreate(Optional<AccountPayable> accountPayable) {
        if (accountPayable.isEmpty()) {
            throw new RuntimeException("Account payable do not exists.");
        }

        if (accountPayable.get().getMaturityDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("It is not possible to settle an account payable with an expired date.");
        }
    }
}
