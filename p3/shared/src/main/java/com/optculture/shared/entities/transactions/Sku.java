package com.optculture.shared.entities.transactions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "retail_pro_sku")
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_id")
    private java.lang.Long skuId;

    @Column(name = "list_id")
    private Long listId;

    @Column(name = "store_number")
    private String storeNumber;

    @Column(name = "subsidiary_number")
    private String subsidiaryNumber;

    @Column(name = "sku")
    private String skuCode;

    @Column(name = "description")
    private String description;

    @Column(name = "list_price")
    private Double listPrice;

    @Column(name = "item_category")
    private String itemCategory;

    @Column(name = "udf1")
    private String udf1;

    @Column(name = "udf2")
    private String udf2;

    @Column(name = "udf3")
    private String udf3;

    @Column(name = "udf4")
    private String udf4;

    @Column(name = "udf5")
    private String udf5;

    @Column(name = "udf6")
    private String udf6;

    @Column(name = "udf7")
    private String udf7;

    @Column(name = "udf8")
    private String udf8;

    @Column(name = "udf9")
    private String udf9;

    @Column(name = "udf10")
    private String udf10;

    @Column(name = "udf11")
    private String udf11;

    @Column(name = "udf12")
    private String udf12;

    @Column(name = "udf13")
    private String udf13;

    @Column(name = "udf14")
    private String udf14;

    @Column(name = "udf15")
    private String udf15;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "item_sid")
    private String itemSid;

    @Column(name = "vendor_code")
    private String vendorCode;

    @Column(name = "department_code")
    private String departmentCode;

    @Column(name = "class_code")
    private String classCode;

    @Column(name = "subclass_code")
    private String subClassCode;

    @Column(name = "dcs")
    private String dcs;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

}
