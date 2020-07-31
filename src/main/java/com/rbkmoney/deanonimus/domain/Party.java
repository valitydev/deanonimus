package com.rbkmoney.deanonimus.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Document(indexName = "party", createIndex = true)
@Setting(settingPath = "/settings/autocomplete-analyzer.json")
public class Party {

    @Id
    private String id;
    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
    private String email;
    @Field(type = FieldType.Date, store = true, format = DateFormat.date_hour_minute_second_millis, index = false)
    private LocalDateTime createdAt;

    private Blocking blocking;
    private Suspension suspension;

    @Field(type = FieldType.Nested, store = true)
    private List<Contractor> contractors;
    @Field(type = FieldType.Nested, store = true)
    private List<Contract> contracts;
    @Field(type = FieldType.Nested, store = true)
    private List<Shop> shops;
    @Field(type = FieldType.Nested, store = true)
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

    public Optional<Contractor> getContractorById(String id) {
        return this.contractors.stream().filter(contractor -> contractor.getId().equals(id)).findFirst();
    }

    public Optional<Wallet> getWalletById(String id) {
        return this.wallets.stream().filter(wallet -> wallet.getId().equals(id)).findFirst();
    }

}
