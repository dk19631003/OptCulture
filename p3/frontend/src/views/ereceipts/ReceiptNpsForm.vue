<script setup lang="ts">

import AppTextField from '@/@core/components/app-form-elements/AppTextField.vue';
import { EreceiptType } from '@/@fake-db/types';
import axios from 'axios';
import { reactive } from 'vue';

interface Props {
  data?: EreceiptType
}

const props = withDefaults(defineProps<Props>(), {
  data: {
    customerToken: "",
    receiptFeedbackAvailable: "",
    receipt: {
      "docSid": "NA"
    },
    customer: {
      "mobilePhone": "NA"
    },
    storeDetails: {
      "storeName": "NA"
    },
    npsOptions: [
      "Collection",
      "Pricing",
      "Customer service",
      "Billing payment",
      "Store ambiance",
      "Other"
    ],
    organization: {
      "companyName": ""
    }
  }
});

const state = reactive({
  step1: true,
  step2A: false,
  step2: false,
  step3: false,
  step4: false,
  ratings: [
    { value: 1, color: "#B30000" },
    { value: 2, color: "#E60000" },
    { value: 3, color: "#FF1A1A" },
    { value: 4, color: "#FF4D4D" },
    { value: 5, color: "#FF8080" },
    { value: 6, color: "#FFB3B3" },
    { value: 7, color: "#77D57A" },
    { value: 8, color: "#50C955" },
    { value: 9, color: "#36AF3B" },
    { value: 10, color: "#2A882E" }
  ],

});

let customerRating: string = "";
let shoppingExp = {}
let optionalTextBox: string = "";

function isFeedbackAvailable() {
  if (props.data?.receiptFeedbackAvailable) {
    state.step1 = false;
    state.step2 = false;
    state.step3 = false;
    state.step4 = true
  }
}

isFeedbackAvailable();

function selectRating(value) {
  let id = document.getElementById('item_' + value);
  id.style.height = 45 + 'px';
  id.style.width = 45 + 'px';
  clearRating(value)
}
function clearRating(value) {
  for (let rate of state.ratings) {
    if (rate.value == value) {
      continue;
    }
    else {
      let id = document.getElementById('item_' + rate.value);
      id.style.height = 38 + 'px';
      id.style.width = 40 + 'px';
    }
  }
}
function mouseLeave(value) {
  let id = document.getElementById('item_' + value);
  id.style.height = 38 + 'px';
  id.style.width = 40 + 'px';
}
function step2(value) {
  customerRating = value;

  let id = document.getElementById('item_' + value);
  if (value >= 0 && value <= 6) {
    state.step2A = !(state.step2A)
  }
  state.step1 = !(state.step1);
  state.step2 = !(state.step2);
}

function selectOption(index) {
  console.log("index :" + index);
  const optionId = document.getElementById('fb_' + index);
  if (shoppingExp['udf' + (index + 2)] == 1) {
    shoppingExp['udf' + (index + 2)] = 0
    optionId.style.border = ''
  }
  else {
    shoppingExp['udf' + (index + 2)] = 1
    optionId.style.border = '3px solid #339933'
    // optionId.style.backgroundColor = '#339933';
  }
}
function step3() {
  state.step2 = !(state.step2)
  state.step3 = !(state.step3)
  console.log(shoppingExp)
}
function step4() {
  state.step3 = !(state.step3)
  state.step4 = !(state.step4)

  saveNpsData();
}
function step2Back() {
  state.step1 = !(state.step1)
  state.step2A = false;
  state.step2 = !state.step2
  shoppingExp = new Map()
}

function step3Back() {
  state.step3 = !(state.step3)
  state.step2 = !(state.step2)
}

function setUdf(index: any) {
  shoppingExp['udf' + (index + 2)] = 0
}

