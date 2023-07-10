package it.cleverad.tracking.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_cpm")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Cpm {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long campaignId;
    private Long imageId;
    private Long mediaId;

    private String refferal;
    private String ip;
    private String agent;

    @CreatedDate
    private LocalDateTime date = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean read = false;

    //dati refferal
    @Column(name = "affiliate_id")
    private Long affiliateId;
    @Column(name = "channel_id")
    private Long channelId;
    @Column(name = "target_id")
    private Long targetId;

    private Boolean blacklisted = false;
}
