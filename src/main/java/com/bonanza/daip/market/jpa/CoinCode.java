package com.bonanza.daip.market.jpa;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "code")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "tb_coin_code"
)
@Entity
public class CoinCode {
    
    @Id
    @Comment("마켓 코드 (ex. KRW-BTC)")
    @Column(name = "code", length = 30)
    private String code;
    
    @Comment("한글명")
    @Column(name = "korean_name", length = 50)
    private String korean_name;
    
    @Comment("영문명")
    @Column(name = "english_name", length = 50)
    private String english_name;
    
    @Comment("업비트")
    @Column(name = "upbit", length = 30)
    private String upbit;
    
    @Comment("빗썸")
    @Column(name = "bithumb", length = 30)
    private String bithumb;
    
    @Comment("코인원")
    @Column(name = "coinone", length = 30)
    private String coinone;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Comment("로고")
    @Column(name = "logo", columnDefinition = "LONGTEXT")
    private String logo;
    
    @Comment("로고 확장자")
    @Column(name = "logo_exts", length = 5)
    private String logo_exts;
}
