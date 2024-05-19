import { zipCode } from '@form-validation/validator-zip-code';
import parsePhoneNumber from 'libphonenumber-js';
import { isEmpty } from '@/@core/utils';
const userData = JSON.parse(localStorage.getItem('userData'));
let currencyInfo=''
let locale=''
if(userData){
currencyInfo= userData.currencyInfo

locale = currencyInfo.split(',')[0]; //{en-IN,INR},{en-US,USD}
if(locale=='ne-IN') locale='en-IN'
}
export function numberFormatter(value:string){
 if(isEmpty(value)) return '-'
 return Number(value).toLocaleString(locale)
}
export function getCurrencySymbol() {
  //locale ex.'es-US'
    const currency = currencyInfo.split(',')[1]; //currency code ex.'USD'
    return (0).toLocaleString(locale, { style: 'currency', currency, minimumFractionDigits: 0, maximumFractionDigits: 0 }).replace(/\d/g, '').trim()

}
export function dateFormater(dateString:string){
    if(dateString==null) return '-'
    const date = new Date(dateString);
    if(locale=='ne-IN') locale='en-IN' //for indian native english
    return date.toLocaleString(locale).replaceAll(',','').toLowerCase()
}
export function phoneValidator(mobile:string){
  const localeCountry=locale.split('-')[1] // second part of locale is country code
  const phoneNumber = parsePhoneNumber(mobile, localeCountry);
 
  let mobileValidAckObj={
    'valid':false,
    'number':''
  }
  if(phoneNumber){
  mobileValidAckObj.valid=phoneNumber.isValid()
  mobileValidAckObj.number= phoneNumber.format('NATIONAL', {nationalPrefix: false}).replaceAll(' ','') //format number by removing white spaces,symbols etc.
  }
  return mobileValidAckObj;
}
export function validateZip(pinCode:string){
  const res1 = zipCode().validate({
    value: pinCode,
    options: {
        country:locale.split('-')[1],
        message: 'Please enter a valid zipcode',
    },
});
// console.log(res1)
return res1;
}
export function getUTCToLocalTime(dateString:any){
    if(isEmpty(dateString)) return null;
    const newDate = new Date(dateString);
    const localDateTime = new Date();

// Get the current UTC date and time
const utcDateTime = new Date(Date.now() + localDateTime.getTimezoneOffset() * 60000);

// Calculate the time difference in milliseconds
const timeDifference = localDateTime - utcDateTime;

newDate.setMinutes(newDate.getMinutes() + timeDifference / (1000 * 60));
// const formattedDate = `${newDate.getFullYear()}-${(newDate.getMonth() + 1).toString().padStart(2, '0')}-${newDate.getDate().toString().padStart(2, '0')}T${newDate.getHours().toString().padStart(2, '0')}:${newDate.getMinutes().toString().padStart(2, '0')}:${newDate.getSeconds().toString().padStart(2, '0')}`;
// console.log(newDate)
const formattedDate = `${newDate.getFullYear()}-${(newDate.getMonth() + 1).toString().padStart(2, '0')}-${newDate.getDate().toString().padStart(2, '0')} ${newDate.getHours().toString().padStart(2, '0')}:${newDate.getMinutes().toString().padStart(2, '0')}:${newDate.getSeconds().toString().padStart(2, '0')}`;
// console.log(formattedDate);
return formattedDate;
}

export function getUTCTime(date: string) {
  if(isEmpty(date)) return null;
  const currentUtcTime = new Date().toISOString();
  const currentTime = new Date();
  // Convert currentUtcTime to a Date object
  const currentUtcDate = new Date(currentUtcTime);

  // Calculate the time difference in milliseconds
  const timeDiff = currentTime - currentUtcDate;
  const newDate = new Date(date);
  newDate.setMinutes(newDate.getMinutes() - timeDiff / (1000 * 60)); //convert sch
 
  return newDate.toISOString();
}
