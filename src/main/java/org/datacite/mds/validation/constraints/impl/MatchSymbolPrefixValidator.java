package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.ValidationHelper;
import org.datacite.mds.validation.constraints.MatchSymbolPrefix;
import org.springframework.beans.factory.annotation.Autowired;

public class MatchSymbolPrefixValidator implements ConstraintValidator<MatchSymbolPrefix, Datacentre> {
    String defaultMessage;

    @Autowired
    ValidationHelper validationHelper;

    public void initialize(MatchSymbolPrefix constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
    }

    public boolean isValid(Datacentre datacentre, ConstraintValidatorContext context) {
        if (datacentre.getAllocator() == null || !validationHelper.isValid(datacentre, "symbol")) {
            // don't check until a allocator is selected and a valid symbol is given
            return true;
        }
        
        if (!datacentre.getSymbol().startsWith(datacentre.getAllocator().getSymbol()+".")) {
            context.disableDefaultConstraintViolation();
            ValidationUtils.addConstraintViolation(context, defaultMessage, "symbol");
            return false;
        }
        
        return true;
    }
}
