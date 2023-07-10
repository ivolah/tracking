package it.cleverad.tracking.web.dto;

import it.cleverad.tracking.persistence.model.Cps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CpsDTO {

    private long id;

    private String refferal;
    private String ip;
    private String agent;
    private String data;

    private LocalDateTime date;
    private Boolean read;

    //dati refferal
    private Long mediaId;
    private Long campaignId;
    private Long affiliateId;
    private Long channelId;
    private Long targetId;
    private Boolean blacklisted;

    public static CpsDTO from(Cps cps) {
        return new CpsDTO(cps.getId(), cps.getRefferal(), cps.getIp(), cps.getAgent(), cps.getData(), cps.getDate(), cps.getRead(),
                cps.getMediaId(), cps.getCampaignId(), cps.getAffiliateId(), cps.getChannelId(), cps.getTargetId(), cps.getBlacklisted());
    }

}
