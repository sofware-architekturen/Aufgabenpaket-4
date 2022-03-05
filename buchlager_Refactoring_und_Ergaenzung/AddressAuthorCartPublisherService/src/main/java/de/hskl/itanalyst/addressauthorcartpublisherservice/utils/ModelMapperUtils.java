package de.hskl.itanalyst.addressauthorcartpublisherservice.utils;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ModelMapperUtils {
    @Autowired
    private ModelMapper modelMapper;

    private <D, T> D mapHelper(final T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    public <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outCLass) {
        return entityList.stream()
                .map(entity -> mapHelper(entity, outCLass))
                .collect(Collectors.toList());
    }
}
