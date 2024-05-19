<script setup lang="ts">
import AppDateTimePicker from '@/@core/components/app-form-elements/AppDateTimePicker.vue';
import type { EreceiptType } from '@/@fake-db/types'
import { ref } from 'vue'
import axios from '@axios'


interface Props {
  data?: EreceiptType
}
const show = ref(false)
const props = withDefaults(defineProps<Props>(), {
  data: {
    customer: {
      "firstName": "First Name",
      "lastName": "Last Name",
      "mobilePhone": "Mobile No",
      "emailId": "name@email.id",
      "birthDay": ""
    },
    organization: {
      "companyName": ""
    },
    customerToken: ""
  }
});
const fullName = props.data?.customer.firstName + " " + props.data?.customer.lastName;

let isFNameValid = false;
let isLNameValid = false;
let isEmailValid = false;
let isMobileValid = false;
let isDOBValid = false;
const successOrFailed = ref(false);
const notificationEnable = ref(false);
const notificationColor = ref('error');
const emailRegex = /^[a-zA-Z0-9]+(\.[a-zA-Z0-9]+)?(\+[a-zA-Z0-9]+)?@[a-zA-Z]+\.[a-zA-Z]{2,}$/;

function saveData() {
  const contactData = {
    "firstName": props.data?.customer.firstName.trim(),
    "lastName": props.data?.customer.lastName.trim(),
    "emailId": props.data?.customer.emailId.trim(),
    "mobilePhone": props.data?.customer.mobilePhone,
    "birthDay": props.data?.customer.birthDay,
    "birthMonth": props.data?.customer.udf11,
    "customerToken": props.data?.customerToken,
  };

  // Validation
  isFNameValid = props.data?.customer.firstName.trim().length > 0 || false;
  isLNameValid = props.data?.customer.lastName.trim().length > 0 || false;
  isMobileValid = props.data?.customer.mobilePhone > 0 && props.data?.customer.mobilePhone.length == 10 || false;
  if (props.data?.customer.birthDay != null && props.data?.customer.birthDay != '') {
    isDOBValid = props.data?.customer.birthDay.slice(0, 10) <= new Date().toISOString().slice(0, 10) || false;
  }
  isEmailValid = emailRegex.test(props.data?.customer.emailId) || false;


  window.console.log("DOB : " + props.data?.customer.birthDay)
  window.console.log(isEmailValid + " " + isMobileValid + " " + isDOBValid);
  if (isFNameValid && isLNameValid && isEmailValid && isMobileValid && (isDOBValid || props.data?.customer.birthDay == null)) {
    axios.post('/api/ereceipt/update-contact', contactData).then(response => {
      console.log(response.data)
    })
    show.value = !(show.value);
    notificationColor.value = 'success';
    successOrFailed.value = true;
  }
  else {
    notificationColor.value = 'error';
    successOrFailed.value = false;
  }
  notificationEnable.value = true;
}


function validateEmailId(mailId) {
  return emailRegex.test(mailId) || 'Invalid Email-ID';
}

function validateMobileNo(mobileNo) {
  return mobileNo > 0 && mobileNo.length == 10 || 'Invalid Mobile Number';
}

function validateDate(date) {
  const currentDate = new Date().toISOString().slice(0, 10);
  return date <= currentDate || 'Cannot select future dates';
}

function validateNames(name) {
  return name.trim().length > 0 || 'Enter this field';
}
const dobMonths = [
  { title: 'January', value: 'January' },
  { title: 'February', value: 'February' },
  { title: 'March', value: 'March' },
  { title: 'April', value: 'April' },
  { title: 'May', value: 'May' },
  { title: 'June', value: 'June' },
  { title: 'July', value: 'July' },
  { title: 'August', value: 'August' },
  { title: 'September', value: 'September' },
  { title: 'October', value: 'October' },
  { title: 'November', value: 'November' },
  { title: 'December', value: 'December' },
]

function startsWithAlphabet() {
  return /^[A-Za-z]/.test(props.data?.customer.firstName);
}
</script>

