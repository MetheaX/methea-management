package io.github.metheax.mgt.data.service;

import io.github.metheax.domain.entity.TAccount;

import io.github.metheax.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class TAccountService extends CrudService<TAccount, String> {

    private AccountRepository repository;

    public TAccountService(@Autowired AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    protected AccountRepository getRepository() {
        return repository;
    }
}
