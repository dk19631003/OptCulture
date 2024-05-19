<script setup lang="ts">

import type { EreceiptType } from '@/@fake-db/types'
import axios from '@axios'
import { isEmpty } from '@/@core/utils'

interface Props {
    data?: EreceiptType
}

const props = withDefaults(defineProps<Props>(), {
    data: {
        productIds: "ABC",
        customerToken: "",
        preview: 1
    }
})
let lineItems = ref([])
lineItems.value = props.data.lineItem;
const recommendationList = ref([]);
let item1 = ref([])
let item2 = ref([])
const departmentStr = ref('')
const colorStr = ref('')
const userId = ref('')
let priceList = []


let productIds = []


async function getRecom() {
    for (let i = 0; i < lineItems.value.length; i++) {

        /*let eachCode: string = lineItems.value[i].skuInventory.udf12;
        if (eachCode) {
            productIds.push(eachCode);
        }*/
        userId.value = lineItems.value[i].skuInventory.userId;

        priceList.push(lineItems.value[i].skuInventory.listPrice);// all items prices

        if (lineItems.value[i].skuInventory.departmentCode) {
            if (!isEmpty(departmentStr.value) && departmentStr.value != null) {
                if (!departmentStr.value.toLowerCase().includes(`'${lineItems.value[i].skuInventory.departmentCode.toLowerCase()}'`)) {
                    departmentStr.value += `,${lineItems.value[i].skuInventory.departmentCode}`;
                }
            } else {
                departmentStr.value = `${lineItems.value[i].skuInventory.departmentCode}`;
            }
        }
        if (lineItems.value[i].skuInventory.udf3) {
            if (!isEmpty(colorStr.value) && colorStr.value != null) {
                if (!colorStr.value.toLowerCase().includes(`'${lineItems.value[i].skuInventory.udf3.toLowerCase()}'`)) {
                    colorStr.value += `,${lineItems.value[i].skuInventory.udf3}`;
                }
            } else {
                colorStr.value = `${lineItems.value[i].skuInventory.udf3}`;
            }
        }

    }
    console.log("department and colors and uid " + departmentStr.value + " " + colorStr.value + "" + userId.value);
    console.log("prices " + priceList);

    //console.log("-->ProductIds<--", productIds)
    /*let map = {
        product_id: productIds
    }*/
    let reqParams = {
        departments: departmentStr.value,
        color: colorStr.value,
        priceList: priceList.values
    }
    if (!isEmpty(departmentStr.value)) {
        axios.post(`/api/auto-recommendations/${userId.value}`, reqParams, {

        }).then(response => {
            console.log(response.data)
            recommendationList.value = response.data;
            if (recommendationList.value.length > 0) {
                for (let i = 0; i <= recommendationList.value.length; i++) {
                    if (i > 3) break;
                    if (i == 0 || i == 1)
                        item1.value.push(recommendationList.value[i]);
                    else
                        item2.value.push(recommendationList.value[i]);

                    if (recommendationList.value[i].description != null && recommendationList.value[i].description.length > 0 && corouselHeight == "270px")
                        corouselHeight = "300px"

                }
            }

            console.log(item1);
            console.log(item2);
        })
        /*const resp = await axios.post('https://5rlc65g16m.execute-api.us-east-2.amazonaws.com/api/rec/1072', map).then(response => {
            console.log(response.data)
            recommendationList.value = response.data;
            for (let i = 0; i <= recommendationList.value.length; i++) {
                if (i > 3) break;
                if (i == 0 || i == 1)
                    item1.value.push(recommendationList.value[i]);
                else
                    item2.value.push(recommendationList.value[i]);

                if (recommendationList.value[i].description.length > 0 && corouselHeight == "270px")
                    corouselHeight = "300px"


            }
            console.log(item1);
            console.log(item2);
        })*/
    }
}

let corouselHeight = "270px"

const defaultItems = ref([]);
if (props.data?.preview) {
    console.log('confi')
    defaultItems.value.push('https://fastly.picsum.photos/id/250/200/200.jpg?hmac=23TaEG1txY5qYZ70amm2sUf0GYKo4v7yIbN9ooyqWzs')
    defaultItems.value.push('https://fastly.picsum.photos/id/64/200/200.jpg?hmac=lJVbDn4h2axxkM72s1w8X1nQxUS3y7li49cyg0tQBZU')
}
if (props.data?.customerToken) {
    console.log('receipt')
    // defaultItems.value = []
    getRecom()
}

function getUrl(url: string) {
    const link = 'https://styleunion.in/products/' 
    const utmParams = '?utm_source=oc_recommendations&utm_medium=oc_webreceipts';
    return `${link}${url}${utmParams}`;
}



</script>
<template>
    <VCard elevation="0">
        <VCardText v-if="defaultItems.length > 0">
            <p class="wr-text-h1 text-center">You may also like</p>
            <VRow>
                <VCol v-for="(image_url, index) in defaultItems" :key="index">
                    <img :src="image_url" class="image">
                </VCol>
            </VRow>
        </VCardText>
        <VCardText v-if="item1.length > 0">
            <p class="wr-text-h1 text-center">You may also like</p>

            <v-carousel cycle :show-arrows="false" :height="corouselHeight" hide-delimiter-background
                v-if="data.productIds != 'ABC'">
                <v-carousel-item>
                    <VRow>
                        <VCol v-for="(product1, index) in item1" :key="index">
                            <a :href="getUrl(product1.url)" target="_blank">
                                <img :src="product1.image_url" class="image">
                                <p class="mb-0 wr-text-h2">{{ product1.department }}</p>
                            </a>
                            <p class="mb-0 wr-text-h3">{{ product1.description }}</p>
                            <!-- <p class="mb-0 wr-text-h3">&#8377; {{ Number(product1.price).toFixed(2) }}</p> -->
                        </VCol>
                    </VRow>
                </v-carousel-item>
                <v-carousel-item v-if="item2.length > 0">
                    <VRow>
                        <VCol v-for="(product2, index) in item2" :key="index">
                            <a :href="getUrl(product2.url)" target="_blank">
                                <img :src="product2.image_url" class="image">
                                <p class="mb-0 wr-text-h2">{{ product2.department }}</p>
                            </a>
                            <p class="mb-0 wr-text-h3">{{ product2.description }}</p>
                            <!-- <p class="mb-0 wr-text-h3">&#8377; {{ Number(product2.price).toFixed(2) }}</p> -->
                        </VCol>
                    </VRow>
                </v-carousel-item>
            </v-carousel>
        </VCardText>
    </VCard>
</template>
<style scoped>
.image {
    width: 100%;
    max-width: 180px;
    height: auto;
    max-height: 180px;
}
</style>
