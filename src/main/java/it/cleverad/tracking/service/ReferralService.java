package it.cleverad.tracking.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
public class ReferralService {

    public Referral decodificaRefferal(String refferalString) {
        String[] tokens = refferalString.split("-");
        Referral referral = new Referral();
        if (tokens[0] != null) {
            referral.setCampaignId(Long.valueOf(decodifica(tokens[0])));
        }
        if (tokens[1] != null) {
            referral.setMediaId(Long.valueOf(decodifica(tokens[1])));
        }
        if (tokens[2] != null) {
            referral.setAffiliateId(Long.valueOf(decodifica(tokens[2])));
        }
        if (tokens[3] != null) {
            referral.setChannelId(Long.valueOf(decodifica(tokens[3])));
        }
        if (tokens[4] != null) {
            referral.setTargetId(Long.valueOf(decodifica(tokens[4])));
        }
        return referral;
    }

    public String decodifica(String refferalString) {
        byte[] decoder = Base64.getDecoder().decode(refferalString);
        return new String(decoder);
    }

    public String creaEncoding(String campaignId, String mediaID, String affilaiteID, String channelID, String targetId) {

        campaignId = encode(campaignId);
        mediaID = encode(mediaID);
        affilaiteID = encode(affilaiteID);
        channelID = encode(channelID);
        targetId = encode(targetId);

        return StringUtils.stripEnd(campaignId, "=") + "-" + StringUtils.stripEnd(mediaID, "=") + "-" + StringUtils.stripEnd(affilaiteID, "=") + "-" + StringUtils.stripEnd(channelID, "=") + "-" + StringUtils.stripEnd(targetId, "=");
    }

    public String encode(String str) {
        byte[] encodedRefferal = Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8));
        String reString = new String(encodedRefferal);
        if (reString.endsWith("=")) {
            reString = reString.substring(0, reString.length() - 1);
        }
        if (reString.endsWith("=")) {
            reString = reString.substring(0, reString.length() - 1);
        }
        return reString;
    }

}
