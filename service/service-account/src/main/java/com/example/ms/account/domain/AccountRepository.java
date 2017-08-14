package com.example.ms.account.domain;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {

	Account findByEmailOrMobile(String name);

}
