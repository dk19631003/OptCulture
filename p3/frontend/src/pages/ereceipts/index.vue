<script setup lang="ts">
import ReceiptBrandCard from '@/views/ereceipts/ReceiptBrandCard.vue'
import ReceiptCustomerCard from '@/views/ereceipts/ReceiptCustomerCard.vue'
import ReceiptGetInvoiceForm from '@/views/ereceipts/ReceiptGetInvoiceForm.vue'
import ReceiptItemsList from '@/views/ereceipts/ReceiptItemsList.vue'
import ReceiptNpsForm from '@/views/ereceipts/ReceiptNpsForm.vue'
import ReceiptOffersCarousel from '@/views/ereceipts/ReceiptOffersCarousel.vue'
import ReceiptPoweredBy from '@/views/ereceipts/ReceiptPoweredBy.vue'
import ReceiptShopOnlineLink from '@/views/ereceipts/ReceiptShopOnlineLink.vue'
import ReceiptSocialIcons from '@/views/ereceipts/ReceiptSocialIcons.vue'
import ReceiptStoreCard from '@/views/ereceipts/ReceiptStoreCard.vue'
import ReceiptTerms from '@/views/ereceipts/ReceiptTerms.vue'
import ReceiptTxnHistoryButton from '@/views/ereceipts/ReceiptTxnHistoryButton.vue'
import ReceiptVideoEmbed from '@/views/ereceipts/ReceiptVideoEmbed.vue'
import ReceiptLoyaltyCard from '@/views/ereceipts/ReceiptLoyaltyCard.vue'
import ReceiptRecommendationCard from '@/views/ereceipts/ReceiptRecommendationCard.vue'
import ReceiptReferralCard from '@/views/ereceipts/ReceiptReferralCard.vue';
import draggable from "vuedraggable";
import axios from '@axios'
import ReceiptContactUsCard from '@/views/ereceipts/ReceiptContactUsCard.vue'
import ConfigCards from '@/views/ereceipts/ConfigCards.vue'
import { vElementSize } from '@vueuse/components'
import { useSharedStore } from '@/store';
const store = useSharedStore();
const availableHeight = ref(0);
const selectedHeight = ref(0);
const mappingIdToComponent = [{}]
mappingIdToComponent[0] = ReceiptBrandCard;
mappingIdToComponent[1] = ReceiptNpsForm;
mappingIdToComponent[2] = ReceiptOffersCarousel;
mappingIdToComponent[3] = ReceiptShopOnlineLink;
mappingIdToComponent[4] = ReceiptTxnHistoryButton;
mappingIdToComponent[5] = ReceiptCustomerCard;
mappingIdToComponent[6] = ReceiptItemsList;
mappingIdToComponent[7] = ReceiptTerms;
mappingIdToComponent[8] = ReceiptVideoEmbed;
mappingIdToComponent[9] = ReceiptGetInvoiceForm;
mappingIdToComponent[10] = ReceiptSocialIcons;
mappingIdToComponent[11] = ReceiptStoreCard;
mappingIdToComponent[12] = ReceiptLoyaltyCard;
mappingIdToComponent[13] = ReceiptContactUsCard;
mappingIdToComponent[14] = ReceiptRecommendationCard;
mappingIdToComponent[15] = ReceiptReferralCard;
mappingIdToComponent[16] = ReceiptPoweredBy;


let availableComponents = ref([
  { id: 1, name: markRaw(ReceiptNpsForm), configuration: {} },
  { id: 2, name: markRaw(ReceiptOffersCarousel), configuration: {} },
  { id: 3, name: markRaw(ReceiptShopOnlineLink), configuration: {} },
  { id: 4, name: markRaw(ReceiptTxnHistoryButton), configuration: {} },
  { id: 5, name: markRaw(ReceiptCustomerCard), configuration: {} },
  { id: 6, name: markRaw(ReceiptItemsList), configuration: {} },
  { id: 7, name: markRaw(ReceiptTerms), configuration: {} },
  { id: 8, name: markRaw(ReceiptVideoEmbed), configuration: {} },
  { id: 9, name: markRaw(ReceiptGetInvoiceForm), configuration: {} },
  { id: 10, name: markRaw(ReceiptSocialIcons), configuration: {} },
  { id: 11, name: markRaw(ReceiptStoreCard), configuration: {} },
  { id: 12, name: markRaw(ReceiptLoyaltyCard), configuration: {} },
  { id: 13, name: markRaw(ReceiptContactUsCard), configuration: {} },
  { id: 14, name: markRaw(ReceiptRecommendationCard), configuration: {} },
  { id: 15, name: markRaw(ReceiptReferralCard), configuration: {} },
]);

let selectedComponents = ref([]);

let savingComponents = ref([]);

function validation() {
  return store.ReceiptConfiguration.isValid.valid
};

