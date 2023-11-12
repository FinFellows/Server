package com.finfellows.domain.educontent.domain;

import com.finfellows.domain.common.BaseEntity;
import com.finfellows.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "EduContent")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Where(clause = "status = 'ACTIVE'")
public class EduContent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name="url", nullable = false, unique = true)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="writer_id")
    private User writer;

    @Builder
    public EduContent(String url, User writer){
        this.url=url;
        this.writer=writer;
    }
}
