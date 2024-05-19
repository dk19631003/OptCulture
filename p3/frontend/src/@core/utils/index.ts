import axios from '@axios';


// 👉 IsEmpty
export const isEmpty = (value: string): boolean => {
  if (value === null || value === undefined || value === ''|| value.toString().trim() ==='' )
    return true

  return !!(Array.isArray(value) && value.length === 0)
}

// 👉 IsNullOrUndefined
export const isNullOrUndefined = (value: unknown): value is undefined | null => {
  return value === null || value === undefined
}

// 👉 IsEmptyArray
export const isEmptyArray = (arr: unknown): boolean => {
  return Array.isArray(arr) && arr.length === 0
}

// 👉 IsObject
export const isObject = (obj: unknown): obj is Record<string, unknown> =>
  obj !== null && !!obj && typeof obj === 'object' && !Array.isArray(obj)

export const isToday = (date: Date) => {
  const today = new Date()

  return (
    /* eslint-disable operator-linebreak */
    date.getDate() === today.getDate() &&
    date.getMonth() === today.getMonth() &&
    date.getFullYear() === today.getFullYear()
    /* eslint-enable */
  )
}
export const mergeTagsMap= new Map<string,string|Object>()



mergeTagsMap.set('Email', {tag: "${contact.emailId ! 'N/A'}",preview:'sample@email.com',length:20 } );
mergeTagsMap.set('Phone',  {tag: "${contact.mobilePhone ! 'N/A'}",preview:'8743456234',length:12});
mergeTagsMap.set('Loyalty Membership Number',  {tag: "${loyalty.membershipNumber ! 'Not Available'}",preview:'9573777743',length:10});
mergeTagsMap.set('AddressOne',  {tag: "${contact.addressOne ! 'N/A'}",preview:'2-12/B Hyderabad,Telangana,580001',length:20});
mergeTagsMap.set('AddressTwo',  {tag: "${contact.addressTwo ! 'N/A'}",preview:'2-12/B Hyderabad,Telangana,580001',length:20});
mergeTagsMap.set('FirstName',  {tag: "${contact.firstName ! 'N/A'}",preview:'Mathew ',length:15});
mergeTagsMap.set('LastName',  {tag: "${contact.lastName ! 'N/A'}",preview:'Williams',length:15});
mergeTagsMap.set("Contact's Homestore Address",  {tag: "${homeStore.addressStr ! 'Not Available'}",preview:'2-12/B Hyderabad,Telangana,580001',length:20});
mergeTagsMap.set('Loyalty Points Balance',  {tag: "${loyalty.loyaltyPointBalance ! 'Not Available'}",preview:'287',length:10});
mergeTagsMap.set('Loyalty Membership Currency Balance',  {tag: "${loyalty.loyaltyCurrencyBalance ! 'Not Available'}",preview:'350',length:10});
mergeTagsMap.set('City',  {tag: "${contact.city ! 'N/A'}",preview:'Hyderabad',length:15});
mergeTagsMap.set('State',  {tag: "${contact.state ! 'N/A'}",preview:'Telangana',length:15});
mergeTagsMap.set('Country',  {tag: "${contact.country ! 'N/A'}",preview:'India',length:15});
mergeTagsMap.set('Pin',  {tag: "${contact.pin ! 'N/A'}",preview:'567534',length:6});
mergeTagsMap.set('Gender',  {tag: "${contact.gender ! 'N/A'}",preview:'MALE',length:6});
mergeTagsMap.set('Birthday',  {tag: "${contact.birthDay ! 'N/A'}",preview:'2023-12-05',length:10});
mergeTagsMap.set('Anniversary',  {tag: "${contact.anniversary ! 'N/A'}",preview:'2023-12-05',length:10});
mergeTagsMap.set("Contact's Homestore Phone",  {tag: "${homeStore.phone ! 'Not Available'}",preview:'8364684349',length:10});
mergeTagsMap.set("Contact's Homestore Street",  {tag: "${homeStore.street ! 'Not Available'}",preview:'Gandhinagar',length:20});
mergeTagsMap.set("Contact's Homestore City",  {tag: "${homeStore.city ! 'Not Available'}",preview:'Hyderabad',length:10});
mergeTagsMap.set("Contact's Homestore State",  {tag: "${homeStore.state ! 'Not Available'}",preview:'Telangana',length:10});
mergeTagsMap.set("Contact's Homestore Zip",  {tag: "${homeStore.zip ! 'Not Available'}",preview:'580001',length:10});
mergeTagsMap.set('Organization Name',  {tag: "${organization.organizationName ! 'Not Available'}",preview:'Optculture',length:20});
mergeTagsMap.set('Date_today',  {tag: "${.now?date}",preview:'2024-10-12',length:10});
mergeTagsMap.set('Date_tomorrow',  {tag: "${date.tomorrow}",preview:'2024-10-12',length:10});
mergeTagsMap.set('Date_7_days',  {tag: "${date.next_7_days}",preview:'2024-10-12',length:10});
mergeTagsMap.set('Date_30_days',  {tag: "${date.next_30_days}",preview:'2024-10-12',length:10});
mergeTagsMap.set('Date_X_days',  {tag: "${date.next_x_days}",preview:'2024-10-12',length:10});
mergeTagsMap.set('Loyalty Membership Pin',  {tag: "${loyalty.membershipPin ! 'Not Available'}",preview:'888554',length:6});
mergeTagsMap.set('Loyalty Enrollment Date',  {tag: "${loyalty.enrollmentDate ! 'Not Available'}",preview:'2024-10-12',length:10});
mergeTagsMap.set('Loyalty Enrollment Source',  {tag: "${loyalty.enrollmentSource ! 'Not Available'}",preview:'POS',length:15});
mergeTagsMap.set('Loyalty Enrollment Store',  {tag: "${loyalty.enrollmentStore ! 'Not Available'}",preview:'Store1',length:15});
mergeTagsMap.set('Loyalty Gift Balance',  {tag: "${loyalty.giftBalance ! 'Not Available'}",preview:'230',length:10});
mergeTagsMap.set('Loyalty LifeTime Purchase Value',  {tag: "${loyalty.lifeTimePurchaseValue ! 'Not Available'}",preview:'5040',length:10});
mergeTagsMap.set('Loyalty Lifetime Points',  {tag: "${loyalty.lifetimePoints ! 'Not Available'}",preview:'5094',length:10});
mergeTagsMap.set('Loyalty Member Status',  {tag: "${loyalty.membershipStatus ! 'Not Available'}",preview:'Active',length:10});
mergeTagsMap.set("Loyalty Last Threshold Level",  {tag: "${loyalty.lastThresholdLevel ! 'Not Available'}",preview:'450',length:10});
mergeTagsMap.set("Loyalty Member Tier",  {tag: "${loyalty.tier.programTierName ! 'Not Available'}",preview:'Tier 1',length:10});
mergeTagsMap.set("Loyalty Redeemable Currency Balance",  {tag: "${loyalty.redeemedCurrency ! 'Not Available'}",preview:'200',length:10});
mergeTagsMap.set("Loyalty Hold Balance",  {tag: "${loyalty.holdBalance ! 'Not Available'}",preview:'200',length:10});
mergeTagsMap.set("Loyalty Reward Activation Period",  {tag: "${loyalty.tier.rewardActivationPeriod ! 'Not Available'}",preview:'2',length:5});
mergeTagsMap.set("Loyalty Last Earned Value",  {tag: "${loyalty.lastEarnedValue ! 'Not Available'}",preview:'207',length:10});
mergeTagsMap.set("Loyalty Last Redeemed Value",  {tag: "${loyalty.lastRedeemedValue ! 'Not Available'}",preview:'207',length:10});
mergeTagsMap.set("Loyalty Membership Password",  {tag: "${loyalty.membershipPassword ! 'Not Available'}",preview:'Opt@3465',length:10});
mergeTagsMap.set("Loyalty Reward Expiration Period",  {tag: "${loyalty.tier.rewardExpirationPeriod ! 'Not Available'}",preview:'1',length:5});
mergeTagsMap.set("Loyalty Gift Amount Expiration Period",  {tag: "${loyalty.giftAmountExpirationPeriod ! 'Not Available'}",preview:'1',length:5});
mergeTagsMap.set("Loyalty Membership Expiration Date",  {tag: "${loyalty.tier.membershipExpirationDate ! 'Not Available'}",preview:'2024-03-12',length:10});
mergeTagsMap.set("Loyalty Gift-card Expiration Date",  {tag: "${loyalty.giftCardExpriationDate ! 'Not Available'}",preview:'2024-03-12',length:10});
mergeTagsMap.set("Loyalty Last Bonus Value",  {tag: "${loyalty.lastBonusValue ! 'Not Available'}",preview:'350',length:10});
mergeTagsMap.set("Loyalty Reward Expiring Value",  {tag: "${loyalty.tier.rewardExpiringValue ! 'Not Available'}",preview:'350',length:10});
mergeTagsMap.set("Loyalty Gift Amount Expiring Value",  {tag: "${loyalty.giftAmountExpiringValue ! 'Not Available'}",preview:'350',length:10});


