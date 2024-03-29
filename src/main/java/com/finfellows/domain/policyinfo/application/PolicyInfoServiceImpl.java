package com.finfellows.domain.policyinfo.application;

import com.finfellows.domain.common.Status;
import com.finfellows.domain.policyinfo.domain.PolicyInfo;
import com.finfellows.domain.policyinfo.domain.repository.PolicyInfoRepository;
import com.finfellows.domain.policyinfo.dto.PolicyInfoDetailRes;
import com.finfellows.domain.policyinfo.dto.PolicyUpdateReq;
import com.finfellows.domain.policyinfo.dto.SearchPolicyInfoRes;
import com.finfellows.domain.policyinfo.exception.InvalidPolicyInfoException;
import com.finfellows.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PolicyInfoServiceImpl implements PolicyInfoService {

    private final PolicyInfoRepository policyInfoRepository;

    @Override
    public Page<SearchPolicyInfoRes> findPolicyInfos(UserPrincipal userPrincipal, String searchKeyword, Pageable pageable) {
        if (userPrincipal != null) {
            return policyInfoRepository.findPolicyInfosWithAuthorization(searchKeyword, pageable, userPrincipal.getId());
        }
        return policyInfoRepository.findPolicyInfos(searchKeyword, pageable);
    }

    @Override
    public PolicyInfoDetailRes findPolicyDetail(UserPrincipal userPrincipal, Long policyId) {
        if(userPrincipal != null){
            return policyInfoRepository.findPolicyDetailWithAuthorization(policyId, userPrincipal.getId());
        }
        return policyInfoRepository.findPolicyDetail(policyId);
    }

    @Override
    @Transactional
    public void deletePolicy(Long policyId) {
        PolicyInfo policy = policyInfoRepository.findById(policyId)
                .orElseThrow(InvalidPolicyInfoException::new);

        policy.updateStatus(Status.DELETE);
    }

    @Override
    @Transactional
    public void updatePolicy(Long policyId, PolicyUpdateReq policyUpdateReq) {
        PolicyInfo policyInfo = policyInfoRepository.findById(policyId)
                .orElseThrow(InvalidPolicyInfoException::new);

        policyInfo.updatePolicyInfo(policyUpdateReq);
    }

}