package de.hskl.itanalyst.addressauthorcartpublisherservice.domain.converter;


import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Book.BookLightDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Cart.CartDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Cart.CartItemDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.CartEntity;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.CartItemEntity;
import de.hskl.itanalyst.addressauthorcartpublisherservice.repository.AuthorRepository;
import de.hskl.itanalyst.addressauthorcartpublisherservice.repository.BookRepository;
import de.hskl.itanalyst.addressauthorcartpublisherservice.utils.ModelMapperUtils;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;

@Component
public class CartConverters {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ModelMapperUtils modelMapperUtils;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @PostConstruct
    public void postConstruct() {
        modelMapper.addConverter(cartEntityCartDTOConverter);
        modelMapper.addConverter(cartItemEntityCartItemDTOConverter);
    }

    private final Converter<CartEntity, CartDTO> cartEntityCartDTOConverter = new AbstractConverter<CartEntity, CartDTO>() {
        @Override
        protected CartDTO convert(CartEntity cartEntity) {
            final CartDTO cartDTO = new CartDTO();
            cartDTO.validUntil = cartEntity.getValidUntil();
            cartDTO.items = new HashSet<>(modelMapperUtils.mapAll(cartEntity.getItems(), CartItemDTO.class));
            return cartDTO;
        }
    };

    private final Converter<CartItemEntity, CartItemDTO> cartItemEntityCartItemDTOConverter = new AbstractConverter<CartItemEntity, CartItemDTO>() {
        @Override
        protected CartItemDTO convert(CartItemEntity cartItemEntity) {
            final CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.amount = cartItemEntity.getAmount();
            cartItemDTO.book = modelMapper.map(cartItemEntity.getItem(), BookLightDTO.class);
            return cartItemDTO;
        }
    };
}
