package com.example.service.appreciation.domain;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface AppreciationConfirmRepository extends PagingAndSortingRepository<AppreciationConfirm, Long> {

	Long countByAppreciationIdAndConfirmResult(Long appreciationId, AppreciationConfirmResult confirmResult);
	
	List<AppreciationConfirm> listByAppreciationIdAndConfirmResult(Long appreciationId, AppreciationConfirmResult confirmResult);
	
}
