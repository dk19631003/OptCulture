import mock from '@/@fake-db/mock';
import type { EreceiptType } from '@/@fake-db/types';

const receipts: EreceiptType[] = [
  {
    "loyalty": {
      "cardNumber": "98765437856",
      "loyaltyPointBalance": 0,
      "loyaltyCurrencyBalance": 0.0,
      "cummulativePurchaseValue": 599,
      "totalLoyaltyPointsEarned": 30,
      "tierLevel": "1",
      "currentTierName": "Silver",
      "currentTierValue": 1,
      "nextTierName": "GOLD",
      "tierUpgradeCriteria": 300
    },
    "customer": {
      "firstName": "Gopi",
      "lastName": "P",
      "mobilePhone": "",
      "emailId": "",
      "addressOne": "SR Nagar",
      "addressTwo": "Ramaram",
      "city": "Hyderabad",
      "state": "Telangana",
      "country": "India",
      "pinCode": "500019",
      "birthDay": "1998-12-12",
      "anniversary": "1998-12-12",
      "gender": "Male",
      "optin": "1"
    },
    "branding": {
      "companyLogo": "https://clues.site/styleunion/img/logo.png",
      "fontName": "Arial",
      "fontURL": "https://fonts.googleapis.com/css2?family=Raleway&display=",
      "coverImage": "https://i.ocmails.com/subscriber/UserData/OomomoLab__org__OomomoLab/RewardApp/Banner%20Image.png",
      "logoImage": "https://i.ocmails.com/subscriber/UserData/OomomoLab__org__OomomoLab/RewardApp/Banner%20Image.png",
      "homePageColorCode": "#90171C",
      "balanceCardThemeColor": "#90981C",
      "balanceCardTextColor": "#100000",
      "bannerName": "STORE",
      "currencySymbol": "$"
    },
    "receipt": {
      "salesDate": "2022-08-16 02:36:40",
      "receiptNumber": "67879878",
      "totalDiscount": 124.0,
      "subTotal": 3990.82,
      "total": 4398.0,
      "storeNumber": "76",
      "invcTotalQty": 4,
      "docSid": "101766-17-789788677",
      "totalTax": 120,
      "downloadReceiptLink":""
    },
    "lineItem": [
      {
          "skuInventory": {
              "skuId": 12083779,
              "listId": 1,
              "storeNumber": "14",
              "subsidiaryNumber": "",
              "skuCode": "GSL10000",
              "description": "MEN'S-MEN'S BOTTOM WEAR-TROUSER",
              "listPrice": 1999.0,
              "itemCategory": "MEN'S",
              "udf1": "ALLEN SOLLY",
              "udf2": "00014444",
              "udf3": "30",
              "udf4": "",
              "udf5": "",
              "udf6": "LTO00163PINK",
              "udf7": "2",
              "udf8": "",
              "udf9": "",
              "udf10": "",
              "udf11": "",
              "udf12": "",
              "udf13": "",
              "udf14": "Nagarmal",
              "udf15": "",
              "userId": 1072,
              "itemSid": "GSL10000",
              "vendorCode": "",
              "departmentCode": "TROUSER",
              "classCode": "MEN'S BOTTOM WEAR",
              "subClassCode": "",
              "dcs": "ALLEN SOLLY 00014444 30   "
          },
          "quantity": 1.0,
          "discount": 0.0,
          "tax": 0.0,
          "netAmount": 1999.0,
          "itemPromoDisc": ""
      }
  ],
    "tax": {
      "tax": "344",
      "totalTax": "232",
      "hsnCode": "9877",
      "taxDescription": "GST 17%",
      "iGstRate": "24.0",
      "iGstAmt": "110.0",
      "cGstRate": "120.0",
      "cGstAmt": "130.61",
      "sGstRate": "140.0",
      "sGstAmt": "493.61",
      "cessRate": "150.0",
      "cessAmt": "170.0"
    },
    "termsAndConditions": {
      "content": "Exchange accepted within 7 days from the date of purchase along with cash memo & Price Tag Only and Timings. | Exchange Timings 11 AM to 4 PM (Except Saturday & Sunday) | No Colour Guarantee on Cotton Wash Items. | We reserve the right to determine whether the goods have been damaged or used. | Exchange will be entertained only once each item sold. | Mobile no is mandatory at the time of exchange. | Credit note will be redeemed only by the approval OTP Received by your mobile. | Cash will not be refunded against goods Exchanged or returned. | Exchange will not be possible on Merchandise Sold during Clearance sales/any promotion/scheme & Altered Garments. | Goods Left for Alteration /Undelivered must be collected within 15days. | Terms of sale: Subject to Hyderabad Jurisdiction."    
    },
    "tender": {
      "name": "EDC",
      "type": "CASH",
      "amount": 3999.00
    },
    "organization": {
      "companyName": "OPT-CULTURE",
      "phoneNo": "7777777777",
      "emailId": "",
      "addressOne": "Road 10 SR nagar",
      "addressTwo":"",
      "city": "Hyderabad",
      "state": "Telangana",
      "country": "India",
      "pinCode": "500032",
      "webPortal": "https://loyalty.optculture.cloud/",
      "youtube": "https://www.youtube.com/",
      "facebook": "https://www.facebook.com/",
      "twitter": "https://twitter.com/",
      "instagram": "https://www.instagram.com/",
      "linkedin": "https://in.linkedin.com/"
    },
    "storeDetails": {
      "homeStoreId": "76",
      "storeName": "40, Commercial Street,",
      "locality": "Road no 10 Punjagutta, Hyderabad, Telangana 500035",
      "city":"",
      "state":"",
      "country":"",
      "zipCode":"",
      "emailId": "store@mail.com"
    },
    "recommendations": [
      {
      "offerBannerImages":"",
      "offerBannerRedirectUrls":""
      } 
      ],
    "components": [
      "ReceiptBrandCard",
      "ReceiptCustomerCard",
      "ReceiptNpsForm",
      "ReceiptItemsList",
      "ReceiptSummaryCard",
      "ReceiptOffersCarousel",
      "ReceiptShopOnlineLink",
      "ReceiptTxnHistoryButton",
      "ReceiptCustomerProfileForm",
      "ReceiptGetInvoiceForm",
      "ReceiptLoyaltyCard",
      "ReceiptStoreCard",
      "ReceiptStoreLocator",
      "ReceiptTerms",
      "ReceiptSocialIcons",
      "ReceiptContactUsCard",
      "ReceiptVideoEmbed",
      "ReceiptPoweredBy"
    ],
    "customerToken":""
  }
];

mock.onGet(/\/api\/ereceipt\/\d+/).reply((config) => {
  // Extract the ID from the URL
  const i = Number(config.url?.split('/').pop());
  console.log(i);
  return [200, receipts[i - 1]]
}
);
