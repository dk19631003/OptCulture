package com.optculture.app.dto.ereceipt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.optculture.app.repositories.LoyaltyTransactionChildRepository;
import org.modelmapper.ModelMapper;

import com.optculture.shared.entities.contact.ContactLoyalty;
import com.optculture.shared.entities.loyalty.LoyaltyProgramTier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loyalty {
	private String cardNumber;
	private Double loyaltyPointBalance;
	private Double loyaltyCurrencyBalance;
	private Double cummulativePurchaseValue;
	private Double totalLoyaltyPointsEarned;
	private Double totalGiftcardAmount;
	private String tierLevel;
	private Double previousTierUpgradedValue;
	private String currentTierName;
	private Double currentTierValue;
	private String nextTierName;
	private String tierUpgradeCriteria;
	private Double tierUpgradeValue;
	private Double tierUpgradeMileStone;
	private Double totalLoyaltyRedemption;
	private Double holdpointsBalance;
	private Double holdamountBalance;
	private Map<String,Long> listOfTiers;

	@Autowired
	LoyaltyTransactionChildRepository loyaltyTransactionChildRepository;

	public static Loyalty of(List<LoyaltyProgramTier> loyaltyProgramTierList, ContactLoyalty contactLoyalty) {
		ModelMapper modelMapper = new ModelMapper();
		Map<String,Long> tierList = new HashMap<>();
		Loyalty loyalty = modelMapper.map(contactLoyalty, Loyalty.class);
		for (LoyaltyProgramTier loyaltyProgramTier:loyaltyProgramTierList) {
			if(!Objects.equals(loyaltyProgramTier.getTierId(), contactLoyalty.getProgramTierId()))
				tierList.put(loyaltyProgramTier.getTierName(),loyaltyProgramTier.getTierId());
		}
		loyalty.setListOfTiers(tierList);

		Double previousTierUpgradedValue = 0.0;
		boolean foundCurrentTier = false;
		for (LoyaltyProgramTier loyaltyProgramTier : loyaltyProgramTierList) {

			loyalty.setTierUpgradeCriteria(loyaltyProgramTier.getTierUpgdConstraint());
			if (foundCurrentTier) {
				loyalty.setNextTierName(loyaltyProgramTier.getTierName());
				break;
			}

			if (!loyaltyProgramTier.getTierId().equals(contactLoyalty.getProgramTierId())) {
				previousTierUpgradedValue = loyaltyProgramTier.getTierUpgdConstraintValue();
			}
			if (loyaltyProgramTier.getTierId().equals(contactLoyalty.getProgramTierId())) {

				//current tier Name
				loyalty.setCurrentTierName(loyaltyProgramTier.getTierName());
				loyalty.setTierUpgradeValue(loyaltyProgramTier.getTierUpgdConstraintValue());

				// upgrade mile stone,current value
				if (Optional.ofNullable(loyaltyProgramTier.getTierUpgdConstraintValue()).isPresent()) {
					if (loyaltyProgramTier.getTierUpgdConstraint().equalsIgnoreCase("LifetimePoints")) {
						loyalty.setTierUpgradeMileStone(loyaltyProgramTier.getTierUpgdConstraintValue() - (contactLoyalty.getTotalLoyaltyPointsEarned() == null ? 0 : contactLoyalty.getTotalLoyaltyPointsEarned()));
						loyalty.setCurrentTierValue(contactLoyalty.getTotalLoyaltyPointsEarned());
					} else if (loyaltyProgramTier.getTierUpgdConstraint().equalsIgnoreCase("LifetimePurchaseValue")) {
						Double LPV = getLPV(contactLoyalty);
						loyalty.setTierUpgradeMileStone(loyaltyProgramTier.getTierUpgdConstraintValue() - LPV);
						loyalty.setCurrentTierValue(LPV);
					} else if (loyaltyProgramTier.getTierUpgdConstraint().equalsIgnoreCase("CumulativePurchaseValue")) {
						// Long loyaltyId =
						// contactLoyalty.getTransferedTo()==null?contactLoyalty.getLoyaltyId():contactLoyalty.getTransferedTo();
						Long months = loyaltyProgramTier.getTierUpgradeCumulativeValue();
						LocalDateTime createdDate = LocalDateTime.now().minusMonths(months);
						Loyalty loyalty1 = new Loyalty();
						Double purchaseValue = loyalty1.getCummulativeAmount(contactLoyalty, createdDate);
						loyalty.setTierUpgradeMileStone(
								loyaltyProgramTier.getTierUpgdConstraintValue() - purchaseValue);
						loyalty.setCurrentTierValue(purchaseValue);
						// Double purchaseValue =
						// loyaltyTransactionChildRepository.findByUserIdAndloyaltyIdAndcreatedDate(contactLoyalty.getUserId(),loyaltyId,createdDate);
					}
				}
				foundCurrentTier = true;
			}
		}
		loyalty.setPreviousTierUpgradedValue(previousTierUpgradedValue);
		

		return loyalty;
	}
	public static Double getLPV(ContactLoyalty contactsLoyalty) {
		Double totPurchaseValue = null;
		Double cummulativePurchaseValue = contactsLoyalty.getCummulativePurchaseValue() == null ? 0.0 : contactsLoyalty.getCummulativePurchaseValue();
		Double cummulativeReturnValue = contactsLoyalty.getCummulativeReturnValue() == null ? 0.0 : contactsLoyalty.getCummulativeReturnValue();
		totPurchaseValue = (new BigDecimal(cummulativePurchaseValue-cummulativeReturnValue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		return totPurchaseValue;
	}

	public Double getCummulativeAmount(ContactLoyalty contactLoyalty, LocalDateTime createdDate) {
		Long loyaltyId = contactLoyalty.getTransferedTo()==null?contactLoyalty.getLoyaltyId():contactLoyalty.getTransferedTo();
		return loyaltyTransactionChildRepository.findByUserIdAndloyaltyIdAndcreatedDate(contactLoyalty.getUserId(),loyaltyId,createdDate);
	}
}
