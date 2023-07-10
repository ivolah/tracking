package it.cleverad.tracking.scheduled;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import it.cleverad.tracking.business.CpcBusiness;
import it.cleverad.tracking.business.CplBusiness;
import it.cleverad.tracking.business.CpmBusiness;
import it.cleverad.tracking.business.CpsBusiness;
import it.cleverad.tracking.persistence.repository.BlacklistRepository;
import it.cleverad.tracking.web.dto.CpcDTO;
import it.cleverad.tracking.web.dto.CplDTO;
import it.cleverad.tracking.web.dto.CpmDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
public class ScheduledActivities {

    @Autowired
    CpcBusiness cpcBusiness;
    @Autowired
    CpmBusiness cpmBusiness;
    @Autowired
    CplBusiness cplBusiness;
    @Autowired
    CpsBusiness cpsBusiness;

    @Autowired
    BlacklistRepository blacklistRepository;

    // @Scheduled(cron = "0 */5 * * * ?")
    public void gestisciIp2GeoWeb() {

        //  WebServiceClient client = new WebServiceClient.Builder(10, "LICENSEKEY").build();
        try (WebServiceClient client = new WebServiceClient.Builder(864590, "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").host("geolite.info").build()) {

            List<CpcDTO> listaCpc = cpcBusiness.getToTest5MinutesBefore().stream().collect(Collectors.toList());
            List<CpmDTO> listaCpm = cpmBusiness.getToTest2HourBefore().stream().collect(Collectors.toList());
            List<CplDTO> listaCpl = cplBusiness.getToTest5MinutesBefore().stream().collect(Collectors.toList());
            //  List<CpsDTO> listaCps = cpsBusiness.getToTest2HourBefore().stream().collect(Collectors.toList());

            // GESTISCO CPM
            listaCpm.forEach(cpcDTO -> {
                try {
                    InetAddress ipAddress = InetAddress.getByName(cpcDTO.getIp());
                    CountryResponse response = client.country(ipAddress);
                    Country country = response.getCountry();
                    log.info("Country {} >>> {} :: {} :: {} :: {}", country.getConfidence(), country.getIsoCode(), country.getName(), country.isInEuropeanUnion());
                    if (!country.getIsoCode().equals("IT")) {
                        log.info("cancello record");
                        //  cpmBusiness.delete(cpcDTO.getId());
                    }
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (GeoIp2Exception e) {
                    throw new RuntimeException(e);
                }
            });

            // GESTISCO CPC
            listaCpc.forEach(cpcDTO -> {
                try {
                    InetAddress ipAddress = InetAddress.getByName(cpcDTO.getIp());
                    CountryResponse response = client.country(ipAddress);
                    Country country = response.getCountry();
                    log.info("Country {} >>> {} :: {} :: {} :: {}", country.getConfidence(), country.getIsoCode(), country.getName(), country.isInEuropeanUnion());
                    if (!country.getIsoCode().equals("IT")) {
                        log.info("cancello record");
                        //  cpcBusiness.delete(cpcDTO.getId());
                    }
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (GeoIp2Exception e) {
                    throw new RuntimeException(e);
                }
            });

            // GESTISCO CPL
            listaCpl.forEach(cplDTO -> {
                try {
                    InetAddress ipAddress = InetAddress.getByName(cplDTO.getIp());
                    CountryResponse response = client.country(ipAddress);
                    Country country = response.getCountry();
                    log.info("Country {} >>> {} :: {} :: {} :: {}", country.getConfidence(), country.getIsoCode(), country.getName(), country.isInEuropeanUnion());
                    if (!country.getIsoCode().equals("IT")) {
                        log.info("cancello record");
                        //  cplBusiness.delete(cpcDTO.getId());
                    }
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (GeoIp2Exception e) {
                    throw new RuntimeException(e);
                }
            });

        }// try
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Async
    @Scheduled(cron = "2 */2 * * * ?")
    //@Scheduled(cron = "59 59 */1 * * ?")
    public void gestisciIp2GeoDBeBlacklist() {

        try {
            File database = new File("/opt/ip2geo/GeoLite2-Country.mmdb");
            DatabaseReader reader = new DatabaseReader.Builder(database).build();

            // GESTISCO CPC
            List<CpcDTO> listaCpc = cpcBusiness.getToTest5MinutesBefore().stream().collect(Collectors.toList());
            listaCpc.forEach(dto -> {
                try {
                    // geoIP
                    InetAddress ipAddress = InetAddress.getByName(dto.getIp());
                    cpcBusiness.updateCountry(dto.getId(), reader.country(ipAddress).getCountry().getIsoCode());

                } catch (IOException | GeoIp2Exception e) {
                    log.error("ECCEZIONE ", e);
                }
            });

            // GESTISCO CPL
            List<CplDTO> listaCpl = cplBusiness.getToTest5MinutesBefore().stream().collect(Collectors.toList());
            listaCpl.forEach(dto -> {
                try {
                    InetAddress ipAddress = InetAddress.getByName(dto.getIp());
                    cplBusiness.updateCountry(dto.getId(), reader.country(ipAddress).getCountry().getIsoCode());
                } catch (IOException | GeoIp2Exception e) {
                    log.error("ECCEZIONE ", e);
                }
            });

            // List<CpmDTO> listaCpm = cpmBusiness.getToTest2HourBefore().stream().collect(Collectors.toList());
            //  List<CpsDTO> listaCps = cpsBusiness.getToTest2HourBefore().stream().collect(Collectors.toList());
            // GESTISCO CPM
/*
            listaCpm.forEach(dto -> {
                try {
                    InetAddress ipAddress = InetAddress.getByName(dto.getIp());
                    CountryResponse response = reader.country(ipAddress);
                    Country country = response.getCountry();
                    if (country.getIsoCode() != null && !country.getIsoCode().equals("IT") && !country.getIsoCode().equals("CH") && !country.getIsoCode().equals("CH")) {
                        log.info("Country {} >>> {} :: {}", country.getIsoCode(), country.getName(), country.isInEuropeanUnion());
                        log.info("CPM -- cancello record {} ", dto.getId());
                        cpmBusiness.delete(dto.getId());
                    }
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (GeoIp2Exception e) {
                    throw new RuntimeException(e);
                }
            });
*/

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
