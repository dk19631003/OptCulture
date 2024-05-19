package org.mq.captiway.scheduler.beans;


	public class CouponDiscountGeneration {
		
		private Long couponDisGenId;
		private Coupons coupons;
		private String typeOfDiscount;
		private Double discount;
		private String itemCategory;
		private Long totPurchaseAmount;
		
		
		public CouponDiscountGeneration(){}
		
		public CouponDiscountGeneration(Coupons coupons){
			this.coupons=coupons;
		}
		
		public Long getCouponDisGenId() {
			return couponDisGenId;
		}
		public void setCouponDisGenId(Long couponDisGenId) {
			this.couponDisGenId = couponDisGenId;
		}
		public Coupons getCoupons() {
			return coupons;
		}
		public void setCoupons(Coupons coupons) {
			this.coupons = coupons;
		}
		public String getTypeOfDiscount() {
			return typeOfDiscount;
		}
		public void setTypeOfDiscount(String typeOfDiscount) {
			this.typeOfDiscount = typeOfDiscount;
		}
		public Double getDiscount() {
			return discount;
		}
		public void setDiscount(Double discount) {
			this.discount = discount;
		}
		public String getItemCategory() {
			return itemCategory;
		}
		public void setItemCategory(String itemCategory) {
			this.itemCategory = itemCategory;
		}
		public Long getTotPurchaseAmount() {
			return totPurchaseAmount;
		}
		public void setTotPurchaseAmount(Long totPurchaseAmount) {
			this.totPurchaseAmount = totPurchaseAmount;
		}
		

}
