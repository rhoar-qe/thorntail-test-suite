package org.wildfly.swarm.ts.javaee8.jpa;

import javax.inject.Inject;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class MyAttributeConverter implements AttributeConverter<MyAttribute, String> {
    @Inject
    private MyUtils utils;

    @Override
    public String convertToDatabaseColumn(MyAttribute attribute) {
        return utils.myAttributeToString(attribute);
    }

    @Override
    public MyAttribute convertToEntityAttribute(String dbData) {
        return utils.stringToMyAttribute(dbData);
    }
}

