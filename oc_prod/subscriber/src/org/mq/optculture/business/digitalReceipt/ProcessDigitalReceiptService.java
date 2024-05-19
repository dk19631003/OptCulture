package org.mq.optculture.business.digitalReceipt;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.DR.DigitalReceipt;
import org.mq.optculture.model.DR.DigitalReceiptResponse;
import org.mq.optculture.model.DR.heartland.HeartlandDRRequest;
import org.mq.optculture.model.DR.magento.MagentoBasedDRRequest;
import org.mq.optculture.model.DR.orion.OrionDRRequest;
import org.mq.optculture.model.DR.prism.PrismBasedDRRequest;
import org.mq.optculture.model.DR.shopify.ShopifyBasedDRRequest;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRRequest;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceReturnDRRequest;

public interface ProcessDigitalReceiptService extends BaseService{
	
	public DigitalReceiptResponse processDigitalReceipt(DigitalReceipt digitalReceipt, String mode) throws BaseServiceException ;
	public DigitalReceiptResponse processPrismDRRequest(PrismBasedDRRequest sendDRRequest, String mode) throws BaseServiceException;
	public DigitalReceiptResponse processMagentoDRRequest(MagentoBasedDRRequest sendDRRequest, String mode) throws BaseServiceException;
	public DigitalReceiptResponse processWooCommerceDRRequest(WooCommerceDRRequest sendDRRequest, String mode) throws BaseServiceException;
	public DigitalReceiptResponse processWooCommerceRefundDRRequest(WooCommerceReturnDRRequest returnDRRequest, String mode) throws BaseServiceException;
	public DigitalReceiptResponse processShopifytoDRRequest(ShopifyBasedDRRequest sendDRRequest, String mode) throws BaseServiceException;
	public DigitalReceiptResponse processHeartlandDRRequest(HeartlandDRRequest sendDRRequest, String mode) throws BaseServiceException;
	public DigitalReceiptResponse processOrionDRRequest(OrionDRRequest sendDRRequest, String mode) throws BaseServiceException;

}
