package com.finfellows.domain.product.application;

import com.finfellows.domain.bookmark.domain.FinancialProductBookmark;
import com.finfellows.domain.bookmark.domain.repository.FinancialProductBookmarkRepository;
import com.finfellows.domain.product.domain.CMA;
import com.finfellows.domain.product.domain.FinancialProduct;
import com.finfellows.domain.product.domain.FinancialProductOption;
import com.finfellows.domain.product.domain.FinancialProductType;
import com.finfellows.domain.product.domain.repository.CmaRepository;
import com.finfellows.domain.product.domain.repository.FinancialProductOptionRepository;
import com.finfellows.domain.product.domain.repository.FinancialProductRepository;
import com.finfellows.domain.product.dto.condition.CmaSearchCondition;
import com.finfellows.domain.product.dto.condition.FinancialProductSearchCondition;
import com.finfellows.domain.product.dto.response.*;
import com.finfellows.domain.product.exception.InvalidFinancialProductException;
import com.finfellows.domain.product.exception.ProductTypeMismatchException;
import com.finfellows.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class FinancialProductServiceImpl implements FinancialProductService {

    private final FinancialProductRepository financialProductRepository;
    private final FinancialProductOptionRepository financialProductOptionRepository;
    private final FinancialProductBookmarkRepository financialProductBookmarkRepository;
    private final CmaRepository cmaRepository;

    @Override
    public Page<SearchFinancialProductRes> findDepositProducts(final UserPrincipal userPrincipal, final FinancialProductSearchCondition financialProductSearchCondition, final Pageable pageable) {
        return financialProductRepository.findFinancialProducts(financialProductSearchCondition, pageable, FinancialProductType.DEPOSIT, userPrincipal.getId());
    }

    @Override
    public Page<SearchFinancialProductRes> findSavingProducts(final UserPrincipal userPrincipal, final FinancialProductSearchCondition financialProductSearchCondition, final Pageable pageable) {
        return financialProductRepository.findFinancialProducts(financialProductSearchCondition, pageable, FinancialProductType.SAVING, userPrincipal.getId());
    }

    @Override
    public DepositDetailRes getDepositDetail(final UserPrincipal userPrincipal, final Long depositId) {
        FinancialProduct deposit = financialProductRepository.findById(depositId)
                .orElseThrow(InvalidFinancialProductException::new);

        Optional<FinancialProductBookmark> bookmark = financialProductBookmarkRepository
                .findByUserAndFinancialProduct(userPrincipal.getUser(), deposit);

        if(!deposit.getFinancialProductType().equals(FinancialProductType.DEPOSIT))
            throw new ProductTypeMismatchException();

        List<FinancialProductOption> depositOptions = financialProductOptionRepository.findFinancialProductOptionsByFinancialProduct(deposit);
        List<Integer> terms = depositOptions.stream()
                .map(FinancialProductOption::getSavingsTerm)
                .distinct()
                .sorted()
                .toList();

        FinancialProductOption maxOption = depositOptions.stream()
                .max(Comparator.comparing(FinancialProductOption::getMaximumPreferredInterestRate))
                .orElse(null);

        return DepositDetailRes.toDto(bookmark, deposit, maxOption, terms);
    }

    @Override
    public SavingDetailRes getSavingDetail(final UserPrincipal userPrincipal, final Long savingId) {
        FinancialProduct saving = financialProductRepository.findById(savingId)
                .orElseThrow(InvalidFinancialProductException::new);

        Optional<FinancialProductBookmark> bookmark = financialProductBookmarkRepository
                .findByUserAndFinancialProduct(userPrincipal.getUser(), saving);

        if(!saving.getFinancialProductType().equals(FinancialProductType.SAVING))
            throw new ProductTypeMismatchException();

        List<FinancialProductOption> savingOptions = financialProductOptionRepository.findFinancialProductOptionsByFinancialProduct(saving);
        List<Integer> terms = savingOptions.stream()
                .map(FinancialProductOption::getSavingsTerm)
                .distinct()
                .sorted()
                .toList();

        FinancialProductOption maxOption = savingOptions.stream()
                .max(Comparator.comparing(FinancialProductOption::getMaximumPreferredInterestRate))
                .orElse(null);

        return SavingDetailRes.toDto(bookmark, saving, maxOption, terms);
    }

    @Override
    public List<String> findBanks(final String bankGroupNo) {
        return financialProductRepository.findBanks(bankGroupNo);
    }

    @Override
    public Page<SearchCmaRes> findCmaProducts(UserPrincipal userPrincipal, CmaSearchCondition cmaSearchCondition, Pageable pageable) {
        return financialProductRepository.findCmaProducts(cmaSearchCondition, pageable, userPrincipal.getId());
    }

    @Override
    public CmaDetailRes getCmaDetail(UserPrincipal userPrincipal, Long cmaId) {
        CMA cma = cmaRepository.findById(cmaId)
                .orElseThrow(InvalidFinancialProductException::new);

        return CmaDetailRes.toDto(cma);
    }

}