package it.cleverad.tracking.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_cpc")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Cpc {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refferal;
    private String ip;
    private String agent;
    private LocalDateTime date = LocalDateTime.now();
    private Boolean read = false;
    @Column(name = "html_referral")
    private String htmlReferral;
    private String info;
    private String country;

    //dati refferal
    @Column(name = "media_id")
    private Long mediaId;
    @Column(name = "campaign_id")
    private Long campaignId;
    @Column(name = "affiliate_id")
    private Long affiliateId;
    @Column(name = "channel_id")
    private Long channelId;
    @Column(name = "target_id")
    private Long targetId;

    private Boolean blacklisted = false;

}
