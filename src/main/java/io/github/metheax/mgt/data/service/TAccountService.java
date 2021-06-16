package io.github.metheax.mgt.data.service;

import io.github.metheax.mgt.data.entity.TAccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class TAccountService extends CrudService<TAccount, Integer> {

    private TAccountRepository repository;

    public TAccountService(@Autowired TAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    protected TAccountRepository getRepository() {
        return repository;
    }

}
