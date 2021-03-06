package org.datacite.mds.validation.constraints.impl;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.util.Utils;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.MatchDomain;

public abstract class MatchDomainValidator {
    String defaultMessage;
    String wildCard;
    String alwaysAllowedDomains;

    public void initialize(MatchDomain constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
        wildCard = constraintAnnotation.wildCard();
    }

    boolean isValid(String url, Datacentre datacentre, ConstraintValidatorContext context) {
        boolean isValidationUnneeded = datacentre == null || StringUtils.isEmpty(url); 
        if (isValidationUnneeded)
            return true;

        String hostname = Utils.getHostname(url).toLowerCase();
        List<String> allowedDomains = new ArrayList<String>(); 
        allowedDomains.addAll(Utils.csvToList(datacentre.getDomains()));
        allowedDomains.addAll(Utils.csvToList(alwaysAllowedDomains));
        
        for (String domain : allowedDomains) {
            domain = domain.toLowerCase();
            boolean matchWildCard = Utils.wildCardMatch(hostname, domain, wildCard);
            boolean matchSubDomain = Utils.wildCardMatch(hostname, "*." + domain, wildCard); 
            if (matchWildCard || matchSubDomain) {
                return true;
            }
        }

        ValidationUtils.addConstraintViolation(context, defaultMessage, "url");
        return false;
    }

    public String getAlwaysAllowedDomains() {
        return alwaysAllowedDomains;
    }

    public void setAlwaysAllowedDomains(String alwaysAllowedDomains) {
        this.alwaysAllowedDomains = alwaysAllowedDomains;
    }
}
