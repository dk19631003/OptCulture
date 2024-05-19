<script setup lang="ts">
import { EreceiptType } from "@/@fake-db/types";

interface Props {
  data?: EreceiptType
}

const props = withDefaults(defineProps<Props>(), {
  data: {
    organization: {
      "webPortal": "N/A",
    }
  }
}
);

</script>

<template>
  <VCard class="my-2" v-if="data.organization.webPortal" elevation="0">
    <!-- <VRow>
      <VCol>
        <VCardText>
          <VListItemTitle>
            <div class="font-weight-medium w-100">
              Check our latest collection<br>
              Shop online on our website
            </div>
          </VListItemTitle>
        </VCardText>
      </VCol>
      <VCol class="my-6 w-100">
        <a :href="data.organization.webPortal" target="_blank">
          <VBtn variant="tonal" append-icon="tabler-shopping-cart">
            Shop Now
          </VBtn>
        </a>
      </VCol>
    </VRow> -->
    <div class="d-flex justify-space-between flex-wrap align-center">
      <div class="box1 wr-text-h1">
        <VListItemTitle>
          Check our latest collection<br>
          Shop online on our website
        </VListItemTitle>
      </div>
      <div class="box2">
        <a :href="data.organization.webPortal" target="_blank">
          <VBtn variant="tonal" append-icon="tabler-shopping-cart">
            Shop Now
          </VBtn>
        </a>
      </div>
    </div>

  </VCard>
</template>
<style scoped>
.box1 {
  text-align: center;
  padding: 5px;
  margin: 5px;
  width: 100%;
}

.box2 {
  text-align: center;
  padding: 5px;
  margin: 5px;
  width: 100%;
}

@media (min-width: 500px) {
  .box1 {
    width: 55%;
  }

  .box2 {
    width: 40%;
  }
}
</style>

