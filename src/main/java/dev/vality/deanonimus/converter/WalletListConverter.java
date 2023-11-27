package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WalletListConverter {

    public Map<String, Wallet> convert(List<dev.vality.deanonimus.domain.Wallet> wallets) {
        return Optional.ofNullable(wallets).orElse(Collections.emptyList())
                .stream()
                .map(this::convertToEntity)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));
    }

    private Map.Entry<String, Wallet> convertToEntity(dev.vality.deanonimus.domain.Wallet walletDomain) {
        return Map.entry(walletDomain.getId(), new Wallet(
                walletDomain.getId(),
                walletDomain.getName()
        ));
    }
}
