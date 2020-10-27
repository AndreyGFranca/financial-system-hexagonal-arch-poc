package com.github.andreygfranca.accountspayable.api.v1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.andreygfranca.accountspayable.api.v1.BasePath;
import com.github.andreygfranca.accountspayable.api.v1.dto.AccountPayableDTO;
import com.github.andreygfranca.accountspayable.api.v1.mapper.AccountPayableMapper;
import com.github.andreygfranca.accountspayable.domain.AccountPayable;
import com.github.andreygfranca.accountspayable.ports.usecase.CreateAccountPayableUseCase;

/**
 * @author Andrey Franca 
 */
@RestController
@RequestMapping(BasePath.BASE_PATH + "accounts-payable")
public class AccountPayableResourceAdapter {

    private static final AccountPayableMapper MAPPER = new AccountPayableMapper();

    private CreateAccountPayableUseCase useCase;

    public AccountPayableResourceAdapter(CreateAccountPayableUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<AccountPayableDTO> create(@RequestBody AccountPayableDTO accountPayableDTO) {
        var input = MAPPER.map(accountPayableDTO, AccountPayable.class);

        var output = useCase.execute(input);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MAPPER.map(output, accountPayableDTO));
    }

}