function saveNpsData() {

  const npsData = {
    "customerRating": customerRating + "",
    "shoppingExp": shoppingExp,
    "optionalTextBox": optionalTextBox + "",
    "docSid": props.data?.receipt.docSid + "",
    "mobilePhone": props.data?.customer.mobilePhone + "",
    "storeName": props.data?.storeDetails.storeName + "",
    "customerToken": props.data?.customerToken
  };
  console.log(npsData)
  console.log(shoppingExp)
  axios.post('/api/ereceipt/nps', npsData).then(response => {
    console.log(response.data)
  })
}
</script>
<template>
  <VCard class="my-2" elevation="0">
    <div class="my-2" v-show="state.step1">
      <VCardText>
        <div class="text-center wr-text-h1">On a scale of 0 to 10, How likely you would recommend
          <span v-if="data.organization.companyName"> {{ data.organization.companyName }}</span>
          <span v-else> our store </span> to your
          friends and family?
        </div>
      </VCardText>
      <VCardText>
        <div class="rating-container">
          <div v-for=" rating in state.ratings " :key="rating.value" @mouseover="selectRating(rating.value)"
            @mouseleave="mouseLeave(rating.value)" :style="{ background: rating.color }" class="rating-box"
            @click="step2(rating.value)" :id="'item_' + rating.value">
            {{ rating.value }}
          </div>
        </div>

        <div class="d-flex justify-space-between mt-2">
          <div class="align-start wr-text-h4" style="color: #B30000;">
            Not likely </div>
          <div class="align-end wr-text-h4" style="color: #2A882E;">
            Very Likely</div>
        </div>

      </VCardText>
    </div>
    <div class="my-2" v-if="state.step2">
      <VIcon size="25" icon="tabler-arrow-narrow-left" class="ml-4 mt-2" @click="step2Back"></VIcon>
      <VCardText class="mt-n3">
        <div v-if="state.step2A" class="w-100 text-center wr-text-h2">We are sorry,We are unable to fulfill your needs,
          Hope we will provide our best service's in near future
        </div>
        <div class="w-100 text-center wr-text-h2" v-else>
          Great to know you enjoyed shopping with us, what made you have an amazing shopping experience today?
        </div><br>
        <div class="container">
          <div v-for="(feedback, index) in props.data?.npsOptions" :key="index">
            <VBtn class="text-center wr-text-h2 pa-2 w-100" style="width: 180px;" @click="selectOption(index)"
              :id="'fb_' + index">
              {{ setUdf(index) }} {{ feedback }}
            </VBtn>
          </div>
        </div>
      </VCardText>
      <VCardText>
        <div class="text-center">
          <VBtn @click="step3()" color="success">Next</VBtn>
        </div>
      </VCardText>
    </div>
    <div class="my-2" v-show="state.step3">
      <VIcon size="25" icon="tabler-arrow-narrow-left" class="ml-4 mt-2" @click="step3Back()"></VIcon>
      <VCardText>
        <div class="w-100 text-center wr-text-h2">
          Please specify, what made you have an amazing shopping experience today? (Optional)</div>
      </VCardText>
      <AppTextField placeholder="Please specify" class="pl-10 pr-10" v-model="optionalTextBox"></AppTextField>
      <VBtn class="w-75 rounded pa-2 mx-13 my-5" @click="step4()">Submit</VBtn>
    </div>
    <div class="my-2" v-show="state.step4">
      <p class="wr-text-h1 text-center ma-5">Thank you for your valuable feedback</p>
    </div>
  </VCard>
</template>
<style scoped>
.rating-container {
  display: flex;
  justify-content: center;
}

.rating-box {
  width: 40px;
  height: 38px;
  display: flex;
  cursor: pointer;
  justify-content: center;
  align-items: center;
  color: #FFF;
  border-right: 0.5px #F3F3F1 solid;
}

#item_1 {
  border-top-left-radius: 7.21px;
  border-bottom-left-radius: 7.21px;
}

#item_10 {
  border-top-right-radius: 7.21px;
  border-bottom-right-radius: 7.21px;
}

.container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}
</style>
