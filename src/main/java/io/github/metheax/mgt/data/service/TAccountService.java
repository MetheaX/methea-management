package io.github.metheax.mgt.data.service;

import io.github.metheax.domain.entity.TAccount;

import io.github.metheax.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class TAccountService extends CrudService<TAccount, String> {

    private AccountRepository repository;

    public TAccountService(@Autowired AccountRepository repository) {
        this.repository = repository;
    }

    public List<TAccount> getAllActiveAccount() {
        return repository.findAll();
    }

    public TAccount disableAccount(TAccount account) {
        account.setStatus("I");
        return repository.save(account);
    }

    public TAccount enableAccount(TAccount account) {
        account.setStatus("A");
        return repository.save(account);
    }

    @Override
    protected AccountRepository getRepository() {
        return repository;
    }
}
