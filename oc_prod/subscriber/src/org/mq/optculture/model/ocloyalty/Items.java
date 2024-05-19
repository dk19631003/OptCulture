package org.mq.optculture.model.ocloyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;


public class Items {
private List<SkuDetails> OCLoyaltyItem;

public List<SkuDetails> getOCLoyaltyItem() {
 return OCLoyaltyItem;
}
@XmlElement(name = "OCLoyaltyItem")
public void setOCLoyaltyItem(List<SkuDetails> OCLoyaltyItem) {
 this.OCLoyaltyItem = OCLoyaltyItem;
}}
