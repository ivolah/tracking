package it.cleverad.tracking.web.dto;

import it.cleverad.tracking.persistence.model.Cpc;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CpcDTO {

    private long id;
    private String refferal;
    private String ip;
    private String agent;
    private LocalDateTime date;
    private Boolean read;
    private String htmlReferral;
    private String info;

    public CpcDTO(long id, String refferal, String ip, String agent, LocalDateTime date, Boolean read, String htmlRefferral, String info) {
        this.id = id;
        this.refferal = refferal;
        this.ip = ip;
        this.agent = agent;
        this.date = date;
        this.read = read;
        this.htmlReferral = htmlRefferral;
        this.info = info;
    }

    public static CpcDTO from(Cpc cpc) {
        return new CpcDTO(cpc.getId(), cpc.getRefferal(), cpc.getIp(), cpc.getAgent(), cpc.getDate(), cpc.getRead(), cpc.getHtmlReferral(), cpc.getInfo());
    }

}