const oldToNewMergeTagMap = new Map([
  ["|^GEN_email / DEFAULT=", "${contact.emailId ! 'N/A'}"],
  ["|^GEN_firstName / DEFAULT=", "${contact.firstName ! 'N/A'} "],
  ["|^GEN_lastName / DEFAULT=", "${contact.lastName ! 'N/A'}"],
  ["|^GEN_addressOne / DEFAULT=", "${contact.addressOne ! 'N/A'}"],
  ["|^GEN_addressTwo / DEFAULT=", "${contact.addressTwo ! 'N/A'}"],
  ["|^GEN_city / DEFAULT=", "${contact.city ! 'N/A'}"],
  ["|^GEN_state / DEFAULT=", "${contact.state ! 'N/A'}"],
  ["|^GEN_country / DEFAULT=", "${contact.country ! 'N/A'}"],
  ["|^GEN_pin / DEFAULT=", "${contact.pin ! 'N/A'}"],
  ["|^GEN_phone / DEFAULT=", "${contact.mobilePhone ! 'N/A'}"],
  ["|^GEN_gender / DEFAULT=", "${contact.gender ! 'N/A'}"],
  ["|^GEN_birthday / DEFAULT=", "${contact.birthday ! 'N/A'}"],
  ["|^GEN_anniversary / DEFAULT=", "${contact.anniversary ! 'N/A'}"],
  ["|^DATE_today^|", "${.now?date}"],
  ["|^DATE_tomorrow^|", "${date.tomorrow}"],
  ["|^DATE_7_days^|", "${date.next_7_days}"],
  ["|^DATE_30_days^|", "${date.next_30_days}"],
  ["|^DATE_X_days^|", "${date.next_x_days}"],
  ["|^GEN_loyaltyMembershipNumber / DEFAULT=", "${loyalty.membershipNumber ! 'Not Available'}"],
  ["|^GEN_loyaltyMembershipPin / DEFAULT=", "${loyalty.membershipPin ! 'Not Available'}"],
  ["|^GEN_loyaltyEnrollmentDate/ DEFAULT=", "${loyalty.enrollmentDate ! 'Not Available'}"],
  ["|^GEN_loyaltyEnrollmentSource/ DEFAULT=", "${loyalty.enrollmentSource ! 'Not Available'}"],
  ["|^GEN_loyaltyEnrollmentStore / DEFAULT=", "${loyalty.enrollmentStore ! 'Not Available'}"],
  ["|^GEN_loyaltyPointsBalance / DEFAULT=", "${loyalty.loyaltyPointBalance ! 'Not Available' }"],
  ["|^GEN_loyaltyMembershipCurrencyBalance / DEFAULT=", "${loyalty.loyaltyCurrencyBalance ! 'Not Available'}"],
  ["|^GEN_loyaltyGiftBalance / DEFAULT=", "${loyalty.giftBalance ! 'Not Available'}"],
  ["|^GEN_loyaltyLifetimePurchaseValue / DEFAULT=", "${loyalty.lifeTimePurchaseValue ! 'Not Available'}"],
  ["|^GEN_loyaltyLifetimePoints / DEFAULT=", "${loyalty.lifetimePoints ! 'Not Available'}"],
  ["|^GEN_loyaltyMemberStatus / DEFAULT=", "${loyalty.membershipStatus ! 'Not Available'}"],
  ["|^GEN_organizationName / DEFAULT=", "${organization.organizationName ! 'Not Available'}"],
  ["|^GEN_contactHomestoreAddress / DEFAULT=", "${homeStore.addressStr ! 'Not Available'}"],
  ["|^GEN_storePhone / DEFAULT=", "${homeStore.phone ! 'Not Available'}"],
  ["|^GEN_storeStreet / DEFAULT=", "${homeStore.street ! 'Not Available'}"],
  ["|^GEN_storeCity / DEFAULT=", "${homeStore.city ! 'Not Available'}"],
  ["|^GEN_storeState / DEFAULT=", "${homeStore.state ! 'Not Available'}"],
  ["|^GEN_storeZip / DEFAULT=", "${homeStore.zip ! 'Not Available'}"],
  ["|^CC_","${coupon.CC_}"]
 ]);

 const keys = [
  "|^GEN_loyaltyLastThresholdLevel / DEFAULT=",
  "|^GEN_loyaltyMemberTier / DEFAULT=",
  "|^GEN_loyaltyRC / DEFAULT=",
  "|^GEN_loyaltyHoldBalance / DEFAULT=",
  "|^GEN_loyaltyRewardActivationPeriod / DEFAULT=",
  "|^GEN_loyaltyLastEarnedValue / DEFAULT=",
  "|^GEN_loyaltyLastRedeemedValue / DEFAULT=",
  "|^GEN_loyaltyMembershipPassword / DEFAULT=",
  "|^GEN_loyaltyRewardExpirationPeriod / DEFAULT=",
  "|^GEN_loyaltyGiftAmountExpirationPeriod / DEFAULT=",
  "|^GEN_loyaltyMembershipExpirationDate / DEFAULT=",
  "|^GEN_loyaltyGiftCardExpirationDate / DEFAULT=",
  "|^GEN_loyaltyLastBonusValue / DEFAULT=",
  "|^GEN_loyaltyRewardExpiringValue / DEFAULT=",
  "|^GEN_loyaltyGiftAmountExpiringValue / DEFAULT="
];

