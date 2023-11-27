package dev.vality.deanonimus.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Party {

    private String id;
    private String email;

    private Blocking blocking;
    private Suspension suspension;

    private List<Contractor> contractors;
    private List<Contract> contracts;
    private List<Shop> shops;
    private List<Wallet> wallets;

    public void addShop(Shop shop) {
        if (this.shops == null) {
            this.shops = new ArrayList<>();
        }
        this.shops.add(shop);
    }

    public void addContract(Contract contract) {
        if (this.contracts == null) {
            this.contracts = new ArrayList<>();
        }
        this.contracts.add(contract);
    }

    public void addContractor(Contractor contractor) {
        if (this.contractors == null) {
            this.contractors = new ArrayList<>();
        }
        this.contractors.add(contractor);
    }

    public void addWallet(Wallet wallet) {
        if (this.wallets == null) {
            this.wallets = new ArrayList<>();
        }
        this.wallets.add(wallet);
    }

    public Optional<Shop> getShopById(String id) {
        return this.shops.stream().filter(shop -> shop.getId().equals(id)).findFirst();
    }

    public Optional<Contract> getContractById(String id) {
        return this.contracts.stream().filter(contract -> contract.getId().equals(id)).findFirst();
    }
}
