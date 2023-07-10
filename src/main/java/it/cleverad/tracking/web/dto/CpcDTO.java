package it.cleverad.tracking.web.dto;

import it.cleverad.tracking.persistence.model.Cpc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CpcDTO {

    private long id;
    private String refferal;
    private String ip;
    private String agent;
    private LocalDateTime date;
    private Boolean read;
    private String htmlReferral;
    private String info;
    private String country;

    //dati refferal
    private Long mediaId;
    private Long campaignId;
    private Long affiliateId;
    private Long channelId;
    private Long targetId;

    private Boolean blacklisted;

    public static CpcDTO from(Cpc cpc) {
        return new CpcDTO(cpc.getId(),
                cpc.getRefferal(),
                cpc.getIp(),
                cpc.getAgent(),
                cpc.getDate(),
                cpc.getRead(),
                cpc.getHtmlReferral(),
                cpc.getInfo(),
                cpc.getCountry(),
                cpc.getMediaId(), cpc.getCampaignId(), cpc.getAffiliateId(), cpc.getChannelId(), cpc.getTargetId(), cpc.getBlacklisted());
    }

}