// Provided strings for values
const values = [
  "${loyalty.lastThresholdLevel}",
  "${loyalty.tier.programTierName ! 'Not Available'}",
  "${loyalty.redeemedCurrency ! 'Not Available'}",
  "${loyalty.holdBalance ! 'Not Available'}",
  "${loyalty.tier.rewardActivationPeriod ! 'Not Available'}",
  "${loyalty.lastEarnedValue ! 'Not Available'}",
  "${loyalty.lastRedeemedValue ! 'Not Available'}",
  "${loyalty.membershipPassword ! 'Not Available'}",
  "${loyalty.tier.rewardExpirationPeriod ! 'Not Available'}",
  "${loyalty.giftAmountExpirationPeriod ! 'Not Available'}",
  "${loyalty.tier.membershipExpirationDate ! 'Not Available'}",
  "${loyalty.giftCardExpriationDate ! 'Not Available'}",
  "${loyalty.lastBonusValue ! 'Not Available'}",
  "${loyalty.tier.rewardExpiringValue ! 'Not Available'}",
  "${loyalty.giftAmountExpiringValue ! 'Not Available'}"
];

keys.forEach((key, index) => { //intialization of map
  oldToNewMergeTagMap.set(key, values[index]);
});

////estimated length of characters after replacing
const tagLengthMap=new Map([
  ["${contact.emailId ! 'N/A'}",20],
  ["${contact.firstName ! 'N/A'}",20],
  ["${contact.lastName ! 'N/A'}",20],
  ["${contact.addressOne ! 'N/A'}",20],
  ["${contact.addressTwo ! 'N/A'}",20],
  ["${contact.city ! 'N/A'}",20],
  ["${contact.state ! 'N/A'}",20],
  ["${contact.country ! 'N/A'}",15],
  ["${contact.pin ! 'N/A'}",20],
  ["${contact.mobilePhone ! 'N/A'}",12],
  ["${contact.gender ! 'N/A'}",6],
  ["${contact.birthDay ! 'N/A'}",20],
  ["${date.next_7_days}",10],
  ["${contact.anniversary ! 'N/A'}",20],
  ["${.now?date}",10],
  ["${date.next_30_days}",10],
  ["${date.next_x_days}",10],
  ["${loyalty.membershipNumber ! 'Not Available'}",15],
  ["${loyalty.membershipPin ! 'Not Available'}",13],
  ["${loyalty.enrollmentDate ! 'Not Available'}",13],
  ["${loyalty.enrollmentSource ! 'Not Available'}",13],
  ["${loyalty.enrollmentStore ! 'Not Available'}",15],
  ["${loyalty.loyaltyPointBalance ! 'Not Available' }",13],
  ["${loyalty.loyaltyCurrencyBalance ! 'Not Available'}",13],
  ["${loyalty.giftBalance ! 'Not Available'}",13],
  ["${loyalty.lifeTimePurchaseValue ! 'Not Available'}",13],
  ["${loyalty.lifetimePoints ! 'Not Available'}",13],
  ["${loyalty.membershipStatus ! 'Not Available'}",13],
  ["${organization.organizationName ! 'Not Available'}",13],
  ["${homeStore.addressStr ! 'Not Available'}",20],
  ["${homeStore.phone ! 'Not Available'}",13],
  ["${homeStore.street ! 'Not Available'}",20],
  ["${homeStore.city ! 'Not Available'}",13],
  ["${homeStore.state ! 'Not Available'}",13],
  ["${homeStore.zip ! 'Not Available'}",13],
  ["${loyalty.lastThresholdLevel ! 'Not Available'}",13],
  ["${loyalty.tier.programTierName ! 'Not Available'}",13], 
  ["${loyalty.redeemedCurrency ! 'Not Available'}",13],
  ["${loyalty.holdBalance ! 'Not Available'}",13],
  ["${loyalty.tier.rewardActivationPeriod ! 'Not Available'}",13],
  ["${loyalty.lastEarnedValue ! 'Not Available'}",13],
  ["${loyalty.lastRedeemedValue ! 'Not Available'}",13],
  ["${loyalty.membershipPassword ! 'Not Available'}",13],
  ["${loyalty.tier.rewardExpirationPeriod ! 'Not Available'}",13],
  ["${loyalty.giftAmountExpirationPeriod ! 'Not Available'}",13],
  ["${loyalty.tier.membershipExpirationDate ! 'Not Available'}",13],
  ["${loyalty.giftCardExpriationDate ! 'Not Available'}",13],
  ["${loyalty.lastBonusValue ! 'Not Available'}",13],
  ["${loyalty.tier.rewardExpiringValue ! 'Not Available'}",13],
  ["${loyalty.giftAmountExpiringValue ! 'Not Available'}",13]
  ])

 export function oldToNewMergeTags(msgContent:string){
  
  const pattern = /\|\^.*?\^\|/g //patrn to match old merge tags
  const replacedString = msgContent.replace(pattern, (match) => {
   
    const index1=match.indexOf('=')
    const index2=match.indexOf('^|')
    if(match.includes('|^CC_')){
      return '${coupon.CC_'+match.substring(5,index2)+'}'; //coupons
    }
    if(index1==-1){
      return oldToNewMergeTagMap.get(match); // like |^DATE_today^| etc. tags that don't have default values.
    }
    else{
      // const defaultVal=match.substring(index1+1,index2); //DEFAULT=
      
      // if(isEmpty(defaultVal)){ 
        //  //if no default text provided
      // } 
      // else{
      //   let newmergeTag=oldToNewMergeTagMap.get(match.substring(0,index1+1)) //${homeStore.zip ! 'Not Available'} from map
       
      //   if(newmergeTag?.toLowerCase().includes('n/a')){
      //     console.log(newmergeTag.replace('N/A',defaultVal)) //setting user's defaultvalue to new mergetags
      //     return newmergeTag.replace('N/A',defaultVal);
      //   }
      // else {
      //   return newmergeTag?.replace('Not Available',defaultVal);
      // }
      return oldToNewMergeTagMap.get(match.substring(0,index1+1));
      }
    }
  );
 //calculates estimated length of msg after replacing tags for calculating credits.
  const length=getLengthOfMsg(replacedString);  
  return [replacedString,length];
 }

