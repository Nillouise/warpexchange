package com.itranswarp.exchange.model.asset;

import com.itranswarp.exchange.enums.AssetEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

import static com.itranswarp.exchange.enums.AssetEnum.SCALE;
import static com.itranswarp.exchange.model.support.EntitySupport.PRECISION;

@Entity
@Table(name = "users_asset")
public class UserAssetDBEntity {

    /**
     * Primary key: assigned.
     */
    @Id
    @Column()
    public Long id;

    @Column(nullable = false, updatable = false)
    public Long userId;

    @Column(nullable = false, updatable = false)
    public Long era;

    @Column(nullable = false, updatable = false)
    public AssetEnum asset;

    /**
     * The match price for this clearing.
     */
    @Column(nullable = false, updatable = false, precision = PRECISION, scale = SCALE)
    public BigDecimal available;

    /**
     * The match price for this clearing.
     */
    @Column(nullable = false, updatable = false, precision = PRECISION, scale = SCALE)
    public BigDecimal frozen;

    @Override
    public String toString() {
        return "UserAssetDBEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", era=" + era +
                ", asset=" + asset +
                ", available=" + available +
                ", frozen=" + frozen +
                '}';
    }
}
