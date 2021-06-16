package io.github.metheax.mgt.data.service;

import io.github.metheax.mgt.data.entity.TGroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class TGroupService extends CrudService<TGroup, Integer> {

    private TGroupRepository repository;

    public TGroupService(@Autowired TGroupRepository repository) {
        this.repository = repository;
    }

    @Override
    protected TGroupRepository getRepository() {
        return repository;
    }

}
