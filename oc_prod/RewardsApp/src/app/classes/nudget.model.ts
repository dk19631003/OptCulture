export interface HEADERINFO {
    REQUESTID: string;
}

export interface COUPONCODEINFO {
    COUPONCODE: string;
    SUBSIDIARYNUMBER: string;
    STORENUMBER: string;
    DOCSID: string;
    RECEIPTNUMBER: string;
    RECEIPTAMOUNT: string;
    DISCOUNTAMOUNT: string;
    CUSTOMERID: string;
    CARDNUMBER: string;
    PHONE: string;
    EMAIL: string;
}

export interface USERDETAILS {
    USERNAME: string;
    ORGID: string;
    TOKEN: string;
}

export interface COUPONCODEENQREQ {
    HEADERINFO: HEADERINFO;
    COUPONCODEINFO: COUPONCODEINFO;
    PURCHASEDITEMS: any[];
    USERDETAILS: USERDETAILS;
}

export interface COUPONDISCOUNTINFO {
    COUPONNAME: string;
    COUPONCODE: string;
    COUPONTYPE: string;
    VALIDFROM: string;
    VALIDTO: string;
    DISCOUNTCRITERIA: string;
    EXCLUDEDISCOUNTEDITEMS: string;
    ACCUMULATEDISCOUNT: string;
    ELIGIBILITY: string;
    LOYALTYPOINTS: string;
    DISCOUNTINFO: any[];
    LOYALTYVALUECODE: string;
    DESCRIPTION: string;
    APPLYATTRIBUTES: string;
    NUDGEPROMOCODE: string;
    NUDGEDESCRIPTION: string;
}

export interface STATUSINFO {
    ERRORCODE: string;
    MESSAGE: string;
    STATUS: string;
}

export interface HEADERINFO {
    REQUESTID: string;
}

export interface BALANCE {
    AMOUNT: string;
    EXCHANGERATE: string;
    DIFFERENCE: string;
    VALUECODE: string;
}

export interface LOYALTYINFO {
    CUSTOMERID: string;
    CARDNUMBER: string;
    CARDPIN: string;
    REDEEMABLEAMOUNT: string;
    OTPENABLED: string;
    LIFETIMEPOINTS: string;
    BALANCES: BALANCE[];
    OTPREDEEMLIMIT: any[];
    DISPLAYTEMPLATE: string;
}

export interface COUPONCODERESPONSE {
    COUPONDISCOUNTINFO: COUPONDISCOUNTINFO[];
    STATUSINFO: STATUSINFO;
    HEADERINFO: HEADERINFO;
    LOYALTYINFO: LOYALTYINFO;
}

export interface NUDGETS {
    COUPONCODERESPONSE: COUPONCODERESPONSE;
}

