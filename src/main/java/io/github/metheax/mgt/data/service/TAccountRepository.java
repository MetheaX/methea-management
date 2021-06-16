package io.github.metheax.mgt.data.service;

import io.github.metheax.mgt.data.entity.TAccount;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TAccountRepository extends JpaRepository<TAccount, Integer> {

}