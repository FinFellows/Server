package com.finfellows.domain.bookmark.application;

import com.finfellows.domain.bookmark.domain.FinancialProductBookmark;
import com.finfellows.domain.bookmark.domain.repository.FinancialProductBookmarkRepository;
import com.finfellows.domain.bookmark.dto.FinancialProductBookmarkRes;
import com.finfellows.domain.product.domain.FinancialProduct;
import com.finfellows.domain.product.domain.repository.FinancialProductRepository;
import com.finfellows.domain.user.domain.User;
import com.finfellows.domain.user.domain.repository.UserRepository;
import com.finfellows.global.config.security.token.UserPrincipal;
import com.finfellows.global.payload.Message;
import com.finfellows.global.payload.ResponseCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FinancialProductBookmarkServiceImpl implements BookmarkService{
    private final FinancialProductBookmarkRepository financialProductBookmarkRepository;
    private final UserRepository userRepository;
    private final FinancialProductRepository financialProductRepository;


    @Transactional
    @Override
    public Message insert(UserPrincipal userPrincipal, Long id) {
        Optional<User> optionalUser = userRepository.findByEmail(userPrincipal.getEmail());
        Optional<FinancialProduct> optionalFinancialProduct = financialProductRepository.findById(id);

        User user = optionalUser.get();
        FinancialProduct financialProduct = optionalFinancialProduct.get();


        FinancialProductBookmark financialProductBookmark = FinancialProductBookmark.builder()
                .user(user)
                .financialProduct(financialProduct)
                .build();

        financialProductBookmarkRepository.save(financialProductBookmark);

        return Message.builder()
                .message("즐겨찾기 추가에 성공했습니다.")
                .build();
    }

    @Transactional
    @Override
    public Message delete(UserPrincipal userPrincipal, Long id) {
        Optional<User> optionalUser = userRepository.findByEmail(userPrincipal.getEmail());
        Optional<FinancialProduct> optionalFinancialProduct = financialProductRepository.findById(id);

        User user = optionalUser.get();
        FinancialProduct financialProduct = optionalFinancialProduct.get();

        FinancialProductBookmark financialProductBookmark = financialProductBookmarkRepository.findByUserAndFinancialProduct(user, financialProduct).get();

        financialProductBookmarkRepository.delete(financialProductBookmark);


        return Message.builder()
                .message("즐겨찾기 삭제에 성공했습니다.")
                .build();
    }

    public ResponseCustom<List<FinancialProductBookmarkRes>> findBookmarks(UserPrincipal userPrincipal) {
        Optional<User> optionalUser = userRepository.findByEmail(userPrincipal.getEmail());

        User user = optionalUser.get();

        List<FinancialProductBookmark> bookmarks = financialProductBookmarkRepository.findAllByUser(user);


        List<FinancialProductBookmarkRes> financialProductBookmarkResList = FinancialProductBookmarkRes.toDto(bookmarks);


        return ResponseCustom.OK(financialProductBookmarkResList);
    }

}