package io.github.metheax.mgt.data.service;

import io.github.metheax.domain.entity.TGroup;

import io.github.metheax.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class TGroupService extends CrudService<TGroup, String> {

    private GroupRepository repository;

    public TGroupService(@Autowired GroupRepository repository) {
        this.repository = repository;
    }

    @Override
    protected GroupRepository getRepository() {
        return repository;
    }

}
