package it.cleverad.tracking.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Refferal {
    private Long mediaId;
    private Long campaignId;
    private Long affiliateId;
    private Long channelId;
    private Long targetId;
}
