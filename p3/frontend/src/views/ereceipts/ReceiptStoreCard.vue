<script setup lang="ts">
import type { EreceiptType } from '@/@fake-db/types'

interface Props {
  data?: EreceiptType
}

const props = withDefaults(defineProps<Props>(), {
  data: {
    storeDetails: {
      "storeId": "Store-Id",
      "storeName": "Store-Name",
      "locality": "Address",
      "city": "City",
      "state": "State",
      "emailId": "Mail-Id",
      "googleMapLink": "NA",
      "mobileNo": "NA",
      "website": "weblink"
    },
    show: false
  }
});
const text = 'Find your nearest store'
const mapLink = props.data?.storeDetails?.goggleMapLink;
let userWebsite = "";
if (props.data?.storeDetails?.website) {
  userWebsite = props.data?.storeDetails.website.replace("https://", "").replace("http://", "").replace("/", "");
}
</script>
<template>
  <VCard class="my-2" v-if="data.storeDetails" elevation="0">
    <!-- <VCardTitle class="ml-2"><u>Store Details</u></VCardTitle> -->
    <VCardText>
      <span class="wr-text-h1" v-if="data.storeDetails.storeBrand">{{ data.storeDetails.storeBrand }}<br>
        <span class="wr-text-h2" v-if="data.storeDetails.storeName">{{ data.storeDetails.storeName }}<br></span>
      </span>
      <span v-else>
        <span class="wr-text-h1" v-if="data.storeDetails.storeName">{{ data.storeDetails.storeName }}<br></span>
      </span>
      <span class="wr-text-h2" v-if="data.storeDetails.locality">{{ data.storeDetails.locality }}</span><br>
      <span class="wr-text-h2" v-if="data.storeDetails.city">{{ data.storeDetails.city }}</span>
      <span class="wr-text-h2" v-if="data.storeDetails.zipCode">, {{ data.storeDetails.zipCode }}</span> <br><br>
      <span class="wr-text-h2" v-if="data.storeDetails.state">Region: &nbsp; {{ data.storeDetails.state }}</span> <br>
      <span class="wr-text-h2" v-if="data.storeDetails.storeManagerName">GSTIN NO: &nbsp;
        {{ data.storeDetails.storeManagerName }}<br></span>
      <span class="wr-text-h2" v-if="data.storeDetails.website">You can also shop at <a :href="data.storeDetails.website"
          target="_blank" style="color: blue;"> {{ userWebsite }}</a></span>

    </VCardText>
  </VCard>
</template>
