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
    private String country;

    public CpcDTO(long id, String refferal, String ip, String agent, LocalDateTime date, Boolean read, String htmlReferral, String info, String country) {
        this.id = id;
        this.refferal = refferal;
        this.ip = ip;
        this.agent = agent;
        this.date = date;
        this.read = read;
        this.htmlReferral = htmlReferral;
        this.info = info;
        this.country = country;
    }

    public static CpcDTO from(Cpc cpc) {
        return new CpcDTO(cpc.getId(), cpc.getRefferal(), cpc.getIp(), cpc.getAgent(), cpc.getDate(), cpc.getRead(), cpc.getHtmlReferral(), cpc.getInfo(), cpc.getCountry());
    }

}