function getLengthOfMsg(msg:string){ //
  let length:number=0;
  let startInd=0; 
  let endInd=0;
  const pattern = /\$\{.*?\}/g; //patern to match new merge tags
  const matches = msg.match(pattern);
  for(let i in matches){
    const str=matches[Number(i)] //matched string
    endInd=msg.indexOf(str)
    let len=tagLengthMap.get(str)??0 //get est. length
    if(str.includes('${coupon.')) len=8
  
    length+=endInd-startInd
    length+=len==0?str.length:len; 
    
    startInd=endInd+str.length ;
    
  }
  length+=msg.length-startInd;
  // console.log(msg.length+'-'+length)
  return length;
 }

 export  async function URLShortner(msgContent:string){
  const regexPattern = /^(https?:\/\/|www\.)|\.(com|in|ae)$/; 
  console.log(msgContent)
   let urlsSet=new Set<string>()
   const testStrings=msgContent.split(' ');
   testStrings.forEach((str) => {
      const matches = str.match(regexPattern);
      if (matches) {
       
          if(str.length > 22 && !(str.includes('${')))
            urlsSet.add(str)
      }
    });
  
  if(urlsSet.size==0) return msgContent;

  let shortCodesArray=new Array<string>();
  shortCodesArray = await getShortURL([...urlsSet]);
    
    [...urlsSet].forEach((url,index)=>{ //replacing shortURLs
      msgContent=msgContent.replaceAll(url,shortCodesArray[index])
    })
    
    return msgContent;
 }
 
async function getShortURL(urlsArray:Array<string>){
    try{
        const params = new URLSearchParams();
        urlsArray.forEach((value, index) => {
        params.append('urls', value);
        });
        const resp=await axios.get('/api/campaigns/url-shortner',{
          params:params
        })
        // console.log(resp);
        return resp.data;
        
    }catch(err){
      console.log('error while shortineing URLs')
    }
    
 }
 
