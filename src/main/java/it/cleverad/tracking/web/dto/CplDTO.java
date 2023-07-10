package it.cleverad.tracking.web.dto;

import it.cleverad.tracking.persistence.model.Cpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CplDTO {

    private long id;

    private String refferal;
    private String ip;
    private String agent;
    private String data;
    private LocalDateTime date;
    private Boolean read;
    private String info;
    private String country;

    //dati refferal
    private Long mediaId;
    private Long campaignId;
    private Long affiliateId;
    private Long channelId;
    private Long targetId;

    private Boolean blacklisted;

    public static CplDTO from(Cpl cpl) {
        return new CplDTO(cpl.getId(), cpl.getRefferal(), cpl.getIp(), cpl.getAgent(), cpl.getData(), cpl.getDate(), cpl.getRead(), cpl.getInfo(), cpl.getCountry(),
                cpl.getMediaId(), cpl.getCampaignId(), cpl.getAffiliateId(), cpl.getChannelId(), cpl.getTargetId(), cpl.getBlacklisted());
    }

}