<template>
  <VCard class="my-2 text-center" elevation="0">
    <VSnackbar v-model="notificationEnable" location="top end" timeout="2000" :color=notificationColor>
      {{ successOrFailed ? "Updated Successfully..." : "Please Enter Valid Data" }}
    </VSnackbar>
    <div>
      <VCardText>
        <div>
          <p class="wr-text-h1">Thank you
            <span v-if="startsWithAlphabet()"> {{ data.customer.firstName }}</span>
            for shopping at
            <span v-if="data.organization.companyName">{{ data.organization.companyName }}</span>
            <span v-else>our store.</span>

          </p>
        </div>
        <div class="d-flex justify-space-between align-center">
          <div class="align-start" style="overflow: hidden;">

            <p class="mb-0 wr-text-h2 text-left" v-if="data.customer.firstName"> {{ data.customer.firstName }}
              {{ data.customer.lastName }}</p>
            <p class="mb-0 wr-text-h2 text-left" v-else>Add your name</p>

            <p class="mb-0 wr-text-h2 text-left" v-if="data.customer.mobilePhone"> {{ data.customer.mobilePhone }}</p>
            <p class="mb-0 wr-text-h2 text-left" v-else>Add your mobile number</p>

            <p class="mb-0 wr-text-h2 text-left" v-if="data.customer.emailId"> {{ data.customer.emailId }}</p>
            <p class="mb-0 wr-text-h2 text-left" v-else>Add your mail-id </p>

            <span v-if="data.customer.dobEnabled == false">
              <p class="mb-0 wr-text-h2 text-left" v-if="data.customer.udf11">Birthday Month {{ data.customer.udf11 }}</p>
              <p class="mb-0 wr-text-h2 text-left" v-else>Add your birth month</p>
            </span>
          </div>
          <div class="align-end ml-3">
            <VBtn width="120px" class="wr-text-h2" @click="show = !show">
              <span v-if="show">close</span>
              <span v-else>update profile</span>
            </VBtn>
          </div>
        </div>

      </VCardText>
    </div>
    <div class="border-sm ma-4" v-show="show">
      <VCardTitle class="text-center">
        USER PROFILE
      </VCardTitle>
      <VCardText>
        <VRow class="wr-text-h1  pl-5 mb-1">
          Name
        </VRow>
        <VRow class="pb-3 ml-1 mr-1 mt-0">
          <AppTextField type="text" :rules="[validateNames]" class="w-33 ma-2" placeholder="First Name"
            v-model="data.customer.firstName">
          </AppTextField>
          <AppTextField type="text" :rules="[validateNames]" class="w-33 ma-2" placeholder="Last Name"
            v-model="data.customer.lastName">
          </AppTextField>
        </VRow>
        <VRow class="wr-text-h1 pl-5 mb-1">
          Email Id
        </VRow>
        <AppTextField type="email" :rules="[validateEmailId]" class="ma-2" v-model="data.customer.emailId" placeholder="Email Id">
          <template #prepend-inner>
            <div class="pb-3">
              <VIcon size="20" icon="tabler-mail" class="rounded-pill" />
            </div>
          </template>
        </AppTextField><br>
        <VRow class="wr-text-h1 pl-5 mb-1">
          Mobile Number
        </VRow>
        <AppTextField maxlength="10" minlength="10" :rules="[validateMobileNo]" type="text" class="ma-2"
          v-model="data.customer.mobilePhone" placeholder="Mobile No">
          <template #prepend-inner>
            <div class="pb-3">
              <VIcon size="20" icon="tabler-device-mobile" class="rounded-pill" />
            </div>
          </template>
        </AppTextField><br>
        <span v-if="data.customer.dobEnabled == true">
          <VRow class="wr-text-h1 pl-5 mb-1">
            Date of Birth
          </VRow>
          <AppDateTimePicker type="Date" :rules="[validateDate]" class="ma-2" placeholder="YYYY-MM-DD"
            v-model="data.customer.birthDay">
            <template #prepend-inner>
              <div class="pb-3">
                <VIcon size="20" icon="tabler-calendar-due" class="rounded-pill" />
              </div>
            </template>
          </AppDateTimePicker><br>
        </span>
        <span v-if="data.customer.dobEnabled == false">
          <VRow class="wr-text-h1 pl-5 mb-1">
            Birth Month
          </VRow>
          <AppSelect v-model="data.customer.udf11" :items="dobMonths" class="ma-2" placeholder="select birth month">
          </AppSelect><br>
        </span>
        <VBtn class="w-100 pl-10 pr-10" @click="saveData()">Save</VBtn>
      </VCardText>
    </div>
  </VCard>
</template>

<style></style>