// Saving "SELECTED COMPONENTS"
async function saveSelectedComponents() {
  const isValid = validation()
  if (isValid == 'true' || isValid == 'none') {
    savingComponents.value = []
    const brandObj = { id: 0, name: 'ReceiptBrandCard', configuration: {} };
    const existingBrandIndex = savingComponents.value.findIndex(obj => obj.name === 'ReceiptBrandCard');
    if (existingBrandIndex !== -1) {
      savingComponents.value.splice(existingBrandIndex, 1, brandObj);
    } else {
      savingComponents.value.unshift(brandObj);
    }

    for (let selComp of selectedComponents.value) {
      const comp = { id: selComp.id, name: selComp.name.__name, configuration: store.$state.ReceiptConfiguration[selComp.name.__name] };
      savingComponents.value.push(comp);
    }
    const poweredByObj = { id: 16, name: 'ReceiptPoweredBy', configuration: {} };
    const existingPoweredByIndex = savingComponents.value.findIndex(obj => obj.id === 16);
    if (existingPoweredByIndex !== -1) {
      savingComponents.value.splice(existingPoweredByIndex, 1, poweredByObj);
    } else {
      savingComponents.value.push(poweredByObj);
    }

    console.log("SAVING ==> ", savingComponents);

    try {
      const response = await axios.post('/api/ereceipt/save-components', savingComponents.value); // API - save components
      console.log("Response :: ", response);

      if (response.data === 'SUCCESS') {
        store.setSnackbar({
          content: "Saved Successfully!",
          color: "green",
          isVisible: true,
          icon: 'tabler-circle-check'
        });
      } else {
        store.setSnackbar({
          content: "Something Went Wrong!",
          color: "error",
          isVisible: true,
          icon: 'tabler-exclamation-circle'
        });
      }
    } catch (err) {
      console.log("Error :: ", err);
      store.setSnackbar({
        content: "Something Went Wrong!",
        color: "error",
        isVisible: true,
        icon: 'tabler-exclamation-circle'
      });
    }
  } else {
    store.setSnackbar({
      content: "Invalid Configuration Details",
      color: "error",
      isVisible: true,
      icon: 'tabler-exclamation-circle'
    });
  }
}


// Runs when page loads
onMounted(() => {
  initialPageload();
})

watch(
  selectedComponents,
  (newValue, oldValue) => {
    const height = Math.max(availableHeight.value, selectedHeight.value);
    const draggableAvailable = document.getElementById('draggableAvailable');
    if (draggableAvailable) {
      draggableAvailable.style.minHeight = height + 'px';
    }
    // Set height to the draggableSelected
    const draggableSelected = document.getElementById('draggableSelected');
    if (draggableSelected) {
      draggableSelected.style.minHeight = height + 'px';
    }
  },
  { immediate: true, deep: true }
);

watch(
  [availableHeight, selectedHeight],
  ([newAvailableHeight, newSelectedHeight], [oldAvailableHeight, oldSelectedHeight]) => {
    if (newAvailableHeight !== oldAvailableHeight || newSelectedHeight !== oldSelectedHeight) {
      const height = Math.max(newAvailableHeight, newSelectedHeight);

      // Set height for draggableAvailable div
      const draggableAvailable = document.getElementById('draggableAvailable');
      if (draggableAvailable) {
        draggableAvailable.style.minHeight = height + 'px';
      }

      // Set height for draggableSelected div
      const draggableSelected = document.getElementById('draggableSelected');
      if (draggableSelected) {
        draggableSelected.style.minHeight = height + 'px';
      }
    }
  },
  { immediate: true, deep: true }
);

function initialPageload() {
  axios.get('/api/ereceipt/get-components').then(response => {   // API - fetches components
    console.log("Fetched data :: ", response.data);
    const responseData = response.data;

    if (responseData.length > 0) {
      selectedComponents.value = []

      // Adding selected COMPONENTS in selectedComponents[]
      for (let selComp of responseData) {
        let component = { id: selComp.id, name: mappingIdToComponent[selComp.id], configuration: selComp.configuration };
        selectedComponents.value.push(component);
      }
      selectedComponents.value = selectedComponents.value.filter(component => component.id !== 0 && component.id !== 16);

      let tempAvailableCommponents = availableComponents.value;
      availableComponents.value = []

      // Adding non-selected COMPONENTS in availableComponents[]
      for (let availComp of tempAvailableCommponents) {
        let componentSelected = false;

        for (let selComp of selectedComponents.value) {
          if (availComp.name.__name == selComp.name.__name) {
            componentSelected = true;
          }
        }

        if (!componentSelected) {
          let comp = { id: availComp.id, name: mappingIdToComponent[availComp.id] };
          availableComponents.value.push(comp);
        }
      }
    }
  })
}
function onResizeAvailable({ width, height }: { width: number, height: number }) {
  availableHeight.value = height;
}

function onResizeSelected({ width, height }: { width: number, height: number }) {
  selectedHeight.value = height;
}



</script>

<style>
.contain {
  border: 1pt dashed #A8AAAE;
  min-height: 150px;
  padding: 0pt 5pt 0 5pt;
}
</style>

<template>
  <VRow>
    <VCol col="6">
      <VContainer style="max-width: 30rem;">
        <div class="col-3">
          <h3>Available components</h3>
          <draggable class="list-group mt-6 contain" :list="availableComponents" group="people" itemKey="id"
            v-element-size="onResizeAvailable" id="draggableAvailable">
            <template #item="{ element, index }">
              <component :is="element.name" />
            </template>
          </draggable>
        </div>
      </VContainer>
    </VCol>
    <VCol col="6">
      <VContainer style="max-width: 30rem;">
        <div class="col-3">
          <div>
            <h3>Ereceipt Preview
              <span style="float: right;">
                <VBtn color="primary" @click="saveSelectedComponents()">Save</VBtn>
              </span>
            </h3>
          </div>
        </div>
        <div class="col-3">
          <draggable class="list-group mt-6 contain" :list="selectedComponents" group="people" itemKey="id"
            id="draggableSelected" v-element-size="onResizeSelected">
            <template #header>
              <ReceiptBrandCard />
            </template>
            <template #item="{ element, index }">
              <ConfigCards :configuration="element.configuration" :selected="element.name" class="mb-1">
                <template #default="{ dialogVisible, openDialog, config }">
                  <component :is="element.name" v-if="element.id != 0 && element.id != 16" />
                </template>
              </ConfigCards>
            </template>
            <template #footer>
              <ReceiptPoweredBy />
            </template>
          </draggable>
        </div>
      </VContainer>
    </VCol>
  </VRow>
</template>
