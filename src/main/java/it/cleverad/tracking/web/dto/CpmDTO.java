package it.cleverad.tracking.web.dto;

import it.cleverad.tracking.persistence.model.Cpm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CpmDTO {

    private long id;


    private Long imageId;
    private Long mediaId;

    private String refferal;
    private String ip;
    private String agent;

    private LocalDateTime date;
    private Boolean read;

    private Long campaignId;
    private Long affiliateId;
    private Long channelId;
    private Long targetId;

    private Boolean blacklisted;

    public static CpmDTO from(Cpm cpm) {
        return new CpmDTO(cpm.getId(), cpm.getImageId(), cpm.getMediaId(), cpm.getRefferal(), cpm.getIp(), cpm.getAgent(), cpm.getDate(), cpm.getRead()
               , cpm.getCampaignId(), cpm.getAffiliateId(), cpm.getChannelId(), cpm.getTargetId(), cpm.getBlacklisted());
    }

}
