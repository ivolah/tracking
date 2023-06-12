package it.cleverad.tracking.web.dto;

import it.cleverad.tracking.persistence.model.Cpl;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    public CplDTO(long id, String refferal, String ip, String agent, String data, LocalDateTime date, Boolean read, String info, String country) {
        this.id = id;
        this.refferal = refferal;
        this.ip = ip;
        this.agent = agent;
        this.data = data;
        this.date = date;
        this.read = read;
        this.info = info;
        this.country = country;
    }

    public static CplDTO from(Cpl cpl) {
        return new CplDTO(cpl.getId(), cpl.getRefferal(), cpl.getIp(), cpl.getAgent(), cpl.getData(), cpl.getDate(), cpl.getRead(), cpl.getInfo(), cpl.getCountry());
    }

}
